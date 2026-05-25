import { useState, useEffect, useCallback } from 'react';
import { liquidacionService } from '../services/liquidacionService';

const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

export const useLiquidaciones = () => {
  const [todasLiquidaciones, setTodasLiquidaciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [busqueda, setBusqueda] = useState('');
  const [filtroEstado, setFiltroEstado] = useState('');
  const [filtroTipoAjuste, setFiltroTipoAjuste] = useState('');
  const [resultadoApi, setResultadoApi] = useState(null); // null = usar filtro local

  const fetchLiquidaciones = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await liquidacionService.getLiquidaciones(page);
      setTodasLiquidaciones(response.data.contenido);
      setTotalPaginas(response.data.total_paginas);
    } catch (err) {
      setError('Error al cargar las liquidaciones.');
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    fetchLiquidaciones();
  }, [fetchLiquidaciones]);

  // Cuando la búsqueda es un UUID completo, consulta la API directamente
  useEffect(() => {
    if (!UUID_REGEX.test(busqueda)) {
      setResultadoApi(null);
      return;
    }
    let activo = true;
    const buscarEnApi = async () => {
      try {
        let res;
        try {
          res = await liquidacionService.buscarLiquidacion({ id_liquidacion: busqueda });
        } catch {
          res = await liquidacionService.buscarLiquidacion({ id_ruta: busqueda });
        }
        if (activo) setResultadoApi([{ ...res.data, conductor_nombre: res.data.usuario_id }]);
      } catch {
        if (activo) setResultadoApi([]);
      }
    };
    buscarEnApi();
    return () => { activo = false; };
  }, [busqueda]);

  const liquidaciones = resultadoApi !== null
    ? resultadoApi
    : todasLiquidaciones.filter((liq) => {
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
