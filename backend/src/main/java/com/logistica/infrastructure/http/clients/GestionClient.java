package com.logistica.infrastructure.http.clients;

import com.logistica.infrastructure.http.dto.GestionPaqueteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gestionClient", url = "${package-api.base-url}")
public interface GestionClient {

    @GetMapping("/route/{idRoute}/package/{idPaquete}")
    GestionPaqueteDTO getPackageState(
            @PathVariable("idRoute") String idRoute,
            @PathVariable("idPaquete") String idPaquete
    );
}
