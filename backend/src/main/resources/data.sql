-- Generated Test Data for 20261LogisticsInvoice

-- De-comment and execute the following lines if you want to clear the tables before inserting new data.
-- DELETE FROM eventos_transaccion;
-- DELETE FROM pagos;
-- DELETE FROM ajustes;
-- DELETE FROM liquidaciones;
-- DELETE FROM parada;
-- DELETE FROM ruta;
-- DELETE FROM contratos;
-- DELETE FROM seguros;
-- DELETE FROM vehiculos;
-- DELETE FROM transportista;

-- Insertion Order:
-- 1. transportista
-- 2. vehiculos
-- 3. seguros
-- 4. contratos
-- 5. ruta
-- 6. parada
-- 7. liquidaciones
-- 8. ajustes
-- 9. pagos
-- 10. eventos_transaccion

-- ============================================================
-- CLEAN DATASET - COMPATIBLE CON V1__init_clean_schema.sql
-- ============================================================

-- ============================================================
-- 1. TRANSPORTISTA
-- ============================================================
INSERT INTO transportista (id, nombre) VALUES
    ('a1b2c3d4-e5f6-7890-1234-567890abcdef', 'Transportes Veloz');

-- ============================================================
-- 2. VEHICULOS
-- ============================================================
INSERT INTO vehiculos (id_vehiculo, id_transportista, tipo) VALUES
                                                                ('11111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', 'CAMION'),
                                                                ('22222222-2222-2222-2222-222222222222', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', 'FURGONETA');
-- ============================================================
-- 3. SEGUROS
-- ============================================================
INSERT INTO seguros (id_seguro, numero_poliza, estado) VALUES
    ('33333333-3333-3333-3333-333333333333', 'POL-2024-98765', 'ACTIVO');
-- ============================================================
-- 4. CONTRATOS
-- ============================================================
INSERT INTO contratos (
    id,
    tipo_contratacion,
    tarifa,
    id_contrato,
    tipo_contrato,
    es_por_parada,
    precio_paradas,
    precio,
    tipo_vehiculo,
    fecha_inicio,
    fecha_final,
    id_transportista,
    id_seguro,
    created_at
) VALUES (
             '44444444-4444-4444-4444-444444444444',
             'TARIFA_PLANA',
             150.0000,
             'CONTR-001',
             'PARADA',
             true,
             150.00,
             NULL,
             'CAMION',
             '2024-01-01 00:00:00',
             '2024-12-31 23:59:59',
             'a1b2c3d4-e5f6-7890-1234-567890abcdef',
             '33333333-3333-3333-3333-333333333333',
             NOW()
         );
-- ============================================================
-- 5. RUTA
-- ============================================================
INSERT INTO ruta (
    id,
    ruta_id,
    transportista_id,
    vehiculo_id,
    tipo_vehiculo,
    modelo_contrato,
    fecha_inicio_transito,
    fecha_cierre,
    estado_procesamiento
) VALUES (
             '55555555-5555-5555-5555-555555555555',
             'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee',
             'a1b2c3d4-e5f6-7890-1234-567890abcdef',
             '11111111-1111-1111-1111-111111111111',
             'CAMION',
             'REPARTO',
             '2024-05-20 08:00:00',
             '2024-05-20 18:00:00',
             'PROCESADO'
         );

-- ============================================================
-- 6. PARADA
-- ============================================================
INSERT INTO parada (
    id,
    paquete_id,
    parada_id,
    ruta_entity_id,
    estado,
    motivo_falla
) VALUES
      (
          '66666666-6666-6666-6666-666666666661',
          'aaaaaaaa-0000-0000-0000-000000000001',
          'bbbbbbbb-0000-0000-0000-000000000001',
          '55555555-5555-5555-5555-555555555555',
          'ENTREGADO',
          NULL
      ),
      (
          '66666666-6666-6666-6666-666666666662',
          'aaaaaaaa-0000-0000-0000-000000000002',
          'bbbbbbbb-0000-0000-0000-000000000002',
          '55555555-5555-5555-5555-555555555555',
          'ENTREGADO',
          NULL
      ),
      (
          '66666666-6666-6666-6666-666666666663',
          'aaaaaaaa-0000-0000-0000-000000000003',
          'bbbbbbbb-0000-0000-0000-000000000003',
          '55555555-5555-5555-5555-555555555555',
          'FALLIDO',
          'DIRECCION_INCORRECTA'
      );
-- ============================================================
-- 7. LIQUIDACIONES
-- ============================================================
INSERT INTO liquidaciones (
    id,
    id_ruta,
    id_contrato,
    estado,
    valor_base,
    valor_final,
    fecha_calculo,
    solicitud_revision_aceptada,
    version,
    created_at,
    updated_at
) VALUES (
             '77777777-7777-7777-7777-777777777777',
             'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee',
             '44444444-4444-4444-4444-444444444444',
             'GENERADA',
             300.0000,
             300.0000,
             NOW(),
             false,
             0,
             NOW(),
             NOW()
         );

-- ============================================================
-- 8. AJUSTES
-- ============================================================
INSERT INTO ajustes (
    id,
    id_liquidacion,
    tipo,
    monto,
    motivo,
    created_at,
    updated_at
) VALUES (
             '88888888-8888-8888-8888-888888888888',
             '77777777-7777-7777-7777-777777777777',
             'BONIFICACION',
             50.0000,
             'Bono por buen servicio',
             NOW(),
             NOW()
         );

-- UPDATE LIQUIDACION
UPDATE liquidaciones
SET valor_final = 350.0000,
    estado = 'APROBADA'
WHERE id = '77777777-7777-7777-7777-777777777777';

-- ============================================================
-- 9. PAGOS
-- ============================================================
INSERT INTO pagos (
    id_pago,
    id_usuario,
    monto_base,
    fecha,
    id_penalidad,
    monto_neto,
    id_liquidacion,
    estado_actual,
    fecha_ultima_actualizacion,
    ultima_secuencia_procesada,
    version
) VALUES (
             '99999999-9999-9999-9999-999999999999',
             '00000000-0000-0000-0000-000000000000',
             350.00,
             NOW(),
             NULL,
             350.00,
             '77777777-7777-7777-7777-777777777777',
             'PENDIENTE',
             NOW(),
             0,
             0
         );

-- ============================================================
-- 10. EVENTOS TRANSACCION
-- ============================================================
INSERT INTO eventos_transaccion (
    id_evento,
    id_transaccion_banco,
    id_pago,
    id_liquidacion,
    payload_recibido,
    fecha_recepcion,
    fecha_evento_banco,
    secuencia,
    estado_solicitado,
    estado_procesamiento,
    procesado
) VALUES (
             'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
             'banco-trans-001',
             '99999999-9999-9999-9999-999999999999',
             '77777777-7777-7777-7777-777777777777',
             '{"status":"completed"}',
             NOW(),
             NOW(),
             1,
             'PAGADO',
             'PROCESADO',
             true
         );

-- UPDATE FINAL
UPDATE pagos
SET estado_actual = 'PAGADO',
    ultima_secuencia_procesada = 1
WHERE id_pago = '99999999-9999-9999-9999-999999999999';