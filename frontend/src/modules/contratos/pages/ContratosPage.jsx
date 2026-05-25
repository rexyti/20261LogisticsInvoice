import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useContratos } from '../hooks/useContratos';
import { contratoService } from '../services/contratoService';
import './Contratos.css';

const TIPOS_VEHICULO = ['VAN', 'CAMION', 'MOTO', 'NHR', 'TURBO', 'FURGON'];
const ESTADOS_SEGURO = ['ACTIVO', 'INACTIVO', 'VENCIDO'];

const formatDate = (dt) =>
  dt ? new Date(dt).toLocaleDateString('es-CO', { dateStyle: 'medium' }) : '—';

const formatCurrency = (value) =>
  value != null ? `$${Number(value).toLocaleString('es-CO', { minimumFractionDigits: 2 })}` : '—';

const EyeIcon = () => (
  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" /><circle cx="12" cy="12" r="3" />
  </svg>
);

const formVacio = () => ({
  id_contrato: '', tipo_contrato: '', transportista_id: '',
  es_por_parada: true, precio_paradas: '', precio: '',
  tipo_vehiculo: 'VAN',
  fecha_inicio: '', fecha_final: '',
  seguro: { numero_poliza: '', estado: 'ACTIVO' },
});

const ContratosPage = () => {
  const navigate = useNavigate();
  const { contratos, loading, error, page, totalPaginas, setPage, retry } = useContratos();
  const [mostrarForm, setMostrarForm] = useState(false);
  const [form, setForm] = useState(formVacio());
  const [enviando, setEnviando] = useState(false);
  const [errorForm, setErrorForm] = useState(null);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    if (name.startsWith('seguro.')) {
      const key = name.replace('seguro.', '');
      setForm((p) => ({ ...p, seguro: { ...p.seguro, [key]: value } }));
    } else {
      setForm((p) => ({ ...p, [name]: type === 'checkbox' ? checked : value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setEnviando(true);
    setErrorForm(null);
    try {
      const payload = {
        ...form,
        precio_paradas: form.es_por_parada ? Number(form.precio_paradas) : null,
        precio: form.es_por_parada ? null : Number(form.precio),
        fecha_inicio: form.fecha_inicio ? `${form.fecha_inicio}:00` : null,
        fecha_final: form.fecha_final ? `${form.fecha_final}:00` : null,
      };
      await contratoService.crearContrato(payload);
      setForm(formVacio());
      setMostrarForm(false);
      retry();
    } catch (err) {
      setErrorForm(
        err.response?.data?.message ??
        err.response?.data?.error ??
        (err.response?.status === 403 ? 'No tienes permisos (requiere GESTOR_FINANCIERO).' : 'Error al crear el contrato.')
      );
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div>
      <div className="con-header">
        <div>
          <h1>Contratos</h1>
          <p>Gestión de contratos con transportistas.</p>
        </div>
        <div className="con-header-actions">
          <button
            className={mostrarForm ? 'con-btn-secondary' : 'con-btn-primary'}
            onClick={() => { setMostrarForm((v) => !v); setErrorForm(null); }}
          >
            {mostrarForm ? '✕ Cancelar' : '+ Nuevo Contrato'}
          </button>
        </div>
      </div>

      {mostrarForm && (
        <div className="con-form-panel">
          <p className="con-form-title">Nuevo Contrato</p>
          <form onSubmit={handleSubmit}>
            <div className="con-form-grid">
              <div className="con-field">
                <label>ID Contrato *</label>
                <input name="id_contrato" value={form.id_contrato} onChange={handleChange}
                  placeholder="CONT-2026-001" required />
              </div>
              <div className="con-field">
                <label>Tipo Contrato *</label>
                <input name="tipo_contrato" value={form.tipo_contrato} onChange={handleChange}
                  placeholder="Ej: PARADA, MENSAJERIA" required />
              </div>
              <div className="con-field">
                <label>ID Transportista (UUID) *</label>
                <input name="transportista_id" value={form.transportista_id} onChange={handleChange}
                  placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx" required />
              </div>
              <div className="con-field">
                <label>Tipo Vehículo *</label>
                <select name="tipo_vehiculo" value={form.tipo_vehiculo} onChange={handleChange}>
                  {TIPOS_VEHICULO.map((t) => <option key={t} value={t}>{t}</option>)}
                </select>
              </div>
            </div>

            <div className="con-form-grid-3">
              <div className="con-field" style={{ flexDirection: 'row', alignItems: 'center', gap: '0.75rem' }}>
                <input type="checkbox" id="es_por_parada" name="es_por_parada"
                  checked={form.es_por_parada} onChange={handleChange}
                  style={{ width: 'auto', accentColor: '#3B82F6' }} />
                <label htmlFor="es_por_parada" style={{ textTransform: 'none', fontSize: '0.875rem', color: '#F9FAFB' }}>
                  Precio por parada
                </label>
              </div>
              {form.es_por_parada ? (
                <div className="con-field">
                  <label>Precio por Parada *</label>
                  <input type="number" name="precio_paradas" value={form.precio_paradas}
                    onChange={handleChange} min="0.01" step="0.01" placeholder="0.00" required />
                </div>
              ) : (
                <div className="con-field">
                  <label>Precio Total *</label>
                  <input type="number" name="precio" value={form.precio}
                    onChange={handleChange} min="0.01" step="0.01" placeholder="0.00" required />
                </div>
              )}
            </div>

            <div className="con-form-grid">
              <div className="con-field">
                <label>Fecha Inicio *</label>
                <input type="datetime-local" name="fecha_inicio"
                  value={form.fecha_inicio} onChange={handleChange} required />
              </div>
              <div className="con-field">
                <label>Fecha Final *</label>
                <input type="datetime-local" name="fecha_final"
                  value={form.fecha_final} onChange={handleChange} required />
              </div>
              <div className="con-field">
                <label>N° Póliza Seguro *</label>
                <input name="seguro.numero_poliza" value={form.seguro.numero_poliza}
                  onChange={handleChange} placeholder="POL-2026-001" required />
              </div>
              <div className="con-field">
                <label>Estado Seguro *</label>
                <select name="seguro.estado" value={form.seguro.estado} onChange={handleChange}>
                  {ESTADOS_SEGURO.map((e) => <option key={e} value={e}>{e}</option>)}
                </select>
              </div>
            </div>

            {errorForm && <p className="con-form-error">{errorForm}</p>}

            <div className="con-form-actions">
              <button type="button" className="con-btn-secondary"
                onClick={() => { setMostrarForm(false); setForm(formVacio()); setErrorForm(null); }}>
                Cancelar
              </button>
              <button type="submit" className="con-btn-primary" disabled={enviando}>
                {enviando ? 'Creando...' : 'Crear Contrato'}
              </button>
            </div>
          </form>
        </div>
      )}

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
