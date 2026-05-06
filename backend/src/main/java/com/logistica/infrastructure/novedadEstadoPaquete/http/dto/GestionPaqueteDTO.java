package com.logistica.infrastructure.novedadEstadoPaquete.http.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GestionPaqueteDTO(
        @JsonProperty("id_paquete")
        @JsonAlias({"idPaquete", "idPackage"})
        String idPaquete,

        @JsonProperty("estado")
        String estado
) {}
