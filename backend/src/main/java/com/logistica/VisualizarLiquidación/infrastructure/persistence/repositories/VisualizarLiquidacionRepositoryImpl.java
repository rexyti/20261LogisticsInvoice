package com.logistica.VisualizarLiquidación.infrastructure.persistence.repositories;

import com.logistica.VisualizarLiquidación.domain.models.Liquidacion;
import com.logistica.VisualizarLiquidación.domain.models.ResultadoBusquedaPorRuta;
import com.logistica.VisualizarLiquidación.domain.repositories.LiquidacionRepository;
import com.logistica.VisualizarLiquidación.infrastructure.adapters.VisualizarLiquidacionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class VisualizarLiquidacionRepositoryImpl implements LiquidacionRepository {

    private final VisualizarLiquidacionJpaRepository jpaRepository;
    private final VisualizarLiquidacionRutaJpaRepository rutaJpaRepository;
    private final VisualizarLiquidacionMapper mapper;

    public VisualizarLiquidacionRepositoryImpl(VisualizarLiquidacionJpaRepository jpaRepository,
                                     VisualizarLiquidacionRutaJpaRepository rutaJpaRepository,
                                     VisualizarLiquidacionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.rutaJpaRepository = rutaJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<Liquidacion> listarTodas(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Liquidacion> listarPorUsuario(String usuarioId, Pageable pageable) {
        return jpaRepository.findByUsuarioId(usuarioId, pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<Liquidacion> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public ResultadoBusquedaPorRuta buscarPorIdRuta(UUID idRuta) {
        return jpaRepository.findByRuta_Id(idRuta)
                .<ResultadoBusquedaPorRuta>map(entity ->
                        new ResultadoBusquedaPorRuta.Encontrada(mapper.toDomain(entity)))
                .orElseGet(() -> rutaJpaRepository.existsById(idRuta)
                        ? new ResultadoBusquedaPorRuta.RutaSinLiquidacion()
                        : new ResultadoBusquedaPorRuta.RutaNoExiste());
    }
}
