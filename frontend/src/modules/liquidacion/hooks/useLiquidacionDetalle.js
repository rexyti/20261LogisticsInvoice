import { useState, useEffect, useCallback } from 'react';
import { liquidacionService } from '../services/liquidacionService';

export const useLiquidacionDetalle = (id) => {
  const [liquidacion, setLiquidacion] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDetalle = useCallback(async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const response = await liquidacionService.getLiquidacionById(id);
      setLiquidacion(response.data);
    } catch (err) {
      setError(err.response?.data?.message ?? 'Error al cargar la liquidación');
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchDetalle();
  }, [fetchDetalle]);

  return { liquidacion, loading, error, retry: fetchDetalle };
};
