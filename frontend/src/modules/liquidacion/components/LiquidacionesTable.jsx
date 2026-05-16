import React from 'react';
import { useNavigate } from 'react-router-dom';

const AVATAR_COLORS = ['#3B82F6', '#8B5CF6', '#10B981', '#F59E0B', '#EF4444', '#EC4899'];

const getAvatarColor = (name) => {
  if (!name) return '#6B7280';
  let hash = 0;
  for (const c of name) hash = c.charCodeAt(0) + ((hash << 5) - hash);
  return AVATAR_COLORS[Math.abs(hash) % AVATAR_COLORS.length];
};

const getInitials = (name) => {
  if (!name) return '??';
  return name.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase();
};

// Valores reales del enum EstadoLiquidacion del backend
const ESTADO_STYLES = {
  CALCULADA:   { bg: 'rgba(96,165,250,0.15)',  color: '#60A5FA' },
  RECALCULADA: { bg: 'rgba(20,184,166,0.15)',  color: '#14B8A6' },
  PENDIENTE:   { bg: 'rgba(245,158,11,0.15)',  color: '#F59E0B' },
  EN_REVISION: { bg: 'rgba(251,146,60,0.15)',  color: '#FB923C' },
  APROBADA:    { bg: 'rgba(16,185,129,0.15)',  color: '#10B981' },
  PAGADA:      { bg: 'rgba(16,185,129,0.15)',  color: '#10B981' },
  ERROR:       { bg: 'rgba(239,68,68,0.15)',   color: '#EF4444' },
};

// Valores reales del enum TipoAjuste del backend
const AJUSTE_STYLES = {
  BONO:         { bg: 'rgba(16,185,129,0.15)',  color: '#10B981' },
  PENALIZACION: { bg: 'rgba(239,68,68,0.15)',   color: '#EF4444' },
};

const EstadoBadge = ({ estado }) => {
  const style = ESTADO_STYLES[estado] ?? { bg: 'rgba(107,114,128,0.15)', color: '#9CA3AF' };
  const label = estado?.replace('_', ' ') ?? '—';
  return (
    <span style={{
      padding: '0.25rem 0.75rem', borderRadius: '9999px',
      fontSize: '0.75rem', fontWeight: 600,
      backgroundColor: style.bg, color: style.color, whiteSpace: 'nowrap',
    }}>
      {label}
    </span>
  );
};

const AjusteBadge = ({ ajustes = [] }) => {
  if (!ajustes.length) {
    return (
      <span style={{
        padding: '0.25rem 0.75rem', borderRadius: '9999px',
        fontSize: '0.75rem', fontWeight: 600,
        backgroundColor: 'rgba(107,114,128,0.15)', color: '#9CA3AF',
      }}>
        Ninguno
      </span>
    );
  }
  const primero = ajustes[0];
  const style = AJUSTE_STYLES[primero.tipo] ?? { bg: 'rgba(107,114,128,0.15)', color: '#9CA3AF' };
  const label = ajustes.length > 1 ? `${primero.tipo} +${ajustes.length - 1}` : primero.tipo;
  return (
    <span style={{
      padding: '0.25rem 0.75rem', borderRadius: '9999px',
      fontSize: '0.75rem', fontWeight: 600,
      backgroundColor: style.bg, color: style.color, whiteSpace: 'nowrap',
    }} title={ajustes.map(a => `${a.tipo}: ${a.razon}`).join('\n')}>
      {label}
    </span>
  );
};

const EyeIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
    <circle cx="12" cy="12" r="3" />
  </svg>
);

const ReceiptIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" />
    <polyline points="14 2 14 8 20 8" />
    <line x1="16" y1="13" x2="8" y2="13" />
    <line x1="16" y1="17" x2="8" y2="17" />
  </svg>
);

const formatCurrency = (value) =>
  value != null
    ? `$${Number(value).toLocaleString('es-CO', { minimumFractionDigits: 2 })}`
    : '—';

const LiquidacionesTable = ({ liquidaciones = [] }) => {
  const navigate = useNavigate();

  if (liquidaciones.length === 0) {
    return <p style={{ color: '#9CA3AF', padding: '1rem 0' }}>No hay liquidaciones para mostrar.</p>;
  }

  return (
    <table>
      <thead>
        <tr>
          <th>ID Ruta / Tipo Vehículo</th>
          <th>Conductor</th>
          <th>Estado de Liquidación</th>
          <th>Ajustes / Penalidades</th>
          <th>Monto Total</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        {liquidaciones.map((liq) => {
          const conductor = liq.conductor_nombre ?? null;
          const idRutaCorto = liq.id_ruta?.toString().slice(0, 8) ?? '—';

          return (
            <tr key={liq.id_liquidacion}>
              <td>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.2rem' }}>
                  <span style={{ fontWeight: 600, fontFamily: 'monospace', fontSize: '0.85rem' }}>
                    {idRutaCorto}…
                  </span>
                  <span style={{ fontSize: '0.75rem', color: '#9CA3AF' }}>
                    {liq.tipo_vehiculo ?? '—'}
                  </span>
                </div>
              </td>
              <td>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                  <div style={{
                    width: '2rem', height: '2rem', borderRadius: '50%',
                    backgroundColor: getAvatarColor(conductor),
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontSize: '0.7rem', fontWeight: 700, color: '#fff', flexShrink: 0,
                  }}>
                    {getInitials(conductor)}
                  </div>
                  <span>{conductor ?? '—'}</span>
                </div>
              </td>
              <td>
                <EstadoBadge estado={liq.estado_liquidacion} />
              </td>
              <td>
                <AjusteBadge ajustes={liq.ajustes ?? []} />
              </td>
              <td style={{ fontWeight: 600 }}>
                {formatCurrency(liq.monto_neto ?? liq.monto_bruto)}
              </td>
              <td>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <button
                    className="action-btn"
                    title="Ver detalle"
                    onClick={() => navigate(`/liquidaciones/${liq.id_liquidacion}`)}
                  >
                    <EyeIcon />
                  </button>
                  <button className="action-btn" title="Ver comprobante">
                    <ReceiptIcon />
                  </button>
                </div>
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
};

export default LiquidacionesTable;
