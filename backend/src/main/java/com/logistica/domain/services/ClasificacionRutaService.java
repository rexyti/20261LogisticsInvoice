package com.logistica.domain.services;

import com.logistica.domain.enums.MotivoFalla;
import com.logistica.domain.enums.ResponsableFalla;
import org.springframework.stereotype.Service;

@Service
public class ClasificacionRutaService {

    public MotivoFalla parsearMotivo(String motivoNoEntrega) {
        return MotivoFalla.fromValue(motivoNoEntrega);
    }

    public ResponsableFalla clasificarResponsable(String motivoNoEntrega) {
        MotivoFalla motivo = MotivoFalla.fromValue(motivoNoEntrega);
        return motivo.getResponsable();
    }
}
