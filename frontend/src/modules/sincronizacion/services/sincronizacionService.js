import api from '../../../api/axiosConfig';

const sincronizarPaquete = (idRuta, idPaquete) =>
  api.post(`/api/rutas/${idRuta}/paquetes/${idPaquete}/sincronizar`);

const getHistorialPaquete = (idPaquete, page = 0, size = 50) =>
  api.get(`/api/paquetes/${idPaquete}/historial`, { params: { page, size } });

const getLogs = (page = 0, size = 50) =>
  api.get('/api/sincronizacion/logs', { params: { page, size } });

const getLogsPorPaquete = (idPaquete, page = 0, size = 50) =>
  api.get(`/api/sincronizacion/logs/paquetes/${idPaquete}`, { params: { page, size } });

export const sincronizacionService = {
  sincronizarPaquete,
  getHistorialPaquete,
  getLogs,
  getLogsPorPaquete,
};
