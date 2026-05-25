import React from 'react';
import { useLiquidaciones } from '../hooks/useLiquidaciones';
import LiquidacionesTable from '../components/LiquidacionesTable';
import './Liquidaciones.css';

const formatCurrency = (value) =>
  `$${Number(value).toLocaleString('es-CO', { minimumFractionDigits: 2 })}`;

const exportarCSV = (liquidaciones) => {
  const headers = ['ID Liquidacion', 'ID Ruta', 'Conductor', 'Tipo Vehiculo', 'Paradas', 'Precio Parada', 'Monto Bruto', 'Monto Neto', 'Estado', 'Fecha Calculo'];
  const filas = liquidaciones.map((l) => [
    l.id_liquidacion ?? '',
    l.id_ruta ?? '',
    l.conductor_nombre ?? '',
    l.tipo_vehiculo ?? '',
    l.numero_paradas ?? '',
    l.precio_parada ?? '',
    l.monto_bruto ?? '',
    l.monto_neto ?? '',
    l.estado_liquidacion ?? '',
    l.fecha_calculo ? new Date(l.fecha_calculo).toLocaleString('es-CO') : '',
  ]);
  const contenido = [headers, ...filas].map((r) => r.join(';')).join('\n');
  const blob = new Blob(['﻿' + contenido], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `liquidaciones_${new Date().toISOString().slice(0, 10)}.csv`;
  a.click();
  URL.revokeObjectURL(url);
};

const SummaryCard = ({ label, value, subtitle, accentColor }) => (
  <div className="liq-summary-card">
    <p className="liq-summary-label" style={{ color: accentColor }}>{label}</p>
    <p className="liq-summary-value">{value}</p>
    {subtitle && <p className="liq-summary-subtitle">{subtitle}</p>}
  </div>
);

const LiquidacionesPage = () => {
  const {
    liquidaciones,
    todasLiquidaciones,
    loading,
    error,
    page,
    totalPaginas,
    setPage,
    retry,
    busqueda,
    setBusqueda,
    filtroEstado,
    setFiltroEstado,
    filtroTipoAjuste,
    setFiltroTipoAjuste,
  } = useLiquidaciones();

  const totalPendiente = todasLiquidaciones
    .filter((l) => l.estado_liquidacion === 'PENDIENTE')
    .reduce((sum, l) => sum + Number(l.monto_neto ?? l.monto_bruto ?? 0), 0);

  const enRevision = todasLiquidaciones.filter(
    (l) => l.estado_liquidacion === 'EN_REVISION'
  ).length;

  const penalidades = todasLiquidaciones.reduce((sum, l) => {
    const totalAjuste = (l.ajustes ?? [])
      .filter((a) => a.tipo === 'PENALIZACION')
      .reduce((s, a) => s + Number(a.monto ?? 0), 0);
    return sum + totalAjuste;
  }, 0);

  return (
    <div>
      <div className="liq-page-header">
        <div>
          <h1>Liquidaciones</h1>
          <p>Gestión administrativa de estados de pago y ajustes por conductor</p>
        </div>
        <div className="liq-header-actions">
          <button className="btn-secondary" onClick={retry}>↻ Recalcular Todo</button>
          <button className="btn-primary" onClick={() => exportarCSV(todasLiquidaciones)}>↓ Exportar Reporte</button>
        </div>
      </div>

      <div className="liq-filters-bar">
        <div className="liq-search-group">
          <svg className="liq-search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="11" cy="11" r="8" /><line x1="21" y1="21" x2="16.65" y2="16.65" />
          </svg>
          <input
            type="text"
            placeholder="ID de Ruta o nombre del conductor..."
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
            className="liq-search-input"
          />
        </div>
        <div className="liq-filter-group">
          <label>Estado</label>
          <select value={filtroEstado} onChange={(e) => setFiltroEstado(e.target.value)}>
            <option value="">Todos los estados</option>
            <option value="CALCULADA">Calculada</option>
            <option value="RECALCULADA">Recalculada</option>
            <option value="PENDIENTE">Pendiente</option>
            <option value="EN_REVISION">En Revisión</option>
            <option value="APROBADA">Aprobada</option>
            <option value="PAGADA">Pagada</option>
            <option value="ERROR">Error</option>
          </select>
        </div>
        <div className="liq-filter-group">
          <label>Tipo Ajuste</label>
          <select value={filtroTipoAjuste} onChange={(e) => setFiltroTipoAjuste(e.target.value)}>
            <option value="">Cualquier ajuste</option>
            <option value="BONO">Bono</option>
            <option value="PENALIZACION">Penalización</option>
          </select>
        </div>
      </div>

      <div className="table-container">
        {loading && <p style={{ color: '#9CA3AF' }}>Cargando...</p>}
        {error && (
          <div>
            <p style={{ color: '#EF4444' }}>{error}</p>
            <button onClick={retry} className="btn-secondary" style={{ marginTop: '0.5rem' }}>
              Reintentar
            </button>
          </div>
        )}
        {!loading && !error && <LiquidacionesTable liquidaciones={liquidaciones} />}

        <div className="pagination-controls">
          <button onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>
            Anterior
          </button>
          <span>Mostrando {liquidaciones.length} de {todasLiquidaciones.length} liquidaciones</span>
          <button
            onClick={() => setPage((p) => Math.min(totalPaginas - 1, p + 1))}
            disabled={page >= totalPaginas - 1}
          >
            Siguiente
          </button>
        </div>
      </div>

      <div className="liq-summary-cards">
        <SummaryCard
          label="TOTAL PENDIENTE"
          value={formatCurrency(totalPendiente)}
          subtitle="+12% vs mes anterior"
          accentColor="#60A5FA"
        />
        <SummaryCard
          label="LIQUIDACIONES EN REVISIÓN"
          value={enRevision}
          subtitle="Pendientes de firma"
          accentColor="#F59E0B"
        />
        <SummaryCard
          label="PENALIDADES DEL MES"
          value={formatCurrency(penalidades)}
          subtitle="incidentes registrados"
          accentColor="#F87171"
        />
      </div>
    </div>
  );
};

export default LiquidacionesPage;
