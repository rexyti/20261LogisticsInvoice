
import React from 'react';

const StatusBadge = ({ status }) => {
  const statusClass = `status-${status}`;
  return <span className={`status-badge ${statusClass}`}>{status}</span>;
};

const RutasTable = ({ rutas = [] }) => {
  if (rutas.length === 0) {
    return <p>No hay rutas para mostrar.</p>;
  }

  return (
    <table>
      <thead>
        <tr>
          <th>ID Ruta</th>
          <th>Tipo Vehículo</th>
          <th>Transportista</th>
          <th>Estado</th>
          <th>Fecha Cierre</th>
        </tr>
      </thead>
      <tbody>
        {rutas.map((ruta) => (
          <tr key={ruta.ruta_id}>
            <td title={ruta.ruta_id}>{ruta.ruta_id?.slice(0, 8)}…</td>
            <td>{ruta.tipo_vehiculo}</td>
            <td>{ruta.transportista?.nombre}</td>
            <td><StatusBadge status={ruta.estado_procesamiento} /></td>
            <td>{ruta.fecha_cierre ? new Date(ruta.fecha_cierre).toLocaleString('es-CO') : '—'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default RutasTable;
