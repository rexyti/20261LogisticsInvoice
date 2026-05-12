
import React from 'react';
import { useRutas } from '../hooks/useRutas';
import RutasTable from '../components/RutasTable';

const RutasPage = () => {
  const { rutas, loading, error, page, totalPages, setPage, setFilter } = useRutas();

  const handleFilterChange = (e) => {
    setFilter(e.target.value || null);
    setPage(0); // Reset to first page on filter change
  };

  return (
    <div>
      <div className="page-header">
        <h1>Cierre de Rutas</h1>
        <p>Gestión y seguimiento de las rutas finalizadas.</p>
      </div>

      <div className="controls-container">
        <div className="filter-group">
          <label htmlFor="status-filter">Filtrar por estado:</label>
          <select id="status-filter" onChange={handleFilterChange}>
            <option value="">Todos</option>
            <option value="PENDIENTE">Pendiente</option>
            <option value="PROCESADO">Procesado</option>
            <option value="ERROR">Error</option>
          </select>
        </div>
        {/* Add other controls like buttons here if needed */}
      </div>

      <div className="table-container">
        {loading && <p>Cargando...</p>}
        {error && <p style={{ color: 'red' }}>{error}</p>}
        
        {!loading && !error && <RutasTable rutas={rutas} />}

        <div className="pagination-controls">
          <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}>
            Anterior
          </button>
          <span> Página {page + 1} de {totalPages} </span>
          <button onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))} disabled={page >= totalPages - 1}>
            Siguiente
          </button>
        </div>
      </div>
    </div>
  );
};

export default RutasPage;
