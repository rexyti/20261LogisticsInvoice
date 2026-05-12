
import axios from 'axios';

const API_URL = '/api/v1/rutas';

const getRutas = (page = 0, size = 20, estado = null) => {
  const params = {
    page,
    size,
    sort: 'fechaCierre,desc',
  };
  if (estado) {
    params.estado = estado;
  }
  return axios.get(API_URL, { params });
};

const getRutaById = (id) => {
  return axios.get(`${API_URL}/${id}`);
};

export const rutaService = {
  getRutas,
  getRutaById,
};
