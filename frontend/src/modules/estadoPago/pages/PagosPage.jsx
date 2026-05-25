import React, { useState } from 'react';
import { usePagos } from '../hooks/usePagos';
import PagosTable from '../components/PagosTable';
import RegistrarEstadoForm from '../components/RegistrarEstadoForm';

const PagosPage = () => {
  const { pagos, loading, error, retry } = usePagos();
  const [mostrarFormulario, setMostrarFormulario] = useState(false);

  const handleSuccess = () => {
    retry();
    setMostrarFormulario(false);
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1.5rem' }}>
        <div>
          <h1 style={{ margin: '0 0 0.25rem' }}>Estado de Pagos</h1>
          <p style={{ margin: 0, color: '#9CA3AF', fontSize: '0.9rem' }}>
            Consulta el estado de los pagos asociados a liquidaciones.
          </p>
        </div>
        <button
          onClick={() => setMostrarFormulario((v) => !v)}
          style={{
            background: mostrarFormulario ? '#374151' : '#4F46E5',
            color: '#fff',
            border: 'none',
            borderRadius: '0.5rem',
            padding: '0.6rem 1.25rem',
            fontSize: '0.875rem',
            fontWeight: 600,
            cursor: 'pointer',
            flexShrink: 0,
          }}
        >
          {mostrarFormulario ? '✕ Cancelar' : '+ Registrar Estado'}
        </button>
      </div>

      {mostrarFormulario && <RegistrarEstadoForm onSuccess={handleSuccess} />}

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
