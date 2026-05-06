package com.logistica.infrastructure.novedadEstadoPaquete.http.clients;

import com.logistica.infrastructure.novedadEstadoPaquete.http.dto.GestionPaqueteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gestion-paquetes", url = "${package.api.url:http://localhost:9090}")
public interface GestionClient {

    @GetMapping("/route/{idRoute}/package/{idPaquete}")
    GestionPaqueteDTO getEstadoPaquete(
            @PathVariable("idRoute")   Long idRoute,
            @PathVariable("idPaquete") Long idPaquete
    );
}
