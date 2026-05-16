import api from '../../../api/axiosConfig';
import axios from 'axios';

const getPagos = () => {
  return api.get('/api/pagos');
};

const getPagoById = (id) => {
  return api.get(`/api/pagos/${id}`);
};

const registrarEstadoPago = (payload) => {
  return axios.post('/api/v1/pagos/webhook/estado', payload);
};

export const pagoService = { getPagos, getPagoById, registrarEstadoPago };
