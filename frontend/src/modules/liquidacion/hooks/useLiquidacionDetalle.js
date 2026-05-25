import { useState, useEffect } from 'react';
import { liquidacionService } from '../services/liquidacionService';

export const useLiquidacionDetalle = (id) => {
  const [liquidacion, setLiquidacion] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDetalle = async () => {
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
  };

  useEffect(() => {
    fetchDetalle();
  }, [id]);

  return { liquidacion, loading, error, retry: fetchDetalle };
};
