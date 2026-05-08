-- Generated Test Data for 20261LogisticsInvoice

-- ============================================================
-- 1. TRANSPORTISTA
-- ============================================================
INSERT INTO transportista (id, nombre) VALUES
    ('a1b2c3d4-e5f6-7890-1234-567890abcdef', 'Transportes Veloz');

-- ============================================================
-- 2. RUTA
-- ============================================================
INSERT INTO ruta (id, ruta_id, transportista_id, vehiculo_id, tipo_vehiculo, modelo_contrato, fecha_inicio_transito, fecha_cierre, estado_procesamiento) 
VALUES ('55555555-5555-5555-5555-555555555555', 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', '11111111-1111-1111-1111-111111111111', 'CAMION', 'REPARTO', '2024-05-20 08:00:00', '2024-05-20 18:00:00', 'PROCESADO');

-- ============================================================
-- 3. PARADA
-- ============================================================
INSERT INTO parada (id, paquete_id, parada_id, ruta_entity_id, estado, motivo_falla) VALUES
    ('66666666-6666-6666-6666-666666666661', 'aaaaaaaa-0000-0000-0000-000000000001', 'bbbbbbbb-0000-0000-0000-000000000001', '55555555-5555-5555-5555-555555555555', 'ENTREGADO', NULL),
    ('66666666-6666-6666-6666-666666666663', 'aaaaaaaa-0000-0000-0000-000000000003', 'bbbbbbbb-0000-0000-0000-000000000003', '55555555-5555-5555-5555-555555555555', 'FALLIDO', 'DIRECCION_INCORRECTA');

-- ============================================================
-- 4. SEGUROS
-- ============================================================
INSERT INTO seguros (id_seguro, numero_poliza, estado) VALUES
    ('33333333-3333-3333-3333-333333333333', 'POL-2024-98765', 'ACTIVO');

-- ============================================================
-- 5. VEHICULOS
-- ============================================================
INSERT INTO vehiculos (id_vehiculo, id_transportista, tipo) VALUES
    ('11111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', 'CAMION');

-- ============================================================
-- 6. CONTRATOS
-- ============================================================
INSERT INTO contratos (id, tipo_contratacion, tarifa, id_contrato, tipo_contrato, es_por_parada, precio_paradas, precio, tipo_vehiculo, fecha_inicio, fecha_final, id_transportista, id_seguro) 
VALUES ('44444444-4444-4444-4444-444444444444', 'TARIFA_PLANA', 150.0000, 'CONTR-001', 'PARADA', true, 150.00, NULL, 'CAMION', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', '33333333-3333-3333-3333-333333333333');

-- ============================================================
-- 7. RUTAS (Read Model)
-- ============================================================
INSERT INTO rutas (id, fecha_inicio, fecha_cierre, tipo_vehiculo, precio_parada, numero_paradas) 
VALUES ('55555555-5555-5555-5555-555555555555', '2024-05-20 08:00:00', '2024-05-20 18:00:00', 'CAMION', 150.00, 3);

-- ============================================================
-- 8. LIQUIDACIONES
-- ============================================================
INSERT INTO liquidaciones (id, id_ruta, id_contrato, estado, valor_base, valor_final, fecha_calculo, solicitud_revision_aceptada, created_at, updated_at, estado_liquidacion, monto_bruto, monto_neto, usuario_id) 
VALUES ('77777777-7777-7777-7777-777777777777', '55555555-5555-5555-5555-555555555555', '44444444-4444-4444-4444-444444444444', 'APROBADA', 300.0000, 350.0000, NOW(), false, NOW(), NOW(), 'APROBADA', 300.00, 350.00, 'a1b2c3d4-e5f6-7890-1234-567890abcdef');

-- ============================================================
-- 9. AJUSTES
-- ============================================================
INSERT INTO ajustes (id, id_liquidacion, tipo, monto, motivo, created_at, updated_at) 
VALUES ('88888888-8888-8888-8888-888888888888', '77777777-7777-7777-7777-777777777777', 'BONIFICACION', 50.0000, 'Bono por buen servicio', NOW(), NOW());

-- ============================================================
-- 10. PAQUETES / HISTORIAL
-- ============================================================
INSERT INTO paquetes (id_paquete, id_ruta, estado_actual, updated_at) VALUES (101, 55555555, 'ENTREGADO', NOW());
INSERT INTO historial_estados (id_paquete, estado, fecha) VALUES (101, 'ENTREGADO', '2024-05-20 14:00:00');

-- ============================================================
-- 11. REGISTRO PAGO
-- ============================================================
INSERT INTO liquidaciones_referencia (id_liquidacion) VALUES ('77777777-7777-7777-7777-777777777777');

INSERT INTO pagos (id_pago, id_usuario, monto_base, fecha, monto_neto, id_liquidacion, estado_actual, fecha_ultima_actualizacion, ultima_secuencia_procesada) 
VALUES ('99999999-9999-9999-9999-999999999999', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', 350.00, NOW(), 350.00, '77777777-7777-7777-7777-777777777777', 'PAGADO', NOW(), 1);

INSERT INTO estados_pago (id_estado_pago, id_pago, estado, fecha_registro, secuencia_evento) 
VALUES (gen_random_uuid(), '99999999-9999-9999-9999-999999999999', 'PAGADO', NOW(), 1);
