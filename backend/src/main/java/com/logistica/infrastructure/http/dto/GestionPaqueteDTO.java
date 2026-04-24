package com.logistica.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GestionPaqueteDTO(
        @JsonProperty("id_paquete") String idPaquete,
        @JsonProperty("estado")     String estado
) {}
