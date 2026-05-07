
import { useState, useEffect, useCallback } from 'react';
import { rutaService } from '../services/rutaService';

export const useRutas = () => {
  const [rutas, setRutas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [filter, setFilter] = useState(null);

  const fetchRutas = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await rutaService.getRutas(page, 20, filter);
      setRutas(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      setError('Error al cargar las rutas.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [page, filter]);

  useEffect(() => {
    fetchRutas();
  }, [fetchRutas]);

  return {
    rutas,
    loading,
    error,
    page,
    totalPages,
    setPage,
    setFilter,
    retry: fetchRutas,
  };
};
