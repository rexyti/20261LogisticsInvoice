import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useLiquidacionDetalle } from '../hooks/useLiquidacionDetalle';
import { liquidacionService } from '../services/liquidacionService';
import './LiquidacionDetalle.css';

const ESTADO_STYLES = {
  CALCULADA:   { bg: 'rgba(96,165,250,0.15)',  color: '#60A5FA' },
  RECALCULADA: { bg: 'rgba(20,184,166,0.15)',  color: '#14B8A6' },
  PENDIENTE:   { bg: 'rgba(245,158,11,0.15)',  color: '#F59E0B' },
  EN_REVISION: { bg: 'rgba(251,146,60,0.15)',  color: '#FB923C' },
  APROBADA:    { bg: 'rgba(16,185,129,0.15)',  color: '#10B981' },
  PAGADA:      { bg: 'rgba(16,185,129,0.15)',  color: '#10B981' },
  ERROR:       { bg: 'rgba(239,68,68,0.15)',   color: '#EF4444' },
};

const PAGO_ESTADO_STYLES = {
  PAGADO:     { bg: 'rgba(16,185,129,0.15)',  color: '#10B981' },
  EN_PROCESO: { bg: 'rgba(96,165,250,0.15)',  color: '#60A5FA' },
  PENDIENTE:  { bg: 'rgba(245,158,11,0.15)',  color: '#F59E0B' },
  RECHAZADO:  { bg: 'rgba(239,68,68,0.15)',   color: '#EF4444' },
};

const AJUSTE_STYLES = {
  BONO:         { bg: 'rgba(16,185,129,0.15)',  color: '#10B981' },
  PENALIZACION: { bg: 'rgba(239,68,68,0.15)',   color: '#EF4444' },
};

const formatCurrency = (value) =>
  value != null
    ? `$${Number(value).toLocaleString('es-CO', { minimumFractionDigits: 2 })}`
    : '—';

const formatDate = (dt) => {
  if (!dt) return '—';
  return new Date(dt).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' });
};

const Badge = ({ valor, styles }) => {
  const style = styles[valor] ?? { bg: 'rgba(107,114,128,0.15)', color: '#9CA3AF' };
  return (
    <span className="liqd-badge" style={{ backgroundColor: style.bg, color: style.color }}>
      {valor?.replace('_', ' ') ?? '—'}
    </span>
  );
};

const AjusteBadge = ({ tipo }) => (
  <Badge valor={tipo} styles={AJUSTE_STYLES} />
);

const KpiCard = ({ label, value, sub, accentColor }) => (
  <div className="liqd-kpi-card">
    <p className="liqd-kpi-label" style={{ color: accentColor }}>{label}</p>
    <p className="liqd-kpi-value">{value}</p>
    {sub && <p className="liqd-kpi-sub">{sub}</p>}
  </div>
);

const Field = ({ label, value }) => (
  <div className="liqd-field">
    <span className="liqd-field-label">{label}</span>
    <span className="liqd-field-value">{value ?? '—'}</span>
  </div>
);

const BackIcon = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
    <polyline points="15 18 9 12 15 6" />
  </svg>
);

const ajusteVacio = () => ({ tipo: 'BONO', monto: '', motivo: '' });

const RecalcularModal = ({ idLiquidacion, onClose, onSuccess }) => {
  const [responsable, setResponsable] = useState('');
  const [ajustes, setAjustes] = useState([]);
  const [ajusteActual, setAjusteActual] = useState(ajusteVacio());
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const agregarAjuste = () => {
    if (!ajusteActual.monto || !ajusteActual.motivo.trim()) return;
    setAjustes((prev) => [...prev, { ...ajusteActual, id: Date.now() }]);
    setAjusteActual(ajusteVacio());
  };

  const eliminarAjuste = (id) =>
    setAjustes((prev) => prev.filter((a) => a.id !== id));

  const handleSubmit = async () => {
    if (!responsable.trim()) { setError('El campo Responsable es obligatorio.'); return; }
    if (ajustes.length === 0) { setError('Debe agregar al menos un ajuste.'); return; }
    setLoading(true);
    setError(null);
    try {
      const payload = {
        responsable,
        ajustes: ajustes.map(({ tipo, monto, motivo }) => ({
          tipo,
          monto: Number(monto),
          motivo,
        })),
      };
      await liquidacionService.recalcularLiquidacion(idLiquidacion, payload);
      onSuccess();
    } catch (err) {
      const msg = err.response?.data?.message ?? err.response?.data?.error ?? 'Error al recalcular.';
      setError(err.response?.status === 403 ? 'No tienes permisos para recalcular (solo ADMIN).' : msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="liqd-recalc-overlay" onClick={onClose}>
      <div className="liqd-recalc-modal" onClick={(e) => e.stopPropagation()}>
        <p className="liqd-recalc-title">Recalcular Liquidación</p>

        <div className="liqd-recalc-field">
          <label className="liqd-recalc-label">Responsable *</label>
          <input
            className="liqd-recalc-input"
            placeholder="Nombre del responsable"
            value={responsable}
            onChange={(e) => setResponsable(e.target.value)}
          />
        </div>

        <div>
          <p className="liqd-recalc-section">Agregar ajuste</p>
          <div className="liqd-recalc-ajuste-form">
            <div className="liqd-recalc-field">
              <label className="liqd-recalc-label">Tipo</label>
              <select
                className="liqd-recalc-select"
                value={ajusteActual.tipo}
                onChange={(e) => setAjusteActual((p) => ({ ...p, tipo: e.target.value }))}
              >
                <option value="BONO">Bono</option>
                <option value="PENALIZACION">Penalización</option>
              </select>
            </div>
            <div className="liqd-recalc-field">
              <label className="liqd-recalc-label">Monto *</label>
              <input
                className="liqd-recalc-input"
                type="number"
                min="0.01"
                step="0.01"
                placeholder="0.00"
                value={ajusteActual.monto}
                onChange={(e) => setAjusteActual((p) => ({ ...p, monto: e.target.value }))}
              />
            </div>
            <div className="liqd-recalc-field">
              <label className="liqd-recalc-label">Motivo *</label>
              <input
                className="liqd-recalc-input"
                placeholder="Razón del ajuste"
                value={ajusteActual.motivo}
                onChange={(e) => setAjusteActual((p) => ({ ...p, motivo: e.target.value }))}
              />
            </div>
            <button className="liqd-recalc-add-btn" onClick={agregarAjuste}>+ Agregar</button>
          </div>
        </div>

        {ajustes.length > 0 && (
          <div>
            <p className="liqd-recalc-section">Ajustes a aplicar ({ajustes.length})</p>
            <div className="liqd-recalc-ajuste-list">
              {ajustes.map((a) => (
                <div key={a.id} className="liqd-recalc-ajuste-item">
                  <div className="liqd-recalc-ajuste-item-info">
                    <AjusteBadge tipo={a.tipo} />
                    <span style={{ fontWeight: 600, fontSize: '0.875rem' }}>
                      {formatCurrency(a.monto)}
                    </span>
                    <span className="liqd-recalc-ajuste-motivo">{a.motivo}</span>
                  </div>
                  <button className="liqd-recalc-remove-btn" onClick={() => eliminarAjuste(a.id)}>✕</button>
                </div>
              ))}
            </div>
          </div>
        )}

        {error && <p className="liqd-recalc-error">{error}</p>}

        <div className="liqd-recalc-footer">
          <button className="liqd-back-btn" onClick={onClose} disabled={loading}>Cancelar</button>
          <button
            className="liqd-recalc-submit-btn"
            onClick={handleSubmit}
            disabled={loading}
          >
            {loading ? 'Recalculando...' : 'Confirmar Recálculo'}
          </button>
        </div>
      </div>
    </div>
  );
};

const LiquidacionDetallePage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { liquidacion: liq, loading, error, retry } = useLiquidacionDetalle(id);

  const [modalRecalcular, setModalRecalcular] = useState(false);
  const [estadoPago, setEstadoPago] = useState(null);

  useEffect(() => {
    if (!id) return;
    liquidacionService.getEstadoPagoPorLiquidacion(id)
      .then((res) => setEstadoPago(res.data))
      .catch(() => setEstadoPago(null));
  }, [id]);

  const handleRecalcularSuccess = () => {
    setModalRecalcular(false);
    retry();
  };

  if (loading) {
    return <p style={{ color: '#9CA3AF', padding: '2rem 0' }}>Cargando...</p>;
  }

  if (error) {
    return (
      <div style={{ padding: '2rem 0' }}>
        <p style={{ color: '#EF4444', marginBottom: '0.75rem' }}>{error}</p>
        <button className="liqd-back-btn" onClick={retry}>Reintentar</button>
      </div>
    );
  }

  if (!liq) return null;

  const ajustes = liq.ajustes ?? [];
  const totalBonos = ajustes
    .filter((a) => a.tipo === 'BONO')
    .reduce((s, a) => s + Number(a.monto ?? 0), 0);
  const totalPenalizaciones = ajustes
    .filter((a) => a.tipo === 'PENALIZACION')
    .reduce((s, a) => s + Number(a.monto ?? 0), 0);

  return (
    <div>
      {modalRecalcular && (
        <RecalcularModal
          idLiquidacion={id}
          onClose={() => setModalRecalcular(false)}
          onSuccess={handleRecalcularSuccess}
        />
      )}

      <div className="liqd-header">
        <div className="liqd-header-left">
          <button className="liqd-back-btn" onClick={() => navigate('/liquidaciones')}>
            <BackIcon /> Volver
          </button>
          <div className="liqd-title-block">
            <h1>Detalle de Liquidación</h1>
            <p>{liq.id_liquidacion}</p>
          </div>
        </div>
        <div className="liqd-header-right">
          <Badge valor={liq.estado_liquidacion} styles={ESTADO_STYLES} />
          <button
            className="liqd-recalc-submit-btn"
            onClick={() => setModalRecalcular(true)}
          >
            Recalcular
          </button>
        </div>
      </div>

      <div className="liqd-kpi-grid">
        <KpiCard
          label="Monto Bruto"
          value={formatCurrency(liq.monto_bruto)}
          sub={`${liq.numero_paradas ?? 0} paradas × ${formatCurrency(liq.precio_parada)}`}
          accentColor="#60A5FA"
        />
        <KpiCard
          label="Monto Neto"
          value={formatCurrency(liq.monto_neto)}
          sub="Después de ajustes"
          accentColor="#10B981"
        />
        <KpiCard
          label="Total Bonos"
          value={formatCurrency(totalBonos)}
          sub={`${ajustes.filter((a) => a.tipo === 'BONO').length} bono(s)`}
          accentColor="#14B8A6"
        />
        <KpiCard
          label="Total Penalizaciones"
          value={formatCurrency(totalPenalizaciones)}
          sub={`${ajustes.filter((a) => a.tipo === 'PENALIZACION').length} penalización(es)`}
          accentColor="#F87171"
        />
      </div>

      <div className="liqd-body">
        <div className="liqd-card">
          <p className="liqd-card-title">Información de la Ruta</p>
          <Field label="ID Ruta" value={liq.id_ruta} />
          <Field label="Tipo de Vehículo" value={liq.tipo_vehiculo} />
          <Field label="N° de Paradas" value={liq.numero_paradas} />
          <Field label="Precio por Parada" value={formatCurrency(liq.precio_parada)} />
          <Field label="Fecha Inicio" value={formatDate(liq.fecha_inicio)} />
          <Field label="Fecha Cierre" value={formatDate(liq.fecha_cierre)} />
        </div>

        <div className="liqd-card">
          <p className="liqd-card-title">Información de la Liquidación</p>
          <Field label="ID Contrato" value={liq.id_contrato} />
          <Field label="Usuario" value={liq.usuario_id} />
          <Field label="Fecha de Cálculo" value={formatDate(liq.fecha_calculo)} />
          <Field label="Monto Bruto" value={formatCurrency(liq.monto_bruto)} />
          <Field label="Monto Neto" value={formatCurrency(liq.monto_neto)} />
          <Field label="Estado" value={<Badge valor={liq.estado_liquidacion} styles={ESTADO_STYLES} />} />
        </div>

        {estadoPago && (
          <div className="liqd-card liqd-pago-card">
            <p className="liqd-card-title">Estado de Pago</p>
            <div className="liqd-pago-grid">
              <Field label="ID Pago" value={estadoPago.id_pago} />
              <Field label="Estado" value={<Badge valor={estadoPago.estado} styles={PAGO_ESTADO_STYLES} />} />
              <Field label="Última Secuencia" value={estadoPago.ultima_secuencia_procesada} />
              <Field label="Última Actualización" value={formatDate(estadoPago.fecha_ultima_actualizacion)} />
            </div>
          </div>
        )}

        <div className="liqd-card liqd-ajustes-card">
          <p className="liqd-card-title">Ajustes y Penalizaciones</p>
          {ajustes.length === 0 ? (
            <p className="liqd-empty-ajustes">No hay ajustes registrados para esta liquidación.</p>
          ) : (
            <table className="liqd-ajustes-table">
              <thead>
                <tr>
                  <th>Tipo</th>
                  <th>Monto</th>
                  <th>Razón</th>
                </tr>
              </thead>
              <tbody>
                {ajustes.map((ajuste) => (
                  <tr key={ajuste.id}>
                    <td><AjusteBadge tipo={ajuste.tipo} /></td>
                    <td style={{ fontWeight: 600 }}>{formatCurrency(ajuste.monto)}</td>
                    <td style={{ color: '#9CA3AF' }}>{ajuste.razon ?? '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
};

export default LiquidacionDetallePage;
