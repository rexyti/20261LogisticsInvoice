import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useContratos } from '../hooks/useContratos';
import './Contratos.css';

const formatDate = (dt) =>
  dt ? new Date(dt).toLocaleDateString('es-CO', { dateStyle: 'medium' }) : '—';

const formatCurrency = (value) =>
  value != null ? `$${Number(value).toLocaleString('es-CO', { minimumFractionDigits: 2 })}` : '—';

const EyeIcon = () => (
  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" /><circle cx="12" cy="12" r="3" />
  </svg>
);

const ContratosPage = () => {
  const navigate = useNavigate();
  const { contratos, loading, error, page, totalPaginas, setPage, retry } = useContratos();

  return (
    <div>
      <div className="con-header">
        <div>
          <h1>Contratos</h1>
          <p>Gestión de contratos con transportistas.</p>
        </div>
      </div>

      <div className="con-table-container">
        {loading && <p style={{ color: '#9CA3AF' }}>Cargando...</p>}
        {error && (
          <div>
            <p style={{ color: '#EF4444', marginBottom: '0.5rem' }}>{error}</p>
            <button className="con-btn-secondary" onClick={retry}>Reintentar</button>
          </div>
        )}
        {!loading && !error && (
          <>
            {contratos.length === 0 ? (
              <p style={{ color: '#9CA3AF' }}>No hay contratos registrados.</p>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>ID Contrato</th>
                    <th>Tipo</th>
                    <th>Transportista</th>
                    <th>Vehículo</th>
                    <th>Precio</th>
                    <th>Vigencia</th>
                    <th>Seguro</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {contratos.map((c) => (
                    <tr key={c.id}>
                      <td style={{ fontFamily: 'monospace', fontWeight: 600 }}>{c.id_contrato}</td>
                      <td>{c.tipo_contrato}</td>
                      <td>{c.transportista?.nombre ?? '—'}</td>
                      <td>
                        <span className="con-badge" style={{ background: 'rgba(96,165,250,0.15)', color: '#60A5FA' }}>
                          {c.tipo_vehiculo}
                        </span>
                      </td>
                      <td>
                        {c.es_por_parada
                          ? `${formatCurrency(c.precio_paradas)} / parada`
                          : formatCurrency(c.precio)}
                      </td>
                      <td style={{ fontSize: '0.8rem' }}>
                        {formatDate(c.fecha_inicio)} → {formatDate(c.fecha_final)}
                      </td>
                      <td>
                        <span className="con-badge" style={{
                          background: c.seguro?.estado === 'ACTIVO'
                            ? 'rgba(16,185,129,0.15)' : 'rgba(239,68,68,0.15)',
                          color: c.seguro?.estado === 'ACTIVO' ? '#10B981' : '#EF4444',
                        }}>
                          {c.seguro?.estado ?? '—'}
                        </span>
                      </td>
                      <td>
                        <button
                          className="con-action-btn"
                          title="Ver detalle"
                          onClick={() => navigate(`/contratos/${c.id_contrato}`)}
                        >
                          <EyeIcon />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}

            {totalPaginas > 1 && (
              <div className="pagination-controls">
                <button onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>
                  Anterior
                </button>
                <span>Página {page + 1} de {totalPaginas}</span>
                <button
                  onClick={() => setPage((p) => Math.min(totalPaginas - 1, p + 1))}
                  disabled={page >= totalPaginas - 1}
                >
                  Siguiente
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default ContratosPage;
