import { useState, useEffect, useCallback } from 'react';
import { pagoService } from '../services/pagoService';

export const usePagos = () => {
  const [pagos, setPagos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchPagos = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await pagoService.getPagos();
      setPagos(response.data ?? []);
    } catch (err) {
      setError(err.response?.data?.message ?? 'Error al cargar los pagos');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchPagos();
  }, [fetchPagos]);

  return { pagos, loading, error, retry: fetchPagos };
};
