import api from '../../../api/axiosConfig';

const getPagos = () => {
  return api.get('/api/pagos');
};

const getPagoById = (id) => {
  return api.get(`/api/pagos/${id}`);
};

const registrarEstadoPago = (payload) => {
  return api.post('/api/v1/pagos/webhook/estado', payload);
};

export const pagoService = { getPagos, getPagoById, registrarEstadoPago };
