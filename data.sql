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

-- Data Generation

-- 1. Transportista
INSERT INTO transportista (id, nombre) VALUES
('a1b2c3d4-e5f6-7890-1234-567890abcdef', 'Transportes Veloz');

-- 2. Vehiculos
INSERT INTO vehiculos (id_vehiculo, id_transportista, tipo) VALUES
('v1a2b3c4-d5e6-f789-0123-456789abcdef', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', 'CAMION'),
('v2a2b3c4-d5e6-f789-0123-456789abcdef', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', 'FURGONETA');

-- 3. Seguros
INSERT INTO seguros (id_seguro, numero_poliza, estado) VALUES
('s1a2b3c4-d5e6-f789-0123-456789abcdef', 'POL-2024-98765', 'ACTIVO');

-- 4. Contratos
INSERT INTO contratos (id, id_contrato, tipo_contrato, es_por_parada, precio_paradas, precio, tipo_vehiculo, fecha_inicio, fecha_final, id_transportista, id_seguro, tipo_contratacion, tarifa, created_at) VALUES
('c1a2b3c4-d5e6-f789-0123-456789abcdef', 'CONTR-001', 'REPARTO', true, 150.00, null, 'CAMION', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', 's1a2b3c4-d5e6-f789-0123-456789abcdef', 'TARIFA_PLANA', 150.0000, NOW());

-- 5. Ruta
INSERT INTO ruta (id, ruta_id, transportista_id, vehiculo_id, tipo_vehiculo, modelo_contrato, fecha_inicio_transito, fecha_cierre, estado_procesamiento) VALUES
('r1a2b3c4-d5e6-f789-0123-456789abcdef', 'r-negocio-1a2b3c4d', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', 'v1a2b3c4-d5e6-f789-0123-456789abcdef', 'CAMION', 'REPARTO', '2024-05-20 08:00:00', '2024-05-20 18:00:00', 'PROCESADO');

-- 6. Parada
INSERT INTO parada (id, paquete_id, parada_id, ruta_entity_id, estado, motivo_falla) VALUES
('p1a2b3c4-d5e6-f789-0123-456789abcdef', 'pkg-001', 'parada-001', 'r1a2b3c4-d5e6-f789-0123-456789abcdef', 'ENTREGADO', null),
('p2a2b3c4-d5e6-f789-0123-456789abcdef', 'pkg-002', 'parada-002', 'r1a2b3c4-d5e6-f789-0123-456789abcdef', 'ENTREGADO', null),
('p3a2b3c4-d5e6-f789-0123-456789abcdef', 'pkg-003', 'parada-003', 'r1a2b3c4-d5e6-f789-0123-456789abcdef', 'FALLIDO', 'DIRECCION_INCORRECTA');

-- 7. Liquidaciones
-- Assuming 2 successful stops at 150 each = 300
INSERT INTO liquidaciones (id, created_at, updated_at, id_ruta, id_contrato, estado, valor_base, valor_final, fecha_calculo, solicitud_revision_aceptada, version) VALUES
('l1a2b3c4-d5e6-f789-0123-456789abcdef', NOW(), NOW(), 'r-negocio-1a2b3c4d', 'c1a2b3c4-d5e6-f789-0123-456789abcdef', 'GENERADA', 300.0000, 300.0000, NOW(), false, 0);

-- 8. Ajustes
-- Adding a bonus of 50
INSERT INTO ajustes (id, created_at, updated_at, id_liquidacion, tipo, monto, motivo) VALUES
('aj1a2b3c4-d5e6-f789-0123-456789abcdef', NOW(), NOW(), 'l1a2b3c4-d5e6-f789-0123-456789abcdef', 'BONIFICACION', 50.0000, 'Bono por buen servicio');

-- Update liquidacion final value after adjustment
UPDATE liquidaciones SET valor_final = 350.0000, estado = 'APROBADA' WHERE id = 'l1a2b3c4-d5e6-f789-0123-456789abcdef';

-- 9. Pagos
INSERT INTO pagos (id_pago, id_usuario, montoBase, fecha, id_penalidad, montoNeto, id_liquidacion, estado_actual, fecha_ultima_actualizacion, ultima_secuencia_procesada, version) VALUES
('pay1a2b3c4-d5e6-f789-0123-456789abcdef', '00000000-0000-0000-0000-000000000000', 350.00, NOW(), null, 350.00, 'l1a2b3c4-d5e6-f789-0123-456789abcdef', 'PENDIENTE', NOW(), 0, 0);

-- 10. Eventos Transaccion
INSERT INTO eventos_transaccion (id_evento, id_transaccion_banco, id_pago, id_liquidacion, payload_recibido, fecha_recepcion, fecha_evento_banco, secuencia, estado_solicitado, estado_procesamiento, procesado) VALUES
('et1a2b3c4-d5e6-f789-0123-456789abcdef', 'banco-trans-001', 'pay1a2b3c4-d5e6-f789-0123-456789abcdef', 'l1a2b3c4-d5e6-f789-0123-456789abcdef', '{"status":"completed"}', NOW(), NOW(), 1, 'PAGADO', 'PROCESADO', true);

-- Update pago status after transaction event
UPDATE pagos SET estado_actual = 'PAGADO', ultima_secuencia_procesada = 1 WHERE id_pago = 'pay1a2b3c4-d5e6-f789-0123-456789abcdef';
