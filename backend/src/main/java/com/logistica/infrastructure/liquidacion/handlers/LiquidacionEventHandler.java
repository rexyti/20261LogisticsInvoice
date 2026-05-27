package com.logistica.infrastructure.liquidacion.handlers;

import com.logistica.application.liquidacion.usecases.LiquidacionCalcularUseCase;
import com.logistica.domain.cierreRuta.enums.EstadoProcesamiento;
import com.logistica.domain.cierreRuta.events.RutaCerradaProcesadaEvent;
import com.logistica.domain.cierreRuta.models.RutaCerrada;
import com.logistica.domain.cierreRuta.repositories.RutaRepository;
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
import java.util.Optional;
import java.util.UUID;

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

    @EventListener
    public void handle(RutaCerradaProcesadaEvent event) {

        if (event.getEstadoProcesamiento() != EstadoProcesamiento.OK) {
            log.warn("Ruta {} requiere revisión, se omite liquidación",
                    event.getRutaId());
            return;
        }

        UUID rutaId = event.getRutaId();
        log.info("Iniciando liquidación para ruta {}", rutaId);


        RutaCerrada ruta = rutaRepository.buscarPorRutaId(rutaId)
                .orElseThrow(() -> new IllegalStateException(
                        "Ruta no encontrada: " + rutaId));

        UUID transportistaId = ruta.getTransportista().getTransportistaId();


        ContratoTarifa contratoTarifa = resolverContratoTarifa(
                transportistaId,
                ruta.getModeloContrato()
        );


        List<NovedadEstadoPaquetePaquete> paquetesBD =
                paqueteRepository.findAllByIdRuta(rutaId);

        if (paquetesBD.isEmpty()) {
            log.error("Sin paquetes para ruta {}, se omite liquidación", rutaId);
            return;
        }


        RutaLiquidacion rutaLiquidacion = RutaLiquidacion.builder()
                .id(rutaId)
                .fechaInicio(ruta.getFechaInicioTransito().atOffset(ZoneOffset.UTC))
                .fechaCierre(ruta.getFechaCierre().atOffset(ZoneOffset.UTC))
                .paquetes(paquetesBD.stream()
                        .map(p -> Paquete.builder()
                                .id(p.getIdPaquete())
                                .estadoFinal(mapearEstado(p.getEstadoActual()))
                                .build())
                        .toList())
                .build();


        liquidacionCalcularUseCase.execute(rutaLiquidacion, contratoTarifa.getId());
        log.info("Liquidación creada exitosamente para ruta {}", rutaId);
    }

    // =============================================
    // RESOLVER CONTRATO: real o mock
    // =============================================
    private ContratoTarifa resolverContratoTarifa(UUID transportistaId,
                                                  String modeloContrato) {
        // Buscar contrato real del profesor en BD
        Optional<Contrato> contratoReal =
                contratoRepository.buscarActivoPorTransportistaId(transportistaId);

        if (contratoReal.isPresent()) {
            Contrato contrato = contratoReal.get();
            log.info("Usando contrato real {} para transportista {}",
                    contrato.getId(), transportistaId);

            // Sincronizar ContratoTarifa si no existe
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

        // Contrato del profesor no disponible → usar mock con tarifa de properties
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

    private EstadoPaquete mapearEstado(String estadoRaw) {
        return switch (estadoRaw) {
            case "ENTREGADO"             -> EstadoPaquete.ENTREGADO;
            case "DEVUELTO"              -> EstadoPaquete.FALLIDO_CLIENTE;
            case "DANADO", "EXTRAVIADO"  -> EstadoPaquete.FALLIDO_TRANSPORTISTA;
            default -> throw new IllegalArgumentException(
                    "Estado de paquete no reconocido: " + estadoRaw);
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