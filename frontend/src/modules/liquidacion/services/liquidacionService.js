import api from '../../../api/axiosConfig';

const getLiquidaciones = (page = 0, size = 10, sortBy = 'fechaCalculo', sortDir = 'desc') => {
  return api.get('/api/liquidaciones', { params: { page, size, sortBy, sortDir } });
};

const getLiquidacionById = (id) => {
  return api.get(`/api/liquidaciones/${id}`);
};

export const liquidacionService = { getLiquidaciones, getLiquidacionById };
