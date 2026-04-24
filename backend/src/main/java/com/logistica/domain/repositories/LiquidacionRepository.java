package com.logistica.domain.repositories;

import com.logistica.domain.models.Liquidacion;
import com.logistica.domain.models.ResultadoBusquedaPorRuta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface LiquidacionRepository {
    Page<Liquidacion> listarTodas(Pageable pageable);
    Page<Liquidacion> listarPorUsuario(String usuarioId, Pageable pageable);
    Optional<Liquidacion> buscarPorId(UUID id);
    ResultadoBusquedaPorRuta buscarPorIdRuta(UUID idRuta);
}