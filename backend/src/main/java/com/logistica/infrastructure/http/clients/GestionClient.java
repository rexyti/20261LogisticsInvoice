package com.logistica.infrastructure.http.clients;

import com.logistica.infrastructure.http.dto.GestionPaqueteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gestion-paquetes", url = "${package.api.url}")
public interface GestionClient {

    @GetMapping("/route/{idRoute}/package/{idPaquete}")
    GestionPaqueteDTO getEstadoPaquete(
            @PathVariable("idRoute")   Long idRoute,
            @PathVariable("idPaquete") Long idPaquete
    );
}
