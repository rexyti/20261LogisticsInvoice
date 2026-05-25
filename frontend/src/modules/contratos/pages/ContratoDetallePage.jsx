import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { contratoService } from '../services/contratoService';
import './Contratos.css';

const formatDate = (dt) =>
  dt ? new Date(dt).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' }) : '—';

const formatCurrency = (value) =>
  value != null ? `$${Number(value).toLocaleString('es-CO', { minimumFractionDigits: 2 })}` : '—';

const BackIcon = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
    <polyline points="15 18 9 12 15 6" />
  </svg>
);

const Field = ({ label, value }) => (
  <div className="con-detail-field">
    <span className="con-detail-label">{label}</span>
    <span className="con-detail-value">{value ?? '—'}</span>
  </div>
);

const ContratoDetallePage = () => {
  const { idContrato } = useParams();
  const navigate = useNavigate();
  const [contrato, setContrato] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    contratoService.getContratoById(idContrato)
      .then((res) => setContrato(res.data))
      .catch(() => setError('No se pudo cargar el contrato.'))
      .finally(() => setLoading(false));
  }, [idContrato]);

  if (loading) return <p style={{ color: '#9CA3AF', padding: '2rem 0' }}>Cargando...</p>;

  if (error) return (
    <div style={{ padding: '2rem 0' }}>
      <p style={{ color: '#EF4444', marginBottom: '0.75rem' }}>{error}</p>
      <button className="con-back-btn" onClick={() => navigate('/contratos')}>
        <BackIcon /> Volver
      </button>
    </div>
  );

  if (!contrato) return null;

  const seguroColor = contrato.seguro?.estado === 'ACTIVO'
    ? { bg: 'rgba(16,185,129,0.15)', color: '#10B981' }
    : { bg: 'rgba(239,68,68,0.15)', color: '#EF4444' };

  return (
    <div>
      <div className="con-detail-header">
        <div className="con-detail-header-left">
          <button className="con-back-btn" onClick={() => navigate('/contratos')}>
            <BackIcon /> Volver
          </button>
          <div className="con-detail-title">
            <h1>Detalle de Contrato</h1>
            <p>{contrato.id_contrato}</p>
          </div>
        </div>
        <span className="con-badge" style={{ background: 'rgba(96,165,250,0.15)', color: '#60A5FA', padding: '0.35rem 1rem' }}>
          {contrato.tipo_contrato}
        </span>
      </div>

      <div className="con-detail-grid">
        <div className="con-detail-card">
          <p className="con-detail-card-title">Transportista</p>
          <Field label="Nombre" value={contrato.transportista?.nombre} />
          <Field label="ID Transportista" value={contrato.transportista?.transportista_id} />
          <Field label="Tipo Vehículo" value={contrato.tipo_vehiculo} />
        </div>

        <div className="con-detail-card">
          <p className="con-detail-card-title">Condiciones económicas</p>
          <Field label="Por parada" value={contrato.es_por_parada ? 'Sí' : 'No'} />
          <Field
            label="Precio por Parada"
            value={contrato.es_por_parada ? formatCurrency(contrato.precio_paradas) : '—'}
          />
          <Field
            label="Precio Total"
            value={!contrato.es_por_parada ? formatCurrency(contrato.precio) : '—'}
          />
        </div>

        <div className="con-detail-card">
          <p className="con-detail-card-title">Vigencia</p>
          <Field label="Fecha Inicio" value={formatDate(contrato.fecha_inicio)} />
          <Field label="Fecha Final" value={formatDate(contrato.fecha_final)} />
        </div>

        <div className="con-detail-card">
          <p className="con-detail-card-title">Seguro</p>
          <Field label="N° Póliza" value={contrato.seguro?.numero_poliza} />
          <Field
            label="Estado"
            value={
              <span className="con-badge" style={{ backgroundColor: seguroColor.bg, color: seguroColor.color }}>
                {contrato.seguro?.estado ?? '—'}
              </span>
            }
          />
        </div>
      </div>
    </div>
  );
};

export default ContratoDetallePage;
