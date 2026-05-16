import { useState, useCallback } from 'react';

// Datos estáticos de demostración — el endpoint GET /api/pagos aún no está implementado.
const MOCK_PAGOS = [
  {
    pago_id: 'a1b2c3d4-0001-4e5f-8a9b-000000000001',
    liquidacion_id: 'f9e8d7c6-0001-4b3a-92e1-111111111111',
    monto: 4850000,
    estado: 'PAGADO',
    fecha: '2026-05-01T10:30:00',
  },
  {
    pago_id: 'a1b2c3d4-0002-4e5f-8a9b-000000000002',
    liquidacion_id: 'f9e8d7c6-0002-4b3a-92e1-222222222222',
    monto: 3120500,
    estado: 'EN_PROCESO',
    fecha: '2026-05-05T14:15:00',
  },
  {
    pago_id: 'a1b2c3d4-0003-4e5f-8a9b-000000000003',
    liquidacion_id: 'f9e8d7c6-0003-4b3a-92e1-333333333333',
    monto: 7650000,
    estado: 'PENDIENTE',
    fecha: '2026-05-08T09:00:00',
  },
  {
    pago_id: 'a1b2c3d4-0004-4e5f-8a9b-000000000004',
    liquidacion_id: 'f9e8d7c6-0004-4b3a-92e1-444444444444',
    monto: 2300750,
    estado: 'RECHAZADO',
    fecha: '2026-05-03T16:45:00',
  },
  {
    pago_id: 'a1b2c3d4-0005-4e5f-8a9b-000000000005',
    liquidacion_id: 'f9e8d7c6-0005-4b3a-92e1-555555555555',
    monto: 5975200,
    estado: 'PAGADO',
    fecha: '2026-05-10T11:20:00',
  },
];

export const usePagos = () => {
  const [pagos] = useState(MOCK_PAGOS);

  // retry es no-op mientras los datos sean estáticos
  const retry = useCallback(() => {}, []);

  return { pagos, loading: false, error: null, retry };
};
