import React, { useState, useEffect, useCallback } from 'react';
import { sincronizacionService } from '../services/sincronizacionService';
import './Sincronizacion.css';

const formatDate = (dt) =>
  dt ? new Date(dt).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' }) : '—';

const RESULTADO_COLORS = {
  EXITOSO:               { bg: 'rgba(16,185,129,0.15)',  color: '#10B981' },
  PENDIENTE:             { bg: 'rgba(245,158,11,0.15)',  color: '#F59E0B' },
  ERROR_HTTP:            { bg: 'rgba(239,68,68,0.15)',   color: '#EF4444' },
  ESTADO_NO_MAPEADO:     { bg: 'rgba(239,68,68,0.15)',   color: '#EF4444' },
  PAQUETE_NO_ENCONTRADO: { bg: 'rgba(239,68,68,0.15)',   color: '#EF4444' },
};

const ResultadoBadge = ({ valor }) => {
  const style = RESULTADO_COLORS[valor] ?? { bg: 'rgba(107,114,128,0.15)', color: '#9CA3AF' };
  return (
    <span className="sinc-badge" style={{ backgroundColor: style.bg, color: style.color }}>
      {valor?.replace(/_/g, ' ') ?? '—'}
    </span>
  );
};

/* ── Sección 1: Sincronizar ── */
const SincronizarSection = () => {
  const [idRuta, setIdRuta] = useState('');
  const [idPaquete, setIdPaquete] = useState('');
  const [loading, setLoading] = useState(false);
  const [resultado, setResultado] = useState(null);
  const [error, setError] = useState(null);

  const handleSincronizar = async () => {
    if (!idRuta.trim() || !idPaquete.trim()) return;
    setLoading(true);
    setResultado(null);
    setError(null);
    try {
      const res = await sincronizacionService.sincronizarPaquete(idRuta.trim(), idPaquete.trim());
      setResultado(res.data);
    } catch (err) {
      setError(err.response?.data?.message ?? 'Error al sincronizar el paquete.');
    } finally {
      setLoading(false);
    }
  };

  const resultClass = resultado ? `sinc-result sinc-result-${resultado.resultado}` : 'sinc-result';

  return (
    <div className="sinc-section">
      <p className="sinc-section-title">Sincronizar Paquete</p>
      <div className="sinc-form-row">
        <div className="sinc-field">
          <label>ID Ruta</label>
          <input value={idRuta} onChange={(e) => setIdRuta(e.target.value)}
            placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx" />
        </div>
        <div className="sinc-field">
          <label>ID Paquete</label>
          <input value={idPaquete} onChange={(e) => setIdPaquete(e.target.value)}
            placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx" />
        </div>
        <button
          className="sinc-btn-primary"
          onClick={handleSincronizar}
          disabled={loading || !idRuta.trim() || !idPaquete.trim()}
        >
          {loading ? 'Sincronizando...' : 'Sincronizar'}
        </button>
      </div>

      {error && (
        <p style={{ color: '#EF4444', marginTop: '0.75rem', fontSize: '0.875rem' }}>{error}</p>
      )}

      {resultado && (
        <div className={resultClass}>
          <span className="sinc-result-label">Resultado</span>
          <span className="sinc-result-value"><ResultadoBadge valor={resultado.resultado} /></span>
          {resultado.estado_actual && (
            <>
              <span className="sinc-result-label" style={{ marginTop: '0.5rem' }}>Estado Actual</span>
              <span className="sinc-result-value">{resultado.estado_actual}</span>
            </>
          )}
          {resultado.porcentaje_pago != null && (
            <>
              <span className="sinc-result-label">% Pago</span>
              <span className="sinc-result-value">{resultado.porcentaje_pago}%</span>
            </>
          )}
          {resultado.mensaje && (
            <>
              <span className="sinc-result-label">Mensaje</span>
              <span className="sinc-result-value" style={{ fontSize: '0.85rem', fontWeight: 400 }}>
                {resultado.mensaje}
              </span>
            </>
          )}
        </div>
      )}
    </div>
  );
};

/* ── Sección 2: Historial de paquete ── */
const HistorialSection = () => {
  const [idPaquete, setIdPaquete] = useState('');
  const [idBuscado, setIdBuscado] = useState(null);
  const [historial, setHistorial] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const buscar = async () => {
    if (!idPaquete.trim()) return;
    setLoading(true);
    setError(null);
    try {
      const res = await sincronizacionService.getHistorialPaquete(idPaquete.trim());
      setHistorial(res.data ?? []);
      setIdBuscado(idPaquete.trim());
    } catch {
      setError('No se encontró historial para ese paquete.');
      setHistorial([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="sinc-section">
      <p className="sinc-section-title">Historial de Estado de Paquete</p>
      <div className="sinc-search-row">
        <div className="sinc-field">
          <label>ID Paquete</label>
          <input value={idPaquete} onChange={(e) => setIdPaquete(e.target.value)}
            placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
            onKeyDown={(e) => e.key === 'Enter' && buscar()} />
        </div>
        <button className="sinc-btn-secondary" onClick={buscar} disabled={loading || !idPaquete.trim()}>
          {loading ? 'Buscando...' : 'Buscar'}
        </button>
      </div>

      {error && <p style={{ color: '#EF4444', fontSize: '0.875rem' }}>{error}</p>}

      {idBuscado && !error && (
        historial.length === 0 ? (
          <p style={{ color: '#6B7280', fontSize: '0.875rem' }}>Sin historial para este paquete.</p>
        ) : (
          <table className="sinc-table">
            <thead>
              <tr>
                <th>ID Evento</th>
                <th>Estado</th>
                <th>Fecha</th>
              </tr>
            </thead>
            <tbody>
              {historial.map((h) => (
                <tr key={h.id}>
                  <td title={h.id}>{h.id?.slice(0, 8)}…</td>
                  <td style={{ fontFamily: 'sans-serif' }}>{h.estado ?? '—'}</td>
                  <td>{formatDate(h.fecha)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )
      )}
    </div>
  );
};

/* ── Sección 3: Logs de sincronización ── */
const LogsSection = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [filtroPaquete, setFiltroPaquete] = useState('');
  const [inputPaquete, setInputPaquete] = useState('');
  const size = 20;

  const fetchLogs = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = filtroPaquete
        ? await sincronizacionService.getLogsPorPaquete(filtroPaquete, page, size)
        : await sincronizacionService.getLogs(page, size);
      setLogs(res.data ?? []);
    } catch {
      setError('Error al cargar los logs.');
    } finally {
      setLoading(false);
    }
  }, [page, filtroPaquete]);

  useEffect(() => { fetchLogs(); }, [fetchLogs]);

  const aplicarFiltro = () => {
    setFiltroPaquete(inputPaquete.trim());
    setPage(0);
  };

  const limpiarFiltro = () => {
    setInputPaquete('');
    setFiltroPaquete('');
    setPage(0);
  };

  return (
    <div className="sinc-section">
      <p className="sinc-section-title">Logs de Sincronización</p>
      <div className="sinc-search-row">
        <div className="sinc-field">
          <label>Filtrar por ID Paquete</label>
          <input value={inputPaquete} onChange={(e) => setInputPaquete(e.target.value)}
            placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
            onKeyDown={(e) => e.key === 'Enter' && aplicarFiltro()} />
        </div>
        <button className="sinc-btn-secondary" onClick={aplicarFiltro} disabled={!inputPaquete.trim()}>
          Filtrar
        </button>
        {filtroPaquete && (
          <button className="sinc-btn-secondary" onClick={limpiarFiltro}>Ver todos</button>
        )}
      </div>

      {loading && <p style={{ color: '#9CA3AF', fontSize: '0.875rem' }}>Cargando...</p>}
      {error && <p style={{ color: '#EF4444', fontSize: '0.875rem' }}>{error}</p>}

      {!loading && !error && (
        logs.length === 0 ? (
          <p style={{ color: '#6B7280', fontSize: '0.875rem' }}>Sin logs{filtroPaquete ? ' para este paquete' : ''}.</p>
        ) : (
          <>
            <table className="sinc-table">
              <thead>
                <tr>
                  <th>ID Log</th>
                  <th>ID Paquete</th>
                  <th>HTTP</th>
                  <th>Fecha Sincronización</th>
                </tr>
              </thead>
              <tbody>
                {logs.map((log) => (
                  <tr key={log.id}>
                    <td title={log.id}>{log.id?.slice(0, 8)}…</td>
                    <td title={log.id_paquete}>{log.id_paquete?.slice(0, 8)}…</td>
                    <td>
                      <span className="sinc-badge" style={{
                        backgroundColor: log.codigo_respuesta_http === 200
                          ? 'rgba(16,185,129,0.15)' : 'rgba(239,68,68,0.15)',
                        color: log.codigo_respuesta_http === 200 ? '#10B981' : '#EF4444',
                        fontFamily: 'sans-serif',
                      }}>
                        {log.codigo_respuesta_http ?? '—'}
                      </span>
                    </td>
                    <td>{formatDate(log.fecha_sincronizacion)}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div className="sinc-pagination">
              <button onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>
                Anterior
              </button>
              <span>Página {page + 1}</span>
              <button
                onClick={() => setPage((p) => p + 1)}
                disabled={logs.length < size}
              >
                Siguiente
              </button>
            </div>
          </>
        )
      )}
    </div>
  );
};

/* ── Página principal ── */
const SincronizacionPage = () => (
  <div>
    <div className="sinc-header">
      <h1>Sincronización de Paquetes</h1>
      <p>Sincroniza paquetes, consulta historial de estados y revisa logs.</p>
    </div>
    <SincronizarSection />
    <HistorialSection />
    <LogsSection />
  </div>
);

export default SincronizacionPage;
