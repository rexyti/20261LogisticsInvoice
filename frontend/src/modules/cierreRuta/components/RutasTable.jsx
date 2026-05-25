
import React from 'react';
import { useNavigate } from 'react-router-dom';

const ESTADO_STYLES = {
  PROCESADO: { bg: 'rgba(16,185,129,0.15)',  color: '#10B981' },
  PENDIENTE: { bg: 'rgba(245,158,11,0.15)',  color: '#F59E0B' },
  ERROR:     { bg: 'rgba(239,68,68,0.15)',   color: '#EF4444' },
};

const StatusBadge = ({ status }) => {
  const style = ESTADO_STYLES[status] ?? { bg: 'rgba(107,114,128,0.15)', color: '#9CA3AF' };
  return (
    <span style={{
      padding: '0.25rem 0.75rem',
      borderRadius: '9999px',
      fontWeight: 600,
      fontSize: '0.75rem',
      display: 'inline-block',
      backgroundColor: style.bg,
      color: style.color,
    }}>
      {status}
    </span>
  );
};

const EyeIcon = () => (
  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
    <circle cx="12" cy="12" r="3" />
  </svg>
);

const RutasTable = ({ rutas = [] }) => {
  const navigate = useNavigate();

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
          <th>ID Paquetes</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        {rutas.map((ruta) => {
          const paradas = ruta.paradas ?? [];
          return (
          <tr key={ruta.ruta_id}>
            <td title={ruta.ruta_id}>{ruta.ruta_id?.slice(0, 8)}…</td>
            <td>{ruta.tipo_vehiculo}</td>
            <td>{ruta.transportista?.nombre}</td>
            <td><StatusBadge status={ruta.estado_procesamiento} /></td>
            <td>{ruta.fecha_cierre ? new Date(ruta.fecha_cierre).toLocaleString('es-CO') : '—'}</td>
            <td>
              {paradas.length === 0 ? (
                <span style={{ color: '#6B7280', fontSize: '0.8rem' }}>—</span>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.2rem' }}>
                  {paradas.map((p) => (
                    <span
                      key={p.parada_id}
                      title={p.paquete_id}
                      style={{
                        fontFamily: 'monospace', fontSize: '0.78rem',
                        color: '#9CA3AF', cursor: 'default',
                      }}
                    >
                      {p.paquete_id?.slice(0, 8)}…
                    </span>
                  ))}
                </div>
              )}
            </td>
            <td>
              <button
                onClick={() => navigate(`/rutas/${ruta.ruta_id}`)}
                title="Ver detalle"
                style={{
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  background: '#374151', color: '#9CA3AF',
                  border: '1px solid #4B5563', borderRadius: '0.375rem',
                  padding: '0.375rem', cursor: 'pointer',
                }}
              >
                <EyeIcon />
              </button>
            </td>
          </tr>
          );
        })}
      </tbody>
    </table>
  );
};

export default RutasTable;
