import api from '../../../api/axiosConfig';

const getContratos = (page = 0, size = 20) =>
  api.get('/api/contratos', { params: { page, size } });

const getContratoById = (idContrato) =>
  api.get(`/api/contratos/${idContrato}`);

const crearContrato = (payload) =>
  api.post('/api/contratos', payload);

export const contratoService = { getContratos, getContratoById, crearContrato };
