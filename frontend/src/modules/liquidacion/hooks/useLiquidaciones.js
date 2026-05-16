import { useState, useEffect, useCallback } from 'react';
import { liquidacionService } from '../services/liquidacionService';

export const useLiquidaciones = () => {
  const [todasLiquidaciones, setTodasLiquidaciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [busqueda, setBusqueda] = useState('');
  const [filtroEstado, setFiltroEstado] = useState('');
  const [filtroTipoAjuste, setFiltroTipoAjuste] = useState('');

  const fetchLiquidaciones = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await liquidacionService.getLiquidaciones(page);
      setTodasLiquidaciones(response.data.contenido);
      setTotalPaginas(response.data.total_paginas);
    } catch (err) {
      setError('Error al cargar las liquidaciones.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    fetchLiquidaciones();
  }, [fetchLiquidaciones]);

  const liquidaciones = todasLiquidaciones.filter((liq) => {
    const texto = busqueda.toLowerCase();
    const coincideBusqueda =
      !busqueda ||
      liq.id_ruta?.toString().toLowerCase().includes(texto) ||
      liq.id_liquidacion?.toString().toLowerCase().includes(texto) ||
      liq.conductor_nombre?.toLowerCase().includes(texto);
    const coincideEstado = !filtroEstado || liq.estado_liquidacion === filtroEstado;
    const coincideTipoAjuste =
      !filtroTipoAjuste ||
      (liq.ajustes ?? []).some((a) => a.tipo === filtroTipoAjuste);
    return coincideBusqueda && coincideEstado && coincideTipoAjuste;
  });

  return {
    liquidaciones,
    todasLiquidaciones,
    loading,
    error,
    page,
    totalPaginas,
    setPage,
    retry: fetchLiquidaciones,
    busqueda,
    setBusqueda,
    filtroEstado,
    setFiltroEstado,
    filtroTipoAjuste,
    setFiltroTipoAjuste,
  };
};
