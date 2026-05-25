import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useRutaDetalle } from '../hooks/useRutaDetalle';
import './RutaDetalle.css';

const ESTADO_STYLES = {
  PENDIENTE:  { bg: 'rgba(245,158,11,0.15)',  color: '#F59E0B' },
  PROCESADO:  { bg: 'rgba(16,185,129,0.15)',  color: '#10B981' },
  ERROR:      { bg: 'rgba(239,68,68,0.15)',   color: '#EF4444' },
};

const PARADA_ESTADO_STYLES = {
  ENTREGADO:   { bg: 'rgba(16,185,129,0.15)',  color: '#10B981' },
  FALLIDO:     { bg: 'rgba(239,68,68,0.15)',   color: '#EF4444' },
  PENDIENTE:   { bg: 'rgba(245,158,11,0.15)',  color: '#F59E0B' },
};

const Badge = ({ valor, styles }) => {
  const style = styles[valor] ?? { bg: 'rgba(107,114,128,0.15)', color: '#9CA3AF' };
  return (
    <span className="rd-badge" style={{ backgroundColor: style.bg, color: style.color }}>
      {valor ?? '—'}
    </span>
  );
};

const formatDate = (dt) =>
  dt ? new Date(dt).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' }) : '—';

const Field = ({ label, value }) => (
  <div className="rd-field">
    <span className="rd-field-label">{label}</span>
    <span className="rd-field-value">{value ?? '—'}</span>
  </div>
);

const BackIcon = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
    <polyline points="15 18 9 12 15 6" />
  </svg>
);

const RutaDetallePage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { ruta, loading, error, retry } = useRutaDetalle(id);

  if (loading) {
    return <p style={{ color: '#9CA3AF', padding: '2rem 0' }}>Cargando...</p>;
  }

  if (error) {
    return (
      <div style={{ padding: '2rem 0' }}>
        <p style={{ color: '#EF4444', marginBottom: '0.75rem' }}>{error}</p>
        <button onClick={retry} style={{ background: '#374151', color: '#F9FAFB', border: '1px solid #4B5563', borderRadius: '0.5rem', padding: '0.5rem 1rem', cursor: 'pointer' }}>
          Reintentar
        </button>
      </div>
    );
  }

  if (!ruta) return null;

  const paradas = ruta.paradas ?? [];

  return (
    <div>
      <div className="rd-header">
        <div className="rd-header-left">
          <button className="rd-back-btn" onClick={() => navigate('/rutas')}>
            <BackIcon /> Volver
          </button>
          <div className="rd-title-block">
            <h1>Detalle de Ruta</h1>
            <p>{ruta.ruta_id}</p>
          </div>
        </div>
        <Badge valor={ruta.estado_procesamiento} styles={ESTADO_STYLES} />
      </div>

      <div className="rd-info-grid">
        <div className="rd-card">
          <p className="rd-card-title">Información del Vehículo</p>
          <Field label="Tipo de Vehículo" value={ruta.tipo_vehiculo} />
          <Field label="ID Vehículo" value={ruta.vehiculo_id} />
          <Field label="Modelo de Contrato" value={ruta.modelo_contrato} />
        </div>

        <div className="rd-card">
          <p className="rd-card-title">Transportista y Fechas</p>
          <Field label="Transportista" value={ruta.transportista?.nombre} />
          <Field label="ID Transportista" value={ruta.transportista?.transportista_id} />
          <Field label="Inicio Tránsito" value={formatDate(ruta.fecha_inicio_transito)} />
          <Field label="Fecha Cierre" value={formatDate(ruta.fecha_cierre)} />
        </div>
      </div>

      <div className="rd-paradas-card">
        <div className="rd-paradas-header">
          <p className="rd-paradas-title">Paradas</p>
          <span className="rd-paradas-count">{paradas.length} parada(s)</span>
        </div>
        {paradas.length === 0 ? (
          <p style={{ color: '#6B7280', fontSize: '0.875rem' }}>No hay paradas registradas.</p>
        ) : (
          <table className="rd-paradas-table">
            <thead>
              <tr>
                <th>ID Parada</th>
                <th>ID Paquete</th>
                <th>Estado</th>
                <th>Motivo Falla</th>
                <th>Responsable</th>
              </tr>
            </thead>
            <tbody>
              {paradas.map((p) => (
                <tr key={p.parada_id}>
                  <td title={p.parada_id}>{p.parada_id?.slice(0, 8)}…</td>
                  <td title={p.paquete_id}>{p.paquete_id?.slice(0, 8)}…</td>
                  <td><Badge valor={p.estado} styles={PARADA_ESTADO_STYLES} /></td>
                  <td style={{ fontFamily: 'sans-serif', color: '#9CA3AF' }}>{p.motivo_falla ?? '—'}</td>
                  <td style={{ fontFamily: 'sans-serif', color: '#9CA3AF' }}>{p.responsable ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default RutaDetallePage;
