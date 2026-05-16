import React from 'react';
import { usePagos } from '../hooks/usePagos';
import PagosTable from '../components/PagosTable';

const PagosPage = () => {
  const { pagos, loading, error, retry } = usePagos();

  return (
    <div>
      <div className="page-header">
        <h1>Estado de Pagos</h1>
        <p>Consulta el estado de los pagos asociados a liquidaciones.</p>
      </div>

      <div className="table-container">
        {loading && <p>Cargando...</p>}
        {error && (
          <div>
            <p style={{ color: 'red' }}>{error}</p>
            <button onClick={retry} style={{ marginTop: '0.5rem' }}>Reintentar</button>
          </div>
        )}
        {!loading && !error && <PagosTable pagos={pagos} />}
      </div>
    </div>
  );
};

export default PagosPage;
