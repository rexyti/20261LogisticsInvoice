package com.logistica.infrastructure.liquidacion.handlers;

import com.logistica.application.liquidacion.usecases.LiquidacionCalcularUseCase;
import com.logistica.domain.cierreRuta.enums.EstadoProcesamiento;
import com.logistica.domain.cierreRuta.enums.MotivoFalla;
import com.logistica.domain.cierreRuta.enums.ResponsableFalla;
import com.logistica.domain.cierreRuta.events.RutaCerradaProcesadaEvent;
import com.logistica.domain.cierreRuta.models.Parada;
import com.logistica.domain.cierreRuta.models.RutaCerrada;
import com.logistica.domain.cierreRuta.repositories.RutaRepository;
import com.logistica.domain.conductor.enums.EstadoConductor;
import com.logistica.domain.conductor.exceptions.ConductorNoEncontradoException;
import com.logistica.domain.conductor.models.Conductor;
import com.logistica.domain.conductor.ports.ConductorGateway;
import com.logistica.domain.contratos.models.Contrato;
import com.logistica.domain.contratos.repositories.ContratoRepository;
import com.logistica.domain.liquidacion.enums.EstadoPaquete;
import com.logistica.domain.liquidacion.enums.TipoContratacion;
import com.logistica.domain.liquidacion.models.ContratoTarifa;
import com.logistica.domain.liquidacion.models.Paquete;
import com.logistica.domain.liquidacion.models.RutaLiquidacion;
import com.logistica.domain.liquidacion.repositories.ContratoTarifaRepository;
import com.logistica.domain.novedadEstadoPaquete.models.NovedadEstadoPaquetePaquete;
import com.logistica.domain.novedadEstadoPaquete.repositories.PaqueteRepository;
import com.logistica.infrastructure.shared.config.TarifaMockProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiquidacionEventHandler {

    private final LiquidacionCalcularUseCase liquidacionCalcularUseCase;
    private final RutaRepository rutaRepository;
    private final ContratoRepository contratoRepository;
    private final ContratoTarifaRepository contratoTarifaRepository;
    private final PaqueteRepository paqueteRepository;
    private final TarifaMockProperties tarifaMockProperties;
    private final ConductorGateway conductorGateway;

    @EventListener
    public void handle(RutaCerradaProcesadaEvent event) {

        if (event.getEstadoProcesamiento() != EstadoProcesamiento.OK) {
            log.warn("Ruta {} requiere revisión, se omite liquidación",
                    event.getRutaId());
            return;
        }

        UUID rutaId = event.getRutaId();
        log.info("Iniciando liquidación para ruta {}", rutaId);

        // 1. Obtener ruta completa
        RutaCerrada ruta = rutaRepository.buscarPorRutaId(rutaId)
                .orElseThrow(() -> new IllegalStateException(
                        "Ruta no encontrada: " + rutaId));

        //1.5 Validar conductor
        UUID transportistaId = ruta.getTransportista().getTransportistaId();
        try {
            Conductor conductor = conductorGateway.consultar(transportistaId);
            if (conductor.getEstado() != EstadoConductor.ACTIVO) {
                log.warn("Conductor {} inactivo, se omite liquidación", transportistaId);
                return;
            }
            log.info("Conductor {} validado correctamente", transportistaId);
        } catch (ConductorNoEncontradoException e) {
            log.error("Conductor {} no encontrado, se omite liquidación", transportistaId);
            return;
        }

        // 2. Resolver contrato (real o mock)
        ContratoTarifa contratoTarifa = resolverContratoTarifa(
                transportistaId,
                ruta.getModeloContrato()
        );

        // 3. Obtener estados finales del módulo de paquetes (fuente de verdad)
        Map<UUID, NovedadEstadoPaquetePaquete> estadosPorPaquete =
                paqueteRepository.findAllByIdRuta(rutaId).stream()
                        .collect(Collectors.toMap(
                                NovedadEstadoPaquetePaquete::getIdPaquete,
                                p -> p
                        ));

        // 4. Cruzar paradas del cierre de ruta con estados del módulo de paquetes
        List<Paquete> paquetes = ruta.getParadas().stream()
                .map(parada -> {
                    UUID idPaquete = parada.getPaqueteId();
                    EstadoPaquete estado;

                    if (estadosPorPaquete.containsKey(idPaquete)) {
                        // Fuente de verdad: módulo de paquetes
                        String estadoReal = estadosPorPaquete.get(idPaquete)
                                .getEstadoActual();
                        estado = mapearEstado(estadoReal);
                        log.info("Paquete {} → estado módulo paquetes: {}",
                                idPaquete, estadoReal);
                    } else {
                        // Fallback: estado reportado por el cierre de ruta
                        estado = mapearEstadoParada(parada);
                        log.warn("Paquete {} sin estado en módulo paquetes, " +
                                "usando cierre de ruta: {}", idPaquete, parada.getEstado());
                    }

                    return Paquete.builder()
                            .id(idPaquete)
                            .estadoFinal(estado)
                            .build();
                })
                .toList();

        if (paquetes.isEmpty()) {
            log.error("Sin paquetes para ruta {}, se omite liquidación", rutaId);
            return;
        }

        // 5. Construir RutaLiquidacion
        RutaLiquidacion rutaLiquidacion = RutaLiquidacion.builder()
                .id(rutaId)
                .fechaInicio(ruta.getFechaInicioTransito().atOffset(ZoneOffset.UTC))
                .fechaCierre(ruta.getFechaCierre().atOffset(ZoneOffset.UTC))
                .paquetes(paquetes)
                .build();

        // 6. Calcular y guardar liquidación
        liquidacionCalcularUseCase.execute(rutaLiquidacion, contratoTarifa.getId());
        log.info("Liquidación creada exitosamente para ruta {}", rutaId);
    }

    // =============================================
    // RESOLVER CONTRATO: real o mock
    // =============================================
    private ContratoTarifa resolverContratoTarifa(UUID transportistaId,
                                                  String modeloContrato) {
        Optional<Contrato> contratoReal =
                contratoRepository.buscarActivoPorTransportistaId(transportistaId);

        if (contratoReal.isPresent()) {
            Contrato contrato = contratoReal.get();
            log.info("Usando contrato real {} para transportista {}",
                    contrato.getId(), transportistaId);

            return contratoTarifaRepository.findById(contrato.getId())
                    .orElseGet(() -> {
                        log.info("Sincronizando ContratoTarifa para contrato {}",
                                contrato.getId());
                        return contratoTarifaRepository.save(
                                ContratoTarifa.builder()
                                        .id(contrato.getId())
                                        .tipoContratacion(contrato.getEsPorParada()
                                                ? TipoContratacion.POR_PARADA
                                                : TipoContratacion.RECORRIDO_COMPLETO)
                                        .tarifa(contrato.getEsPorParada()
                                                ? contrato.getPrecioParadas()
                                                : contrato.getPrecio())
                                        .build()
                        );
                    });
        }

        log.warn("[MOCK] Contrato no encontrado para transportista {}, " +
                "usando mock con modeloContrato={}", transportistaId, modeloContrato);

        TipoContratacion tipo = parsearTipo(modeloContrato);
        BigDecimal tarifa = tipo == TipoContratacion.POR_PARADA
                ? tarifaMockProperties.getPorParada()
                : tarifaMockProperties.getRecorridoCompleto();

        return contratoTarifaRepository.save(
                ContratoTarifa.builder()
                        .id(UUID.randomUUID())
                        .tipoContratacion(tipo)
                        .tarifa(tarifa)
                        .build()
        );
    }

    // =============================================
    // MAPEO DESDE MÓDULO DE PAQUETES (fuente de verdad)
    // =============================================
    private EstadoPaquete mapearEstado(String estadoRaw) {
        return switch (estadoRaw) {
            case "ENTREGADO"            -> EstadoPaquete.ENTREGADO;
            case "DEVUELTO"             -> EstadoPaquete.FALLIDO_CLIENTE;
            case "DANADO", "EXTRAVIADO" -> EstadoPaquete.FALLIDO_TRANSPORTISTA;
            default -> throw new IllegalArgumentException(
                    "Estado de paquete no reconocido: " + estadoRaw);
        };
    }

    // =============================================
    // FALLBACK: MAPEO DESDE CIERRE DE RUTA
    // =============================================
    private EstadoPaquete mapearEstadoParada(Parada parada) {
        return switch (parada.getEstado()) {
            case EXITOSA -> EstadoPaquete.ENTREGADO;
            case FALLIDA -> {
                ResponsableFalla responsable = parada.getResponsable();
                yield switch (responsable) {
                    case CLIENTE        -> EstadoPaquete.FALLIDO_CLIENTE;      // CLIENTE_AUSENTE, DIRECCION_ERRONEA, RECHAZADO
                    case TRANSPORTISTA  -> EstadoPaquete.FALLIDO_TRANSPORTISTA; // ZONA_DIFICIL_ACCESO
                    case EMPRESA        -> EstadoPaquete.FALLIDO_TRANSPORTISTA;
                };
            }
            case NOVEDAD -> {
                MotivoFalla motivo = parada.getMotivoFalla();
                if (motivo == null) yield EstadoPaquete.FALLIDO_TRANSPORTISTA;
                yield switch (motivo) {
                    case DEVOLUCION      -> EstadoPaquete.FALLIDO_CLIENTE;       // pago parcial 30-50%
                    case PAQUETE_DANADO,
                         PERDIDA_PAQUETE -> EstadoPaquete.FALLIDO_TRANSPORTISTA; // sin pago, seguro responde en DANADO
                    default              -> EstadoPaquete.FALLIDO_TRANSPORTISTA;
                };
            }
            // PENDIENTE, SIN_GESTION_CONDUCTOR, EXCLUIDA_DESPACHO
            default -> EstadoPaquete.FALLIDO_TRANSPORTISTA;
        };
    }

    // =============================================
    // PARSEO DEL MODELO DE CONTRATO
    // =============================================
    private TipoContratacion parsearTipo(String modeloContrato) {
        try {
            return TipoContratacion.valueOf(modeloContrato.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            log.error("Modelo de contrato no reconocido: {}, usando POR_PARADA por defecto",
                    modeloContrato);
            return TipoContratacion.POR_PARADA;
        }
    }
}