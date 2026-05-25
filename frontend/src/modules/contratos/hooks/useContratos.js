import { useState, useEffect, useCallback } from 'react';
import { contratoService } from '../services/contratoService';

export const useContratos = () => {
  const [contratos, setContratos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);

  const fetchContratos = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await contratoService.getContratos(page);
      setContratos(response.data.content ?? []);
      setTotalPaginas(response.data.totalPages ?? 0);
    } catch (err) {
      setError('Error al cargar los contratos.');
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    fetchContratos();
  }, [fetchContratos]);

  return { contratos, loading, error, page, totalPaginas, setPage, retry: fetchContratos };
};
