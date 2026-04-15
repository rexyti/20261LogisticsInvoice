package com.logistica.application.dtos.request;

import lombok.Data;
import java.util.UUID;

@Data
public class PaqueteDTO {
    private UUID id;
    private String estadoFinal;
    private String novedades;
}
