import React from 'react';
import { usePagos } from '../hooks/usePagos';
import PagosTable from '../components/PagosTable';

const PagosPage = () => {
  const { pagos, loading, error, retry } = usePagos();

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1.5rem' }}>
        <div>
          <h1 style={{ margin: '0 0 0.25rem' }}>Estado de Pagos</h1>
          <p style={{ margin: 0, color: '#9CA3AF', fontSize: '0.9rem' }}>
            Consulta el estado de los pagos asociados a liquidaciones.
          </p>
        </div>
      </div>

      <div className="table-container">
        {loading && <p style={{ color: '#9CA3AF' }}>Cargando...</p>}
        {error && (
          <div>
            <p style={{ color: '#EF4444' }}>{error}</p>
            <button onClick={retry} style={{ marginTop: '0.5rem' }}>Reintentar</button>
          </div>
        )}
        {!loading && !error && <PagosTable pagos={pagos} />}
      </div>
    </div>
  );
};

export default PagosPage;
