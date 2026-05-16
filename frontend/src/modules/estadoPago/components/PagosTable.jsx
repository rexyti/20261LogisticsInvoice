import React from 'react';

const StatusBadge = ({ status }) => {
  if (!status) return <span style={{ color: '#6B7280' }}>—</span>;
  return <span className={`status-badge status-${status}`}>{status}</span>;
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
