package com.logistica.application.novedadEstadoPaquete.usecases.paquete;

import com.logistica.application.novedadEstadoPaquete.dtos.request.NovedadEstadoPaqueteEvent;
import com.logistica.application.novedadEstadoPaquete.ports.in.ProcesarEstadoPaquetePort;
import com.logistica.domain.novedadEstadoPaquete.enums.NovedadEstadoPaqueteEstadoPaquete;
import com.logistica.domain.novedadEstadoPaquete.models.HistorialEstado;
import com.logistica.domain.novedadEstadoPaquete.models.NovedadEstadoPaquetePaquete;
import com.logistica.domain.novedadEstadoPaquete.repositories.HistorialRepository;
import com.logistica.domain.novedadEstadoPaquete.repositories.PaqueteRepository;
import com.logistica.domain.novedadEstadoPaquete.services.EstadoPaqueteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcesarNovedadEstadoPaqueteUseCase implements ProcesarEstadoPaquetePort {

    private final PaqueteRepository paqueteRepository;
    private final HistorialRepository historialRepository;
    private final EstadoPaqueteService estadoPaqueteService;

    @Transactional
    public void ejecutar(NovedadEstadoPaqueteEvent evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento NOVEDAD_ESTADO_PAQUETE no puede ser null");
        }

        UUID idPaquete = evento.getIdPaquete();
        UUID idRuta = evento.getIdRuta();

        log.info("Procesando evento NOVEDAD_ESTADO_PAQUETE para paquete: {}", idPaquete);

        Optional<NovedadEstadoPaqueteEstadoPaquete> estadoOpt = estadoPaqueteService.resolverEstado(evento.getEstado());
        if (estadoOpt.isEmpty()) {
            log.error("Estado no mapeado '{}' para idPaquete={}", evento.getEstado(), idPaquete);
            throw new IllegalArgumentException("Estado no reconocido: " + evento.getEstado());
        }

        NovedadEstadoPaqueteEstadoPaquete estado = estadoOpt.get();

        NovedadEstadoPaquetePaquete paquete = paqueteRepository.findByIdPaquete(idPaquete)
                .orElse(new NovedadEstadoPaquetePaquete(idPaquete, idRuta, null, null));
        paquete.setIdRuta(idRuta);
        paquete.setEstadoActual(estado.name());
        paquete.setUpdatedAt(LocalDateTime.now());
        paqueteRepository.save(paquete);

        historialRepository.save(new HistorialEstado(null, idPaquete, estado.name(), LocalDateTime.now()));

        log.info("Paquete {} actualizado a estado {} desde evento SQS", idPaquete, estado.name());
    }
}
