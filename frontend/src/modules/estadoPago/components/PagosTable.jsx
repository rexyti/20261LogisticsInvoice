import React from 'react';

const ESTADO_STYLES = {
  PAGADO:     { bg: 'rgba(16,185,129,0.15)',  color: '#10B981' },
  EN_PROCESO: { bg: 'rgba(96,165,250,0.15)',  color: '#60A5FA' },
  PENDIENTE:  { bg: 'rgba(245,158,11,0.15)',  color: '#F59E0B' },
  RECHAZADO:  { bg: 'rgba(239,68,68,0.15)',   color: '#EF4444' },
};

const StatusBadge = ({ status }) => {
  if (!status) return <span style={{ color: '#6B7280' }}>—</span>;
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
      whiteSpace: 'nowrap',
    }}>
      {status.replace('_', ' ')}
    </span>
  );
};

const formatCurrency = (value) =>
  value != null
    ? `$${Number(value).toLocaleString('es-CO', { minimumFractionDigits: 2 })}`
    : '—';

const PagosTable = ({ pagos = [] }) => {
  if (pagos.length === 0) {
    return <p>No hay pagos para mostrar.</p>;
  }

  return (
    <table>
      <thead>
        <tr>
          <th>ID Pago</th>
          <th>ID Liquidación</th>
          <th>Monto</th>
          <th>Estado</th>
          <th>Fecha</th>
        </tr>
      </thead>
      <tbody>
        {pagos.map((pago) => (
          <tr key={pago.pago_id}>
            <td title={pago.pago_id}>{pago.pago_id?.slice(0, 8)}…</td>
            <td title={pago.liquidacion_id}>{pago.liquidacion_id?.slice(0, 8)}…</td>
            <td>{formatCurrency(pago.monto)}</td>
            <td><StatusBadge status={pago.estado} /></td>
            <td>{pago.fecha ? new Date(pago.fecha).toLocaleString('es-CO') : '—'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default PagosTable;
