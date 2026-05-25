import { useState, useEffect, useCallback } from 'react';
import { rutaService } from '../services/rutaService';

export const useRutaDetalle = (id) => {
  const [ruta, setRuta] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchRuta = useCallback(async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const response = await rutaService.getRutaById(id);
      setRuta(response.data);
    } catch (err) {
      setError(err.response?.data?.message ?? 'Error al cargar la ruta');
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchRuta();
  }, [fetchRuta]);

  return { ruta, loading, error, retry: fetchRuta };
};
