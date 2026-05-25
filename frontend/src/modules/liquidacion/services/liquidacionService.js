import api from '../../../api/axiosConfig';

const getLiquidaciones = (page = 0, size = 10, sortBy = 'fechaCalculo', sortDir = 'desc') => {
  return api.get('/api/liquidaciones', { params: { page, size, sortBy, sortDir } });
};

const getLiquidacionById = (id) => {
  return api.get(`/api/liquidaciones/${id}`);
};

const buscarLiquidacion = (params) => {
  return api.get('/api/liquidaciones/buscar', { params });
};

const recalcularLiquidacion = (id, payload) => {
  return api.put(`/api/liquidaciones/${id}/recalcular`, payload);
};

const getEstadoPagoPorLiquidacion = (id) => {
  return api.get(`/api/v1/liquidaciones/${id}/pago/estado`);
};

export const liquidacionService = {
  getLiquidaciones,
  getLiquidacionById,
  buscarLiquidacion,
  recalcularLiquidacion,
  getEstadoPagoPorLiquidacion,
};
