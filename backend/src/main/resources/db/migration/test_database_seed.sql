-- ================================================================
-- test_database_seed.sql
-- Proyecto: 20261LogisticsInvoice (logistica_test)
-- Backend:  Spring Boot 3.2.4 | PostgreSQL 15 | Flyway
--
-- INSTRUCCIONES:
--   PASO 1: Ejecutar el bloque "PARTE A" conectado a la BD "postgres"
--   PASO 2: Conectar a "logistica_test" y ejecutar el resto del script
--
-- Credenciales (docker-compose.yml):
--   Host: localhost | Puerto: 5432
--   Usuario: luis   | Password: 123456789
-- ================================================================

-- ================================================================
-- PARTE A — Crear BD de prueba
-- Ejecutar conectado a la BD "postgres" (o cualquier otra BD existente)
-- En pgAdmin: click derecho sobre el servidor → Query Tool (conectado a postgres)
-- ================================================================

DROP DATABASE IF EXISTS logistica_test;
CREATE DATABASE logistica_test
    WITH OWNER     = luis
         ENCODING  = 'UTF8';

-- ================================================================
-- PARTE B — Conectar a logistica_test y ejecutar desde aquí
-- En pgAdmin: abrir Query Tool sobre "logistica_test"
-- En psql:    \c logistica_test
-- ================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ================================================================
-- ESQUEMA — Tablas (idéntico a V1__init_clean_schema.sql)
-- ================================================================

-- TRANSPORTISTA (entidad compartida: cierreRuta + contratos)
CREATE TABLE IF NOT EXISTS transportista (
    id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(255) NOT NULL
);

-- RUTA (write model — cierreRuta)
CREATE TABLE IF NOT EXISTS ruta (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ruta_id               UUID NOT NULL UNIQUE,
    transportista_id      UUID NOT NULL REFERENCES transportista(id),
    vehiculo_id           UUID,
    tipo_vehiculo         VARCHAR(50),
    modelo_contrato       VARCHAR(255),
    fecha_inicio_transito TIMESTAMP NOT NULL,
    fecha_cierre          TIMESTAMP NOT NULL,
    estado_procesamiento  VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS parada (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paquete_id     UUID,
    parada_id      UUID NOT NULL,
    ruta_entity_id UUID NOT NULL REFERENCES ruta(id),
    estado         VARCHAR(50) NOT NULL,
    motivo_falla   VARCHAR(100),
    CONSTRAINT uk_parada_parada_id_ruta_entity_id UNIQUE (parada_id, ruta_entity_id)
);

-- CONTRATOS
CREATE TABLE IF NOT EXISTS seguros (
    id_seguro     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_poliza VARCHAR(255) NOT NULL,
    estado        VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS vehiculos (
    id_vehiculo      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_transportista UUID NOT NULL REFERENCES transportista(id),
    tipo             VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS contratos (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tipo_contratacion VARCHAR(255) DEFAULT '',
    tarifa            NUMERIC(19, 4) DEFAULT 0,
    id_contrato       VARCHAR(100) UNIQUE,
    tipo_contrato     VARCHAR(255) NOT NULL DEFAULT '',
    es_por_parada     BOOLEAN NOT NULL DEFAULT FALSE,
    precio_paradas    NUMERIC(15, 2),
    precio            NUMERIC(15, 2),
    tipo_vehiculo     VARCHAR(100) NOT NULL DEFAULT '',
    fecha_inicio      TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_final       TIMESTAMP NOT NULL DEFAULT NOW(),
    id_transportista  UUID REFERENCES transportista(id),
    id_seguro         UUID REFERENCES seguros(id_seguro),
    created_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- RUTAS (read model — visualizarLiquidacion)
-- IMPORTANTE: rutas.id == liquidaciones.id_ruta (join sin FK de BD)
CREATE TABLE IF NOT EXISTS rutas (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fecha_inicio   TIMESTAMP NOT NULL,
    fecha_cierre   TIMESTAMP,
    tipo_vehiculo  VARCHAR(50),
    precio_parada  NUMERIC(12, 2),
    numero_paradas INTEGER
);

-- LIQUIDACIONES (write model + read model comparten tabla — CQRS)
CREATE TABLE IF NOT EXISTS liquidaciones (
    id                          UUID PRIMARY KEY,
    id_ruta                     UUID NOT NULL UNIQUE,
    id_contrato                 UUID NOT NULL REFERENCES contratos(id),
    -- write model: LiquidacionEntity
    estado                      VARCHAR(50) NOT NULL,
    valor_base                  NUMERIC(19, 4) NOT NULL DEFAULT 0,
    valor_final                 NUMERIC(19, 4) NOT NULL DEFAULT 0,
    fecha_calculo               TIMESTAMP WITH TIME ZONE NOT NULL,
    solicitud_revision_aceptada BOOLEAN NOT NULL DEFAULT FALSE,
    id_admin_revisor            UUID,
    fecha_aceptacion_revision   TIMESTAMP WITH TIME ZONE,
    version                     BIGINT DEFAULT 0,
    created_at                  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                  TIMESTAMP WITH TIME ZONE NOT NULL,
    -- read model: VisualizarLiquidacionEntity (nullable)
    estado_liquidacion          VARCHAR(50),
    monto_bruto                 NUMERIC(12, 2),
    monto_neto                  NUMERIC(12, 2),
    usuario_id                  VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ajustes (
    id             UUID PRIMARY KEY,
    id_liquidacion UUID NOT NULL REFERENCES liquidaciones(id),
    tipo           VARCHAR(100) NOT NULL,
    monto          NUMERIC(19, 4) NOT NULL,
    motivo         VARCHAR(500) NOT NULL DEFAULT '',
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS auditoria_liquidacion (
    id               UUID PRIMARY KEY,
    id_liquidacion   UUID NOT NULL,
    operacion        VARCHAR(50) NOT NULL,
    valor_anterior   NUMERIC(19, 4),
    valor_nuevo      NUMERIC(19, 4) NOT NULL,
    fecha_operacion  TIMESTAMP WITH TIME ZONE NOT NULL,
    tipo_responsable VARCHAR(50) NOT NULL,
    id_responsable   VARCHAR(100) NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL
);

-- NOVEDAD ESTADO PAQUETE
CREATE TABLE IF NOT EXISTS paquetes (
    id_paquete    UUID PRIMARY KEY,
    id_ruta       UUID NOT NULL,
    estado_actual VARCHAR(50),
    updated_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS historial_estados (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_paquete UUID NOT NULL,
    estado     VARCHAR(50) NOT NULL,
    fecha      TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS log_sincronizacion (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_paquete            UUID NOT NULL,
    codigo_respuesta_http INTEGER,
    json_recibido         TEXT,
    created_at            TIMESTAMP NOT NULL DEFAULT NOW()
);

-- REGISTRAR ESTADO PAGO
CREATE TABLE IF NOT EXISTS liquidaciones_referencia (
    id_liquidacion UUID PRIMARY KEY
);

-- PAGOS (write model + read model — VisualizarEstadoPago)
CREATE TABLE IF NOT EXISTS pagos (
    id_pago                    UUID PRIMARY KEY,
    id_usuario                 UUID,
    monto_base                 NUMERIC(18, 2),
    fecha                      TIMESTAMP,
    id_penalidad               UUID,
    monto_neto                 NUMERIC(18, 2),
    id_liquidacion             UUID NOT NULL,
    estado_actual              VARCHAR(50) NOT NULL,
    fecha_ultima_actualizacion TIMESTAMP NOT NULL,
    ultima_secuencia_procesada BIGINT NOT NULL,
    version                    BIGINT
);

CREATE TABLE IF NOT EXISTS estados_pago (
    id_estado_pago        UUID PRIMARY KEY,
    id_pago               UUID NOT NULL,
    estado                VARCHAR(50) NOT NULL,
    fecha_registro        TIMESTAMP NOT NULL,
    fecha_evento_banco    TIMESTAMP,
    secuencia_evento      BIGINT,
    id_evento_transaccion UUID
);

CREATE TABLE IF NOT EXISTS eventos_transaccion (
    id_evento              UUID PRIMARY KEY,
    id_transaccion_banco   VARCHAR(255) NOT NULL,
    id_pago                UUID NOT NULL,
    id_liquidacion         UUID NOT NULL,
    payload_recibido       TEXT,
    fecha_recepcion        TIMESTAMP NOT NULL,
    fecha_evento_banco     TIMESTAMP,
    secuencia              BIGINT,
    estado_solicitado      VARCHAR(50),
    estado_procesamiento   VARCHAR(50) NOT NULL,
    mensaje_error          TEXT,
    procesado              BOOLEAN NOT NULL,
    CONSTRAINT uk_id_transaccion_banco UNIQUE (id_transaccion_banco)
);

-- VISUALIZAR ESTADO PAGO (read model)
CREATE TABLE IF NOT EXISTS eventos (
    id    UUID PRIMARY KEY,
    tipo  VARCHAR(255),
    fecha TIMESTAMP,
    datos VARCHAR(255)
);

-- ÍNDICES
CREATE INDEX IF NOT EXISTS idx_liquidaciones_id_ruta      ON liquidaciones(id_ruta);
CREATE INDEX IF NOT EXISTS idx_liquidaciones_fecha_calculo ON liquidaciones(fecha_calculo DESC);
CREATE INDEX IF NOT EXISTS idx_ajustes_id_liquidacion      ON ajustes(id_liquidacion);
CREATE INDEX IF NOT EXISTS idx_auditoria_id_liquidacion    ON auditoria_liquidacion(id_liquidacion);
CREATE INDEX IF NOT EXISTS idx_historial_id_paquete        ON historial_estados(id_paquete);
CREATE INDEX IF NOT EXISTS idx_historial_fecha             ON historial_estados(fecha);
CREATE INDEX IF NOT EXISTS idx_log_id_paquete              ON log_sincronizacion(id_paquete);
CREATE INDEX IF NOT EXISTS idx_estado_pago_id_pago         ON estados_pago(id_pago);
CREATE INDEX IF NOT EXISTS idx_estado_pago_fecha           ON estados_pago(fecha_registro);
CREATE INDEX IF NOT EXISTS idx_evento_id_pago              ON eventos_transaccion(id_pago);
CREATE INDEX IF NOT EXISTS idx_evento_fecha_recepcion      ON eventos_transaccion(fecha_recepcion);

-- ================================================================
-- LIMPIAR datos previos (para re-ejecución segura)
-- ================================================================
DELETE FROM eventos;
DELETE FROM eventos_transaccion;
DELETE FROM estados_pago;
DELETE FROM pagos;
DELETE FROM liquidaciones_referencia;
DELETE FROM log_sincronizacion;
DELETE FROM historial_estados;
DELETE FROM paquetes;
DELETE FROM auditoria_liquidacion;
DELETE FROM ajustes;
DELETE FROM liquidaciones;
DELETE FROM parada;
DELETE FROM ruta;
DELETE FROM contratos;
DELETE FROM vehiculos;
DELETE FROM seguros;
DELETE FROM rutas;
DELETE FROM transportista;

-- ================================================================
-- DATOS DE PRUEBA
-- UUID fijos para poder referenciarlos en los endpoints de prueba
-- ================================================================

-- ============================================================
-- TRANSPORTISTAS
-- Prueba: GET /api/contratos → devuelve transportista anidado
-- ============================================================
INSERT INTO transportista (id, nombre) VALUES
    ('a1000001-0000-0000-0000-000000000001', 'Carlos López Pérez'),
    ('a1000001-0000-0000-0000-000000000002', 'María García Torres'),
    ('a1000001-0000-0000-0000-000000000003', 'Juan Rodríguez Mora');

-- ============================================================
-- SEGUROS (5 pólizas — estado variado)
-- Prueba: GET /api/contratos/{idContrato} → incluye seguro anidado
-- ============================================================
INSERT INTO seguros (id_seguro, numero_poliza, estado) VALUES
    ('b2000001-0000-0000-0000-000000000001', 'POL-2024-001', 'VIGENTE'),
    ('b2000001-0000-0000-0000-000000000002', 'POL-2024-002', 'VIGENTE'),
    ('b2000001-0000-0000-0000-000000000003', 'POL-2024-003', 'VIGENTE'),
    ('b2000001-0000-0000-0000-000000000004', 'POL-2023-004', 'VENCIDA'),
    ('b2000001-0000-0000-0000-000000000005', 'POL-2024-005', 'VIGENTE');

-- ============================================================
-- VEHÍCULOS (1 por transportista)
-- ============================================================
INSERT INTO vehiculos (id_vehiculo, id_transportista, tipo) VALUES
    ('c3000001-0000-0000-0000-000000000001', 'a1000001-0000-0000-0000-000000000001', 'CAMION'),
    ('c3000001-0000-0000-0000-000000000002', 'a1000001-0000-0000-0000-000000000002', 'FURGON'),
    ('c3000001-0000-0000-0000-000000000003', 'a1000001-0000-0000-0000-000000000003', 'MOTO');

-- ============================================================
-- CONTRATOS (5 contratos — tipos variados)
-- Prueba:
--   GET  /api/contratos                  → lista paginada (usa bearer gestor@test.com)
--   GET  /api/contratos/{idContrato}     → busca por "CON-2024-001", "CON-2024-002", etc.
--   POST /api/contratos                  → ver payload ejemplo más abajo
--
-- CON1: POR_PARADA,         CAMION, vigente → usado en LIQ1
-- CON2: RECORRIDO_COMPLETO, FURGON, vigente → usado en LIQ2
-- CON3: POR_PARADA,         MOTO,   vigente → usado en LIQ3
-- CON4: RECORRIDO_COMPLETO, CAMION, vencido → usado en LIQ4 (caso límite)
-- CON5: POR_PARADA,         MOTO,   vigente → usado en LIQ5
-- ============================================================
INSERT INTO contratos (id, id_contrato, tipo_contrato, tipo_contratacion, tarifa,
                       es_por_parada, precio_paradas, precio, tipo_vehiculo,
                       fecha_inicio, fecha_final, id_transportista, id_seguro, created_at) VALUES
    ('d4000001-0000-0000-0000-000000000001','CON-2024-001','SERVICIO_NACIONAL', 'POR_PARADA',         12000.0000, TRUE,  12000.00, NULL,      'CAMION','2024-01-01 00:00:00','2026-12-31 23:59:59','a1000001-0000-0000-0000-000000000001','b2000001-0000-0000-0000-000000000001', NOW()),
    ('d4000001-0000-0000-0000-000000000002','CON-2024-002','SERVICIO_REGIONAL', 'RECORRIDO_COMPLETO', 80000.0000, FALSE, NULL,      80000.00, 'FURGON','2024-03-01 00:00:00','2026-09-30 23:59:59','a1000001-0000-0000-0000-000000000002','b2000001-0000-0000-0000-000000000002', NOW()),
    ('d4000001-0000-0000-0000-000000000003','CON-2024-003','SERVICIO_URBANO',   'POR_PARADA',          6500.0000, TRUE,   6500.00, NULL,      'MOTO',  '2024-06-01 00:00:00','2026-12-31 23:59:59','a1000001-0000-0000-0000-000000000003','b2000001-0000-0000-0000-000000000003', NOW()),
    ('d4000001-0000-0000-0000-000000000004','CON-2023-004','SERVICIO_REGIONAL', 'RECORRIDO_COMPLETO', 60000.0000, FALSE, NULL,      60000.00, 'CAMION','2023-01-01 00:00:00','2024-12-31 23:59:59','a1000001-0000-0000-0000-000000000001','b2000001-0000-0000-0000-000000000004', NOW()),
    ('d4000001-0000-0000-0000-000000000005','CON-2024-005','SERVICIO_URBANO',   'POR_PARADA',          7500.0000, TRUE,   7500.00, NULL,      'MOTO',  '2024-08-01 00:00:00','2026-12-31 23:59:59','a1000001-0000-0000-0000-000000000003','b2000001-0000-0000-0000-000000000005', NOW());

/*
  PAYLOAD — POST /api/contratos
  Authorization: Bearer <token_gestor_financiero>   (gestor@test.com / 123456)
  {
    "id_contrato":    "CON-2024-006",
    "tipo_contrato":  "SERVICIO_NACIONAL",
    "transportista_id": "a1000001-0000-0000-0000-000000000002",
    "es_por_parada":  true,
    "precio_paradas": 11000.00,
    "tipo_vehiculo":  "CAMION",
    "fecha_inicio":   "2026-01-01T00:00:00",
    "fecha_final":    "2027-12-31T23:59:59",
    "seguro": { "numero_poliza": "POL-2024-006", "estado": "VIGENTE" }
  }
*/

-- ============================================================
-- RUTAS — read model (tabla "rutas" para visualizarLiquidacion)
-- CRÍTICO: rutas.id == liquidaciones.id_ruta (join sin FK)
-- ============================================================
INSERT INTO rutas (id, fecha_inicio, fecha_cierre, tipo_vehiculo, precio_parada, numero_paradas) VALUES
    ('e5000001-0000-0000-0000-000000000001', '2026-04-01 08:00:00', '2026-04-01 18:00:00', 'CAMION',  12000.00,  8),
    ('e5000001-0000-0000-0000-000000000002', '2026-04-10 09:00:00', '2026-04-10 17:30:00', 'FURGON',  NULL,      NULL),
    ('e5000001-0000-0000-0000-000000000003', '2026-04-20 07:30:00', '2026-04-20 16:00:00', 'MOTO',     6500.00, 12),
    ('e5000001-0000-0000-0000-000000000004', '2026-05-01 08:00:00', '2026-05-01 17:00:00', 'CAMION',  NULL,      NULL),
    ('e5000001-0000-0000-0000-000000000005', '2026-05-05 09:00:00', NULL,                  'MOTO',     7500.00,  6);

-- ============================================================
-- LIQUIDACIONES (write + read model — tabla compartida)
-- Prueba:
--   GET /api/liquidaciones          → lista paginada (todos los roles)
--   GET /api/liquidaciones/{id}     → detalle por UUID
--   GET /api/liquidaciones/buscar   → filtros
--   PUT /api/liquidaciones/{id}/recalcular → solo ADMIN
--
-- Estados disponibles: CALCULADA, RECALCULADA, PAGADA, PENDIENTE, ERROR
-- LIQ1 id: f6000001-0000-0000-0000-000000000001  → CALCULADA  (candidata para recalcular)
-- LIQ2 id: f6000001-0000-0000-0000-000000000002  → RECALCULADA (tiene ajustes)
-- LIQ3 id: f6000001-0000-0000-0000-000000000003  → PAGADA
-- LIQ4 id: f6000001-0000-0000-0000-000000000004  → CALCULADA  (candidata para recalcular)
-- LIQ5 id: f6000001-0000-0000-0000-000000000005  → ERROR
-- ============================================================
INSERT INTO liquidaciones (id, id_ruta, id_contrato, estado, valor_base, valor_final,
                           fecha_calculo, solicitud_revision_aceptada, version,
                           created_at, updated_at,
                           estado_liquidacion, monto_bruto, monto_neto, usuario_id) VALUES
    ('f6000001-0000-0000-0000-000000000001','e5000001-0000-0000-0000-000000000001','d4000001-0000-0000-0000-000000000001',
     'CALCULADA',    96000.0000,  96000.0000, '2026-04-01 20:00:00+00', FALSE, 0,
     '2026-04-01 20:00:00+00','2026-04-01 20:00:00+00',
     'CALCULADA',    96000.00,  96000.00, 'a1b2c3d4-e5f6-7890-1234-567890abcdef'),

    ('f6000001-0000-0000-0000-000000000002','e5000001-0000-0000-0000-000000000002','d4000001-0000-0000-0000-000000000002',
     'RECALCULADA',  80000.0000,  82000.0000, '2026-04-10 20:00:00+00', FALSE, 1,
     '2026-04-10 20:00:00+00','2026-04-11 10:00:00+00',
     'RECALCULADA',  80000.00,  82000.00, '00000000-0000-0000-0000-000000000001'),

    ('f6000001-0000-0000-0000-000000000003','e5000001-0000-0000-0000-000000000003','d4000001-0000-0000-0000-000000000003',
     'PAGADA',       78000.0000,  76500.0000, '2026-04-20 20:00:00+00', FALSE, 1,
     '2026-04-20 20:00:00+00','2026-04-22 09:00:00+00',
     'PAGADA',       78000.00,  76500.00, 'a1b2c3d4-e5f6-7890-1234-567890abcdef'),

    ('f6000001-0000-0000-0000-000000000004','e5000001-0000-0000-0000-000000000004','d4000001-0000-0000-0000-000000000004',
     'CALCULADA',    60000.0000,  60000.0000, '2026-05-01 20:00:00+00', FALSE, 0,
     '2026-05-01 20:00:00+00','2026-05-01 20:00:00+00',
     'CALCULADA',    60000.00,  60000.00, '00000000-0000-0000-0000-000000000001'),

    ('f6000001-0000-0000-0000-000000000005','e5000001-0000-0000-0000-000000000005','d4000001-0000-0000-0000-000000000005',
     'ERROR',             0.0000,      0.0000, '2026-05-05 20:00:00+00', FALSE, 0,
     '2026-05-05 20:00:00+00','2026-05-05 20:00:00+00',
     'ERROR',             0.00,      0.00, 'a1b2c3d4-e5f6-7890-1234-567890abcdef');

/*
  PAYLOAD — PUT /api/liquidaciones/f6000001-0000-0000-0000-000000000001/recalcular
  Authorization: Bearer <token_admin>    (admin@test.com / 123456)
  {
    "ajustes": [
      { "tipo": "BONO",         "monto": 5000.00, "motivo": "Entrega puntual en zona difícil" },
      { "tipo": "PENALIZACION", "monto": 2000.00, "motivo": "Paquete dañado en tránsito"      }
    ],
    "responsable": "admin@test.com"
  }
*/

-- ============================================================
-- AJUSTES (bonos y penalizaciones)
-- Prueba: GET /api/liquidaciones/{id} → campo "ajustes" en respuesta
-- LIQ2 tiene BONO(+5000) y PENALIZACION(-3000) → monto_neto = 82000
-- LIQ3 tiene PENALIZACION(-1500)                → monto_neto = 76500
-- ============================================================
INSERT INTO ajustes (id, id_liquidacion, tipo, monto, motivo, created_at, updated_at) VALUES
    ('17000001-0000-0000-0000-000000000001','f6000001-0000-0000-0000-000000000002',
     'BONO',         5000.0000, 'Entrega puntual en zona de difícil acceso',
     '2026-04-11 10:00:00+00','2026-04-11 10:00:00+00'),

    ('17000001-0000-0000-0000-000000000002','f6000001-0000-0000-0000-000000000002',
     'PENALIZACION', 3000.0000, 'Retraso en 3 paradas del sector norte',
     '2026-04-11 10:00:00+00','2026-04-11 10:00:00+00'),

    ('17000001-0000-0000-0000-000000000003','f6000001-0000-0000-0000-000000000003',
     'PENALIZACION', 1500.0000, 'Paquete dañado durante el tránsito — responsabilidad TT',
     '2026-04-22 09:00:00+00','2026-04-22 09:00:00+00');

-- ============================================================
-- AUDITORÍA DE LIQUIDACIÓN
-- Trazabilidad interna de cálculos (no hay endpoint GET directo)
-- ============================================================
INSERT INTO auditoria_liquidacion
    (id, id_liquidacion, operacion, valor_anterior, valor_nuevo,
     fecha_operacion, tipo_responsable, id_responsable, created_at) VALUES
    ('28000001-0000-0000-0000-000000000001','f6000001-0000-0000-0000-000000000001',
     'CALCULO',   NULL,       96000.0000, '2026-04-01 20:00:00+00','SISTEMA',        'evento-cierre-ruta','2026-04-01 20:00:00+00'),
    ('28000001-0000-0000-0000-000000000002','f6000001-0000-0000-0000-000000000002',
     'CALCULO',   NULL,       80000.0000, '2026-04-10 20:00:00+00','SISTEMA',        'evento-cierre-ruta','2026-04-10 20:00:00+00'),
    ('28000001-0000-0000-0000-000000000003','f6000001-0000-0000-0000-000000000002',
     'RECALCULO', 80000.0000, 82000.0000, '2026-04-11 10:00:00+00','ADMINISTRADOR', '00000000-0000-0000-0000-000000000001','2026-04-11 10:00:00+00'),
    ('28000001-0000-0000-0000-000000000004','f6000001-0000-0000-0000-000000000003',
     'CALCULO',   NULL,       78000.0000, '2026-04-20 20:00:00+00','SISTEMA',        'evento-cierre-ruta','2026-04-20 20:00:00+00'),
    ('28000001-0000-0000-0000-000000000005','f6000001-0000-0000-0000-000000000003',
     'RECALCULO', 78000.0000, 76500.0000, '2026-04-22 09:00:00+00','ADMINISTRADOR', '00000000-0000-0000-0000-000000000001','2026-04-22 09:00:00+00');

-- ============================================================
-- RUTA — write model (tabla "ruta" para cierreRuta)
-- Prueba:
--   GET /api/v1/rutas/{id}          → por id técnico (UUID del registro)
--   GET /api/v1/rutas               → lista paginada, filtrable por estado_procesamiento
--   POST /api/v1/rutas/cerrar       → ver payload ejemplo más abajo
--
-- RW4 NO tiene liquidacion asociada → segura para eliminar en tests DELETE
-- NOTA: ruta.ruta_id coincide con rutas.id para que ambos modelos sean consistentes
-- ============================================================
INSERT INTO ruta (id, ruta_id, transportista_id, vehiculo_id, tipo_vehiculo,
                  modelo_contrato, fecha_inicio_transito, fecha_cierre, estado_procesamiento) VALUES
    ('39000001-0000-0000-0000-000000000001','e5000001-0000-0000-0000-000000000001',
     'a1000001-0000-0000-0000-000000000001','c3000001-0000-0000-0000-000000000001',
     'CAMION','CON-2024-001','2026-04-01 08:00:00','2026-04-01 18:00:00','OK'),

    ('39000001-0000-0000-0000-000000000002','e5000001-0000-0000-0000-000000000002',
     'a1000001-0000-0000-0000-000000000002','c3000001-0000-0000-0000-000000000002',
     'FURGON','CON-2024-002','2026-04-10 09:00:00','2026-04-10 17:30:00','REQUIERE_REVISION'),

    ('39000001-0000-0000-0000-000000000003','e5000001-0000-0000-0000-000000000003',
     'a1000001-0000-0000-0000-000000000003','c3000001-0000-0000-0000-000000000003',
     'MOTO',  'CON-2024-003','2026-04-20 07:30:00','2026-04-20 16:00:00','OK'),

    ('39000001-0000-0000-0000-000000000004','f1000004-0000-0000-0000-000000000004',
     'a1000001-0000-0000-0000-000000000001','c3000001-0000-0000-0000-000000000001',
     'CAMION','CON-2024-001','2026-05-10 08:00:00','2026-05-10 17:00:00','OK');

/*
  PAYLOAD — POST /api/v1/rutas/cerrar   (endpoint público, sin token)
  {
    "tipo_evento": "RUTA_CERRADA",
    "ruta_id":     "f2000099-0000-0000-0000-000000000099",
    "fecha_hora_inicio_transito": "2026-05-11T08:00:00",
    "fecha_hora_cierre":          "2026-05-11T17:00:00",
    "conductor": {
      "conductor_id":    "a1b2c3d4-e5f6-7890-1234-567890abcdef",
      "nombre":          "Carlos López Pérez",
      "modelo_contrato": "CON-2024-001"
    },
    "vehiculo": {
      "vehiculo_id": "c3000001-0000-0000-0000-000000000001",
      "tipo":        "CAMION"
    },
    "paradas": [
      { "parada_id": "aa000001-0000-0000-0000-000000000001", "paquete_id": "bb000001-0000-0000-0000-000000000001", "estado": "EXITOSA" },
      { "parada_id": "aa000001-0000-0000-0000-000000000002", "paquete_id": "bb000001-0000-0000-0000-000000000002", "estado": "FALLIDA", "motivo_no_entrega": "CLIENTE_AUSENTE" }
    ]
  }
*/

-- ============================================================
-- PARADAS (asociadas a rutas — cierreRuta)
-- Prueba: GET /api/v1/rutas/{id} → campo "paradas"
-- Cubre: EXITOSA, FALLIDA con CLIENTE_AUSENTE, PAQUETE_DANADO, DIRECCION_ERRONEA
-- ============================================================
INSERT INTO parada (id, parada_id, paquete_id, ruta_entity_id, estado, motivo_falla) VALUES
    -- Ruta 1 (39..001): 2 exitosas + 1 fallida cliente
    ('4a000001-0000-0000-0000-000000000001','pa000001-0000-0000-0000-000000000001','5b000001-0000-0000-0000-000000000001','39000001-0000-0000-0000-000000000001','EXITOSA', NULL),
    ('4a000001-0000-0000-0000-000000000002','pa000001-0000-0000-0000-000000000002','5b000001-0000-0000-0000-000000000002','39000001-0000-0000-0000-000000000001','EXITOSA', NULL),
    ('4a000001-0000-0000-0000-000000000003','pa000001-0000-0000-0000-000000000003','5b000001-0000-0000-0000-000000000003','39000001-0000-0000-0000-000000000001','FALLIDA','CLIENTE_AUSENTE'),
    -- Ruta 2 (39..002): 1 exitosa + 1 fallida transportista
    ('4a000001-0000-0000-0000-000000000004','pa000001-0000-0000-0000-000000000004','5b000001-0000-0000-0000-000000000004','39000001-0000-0000-0000-000000000002','EXITOSA', NULL),
    ('4a000001-0000-0000-0000-000000000005','pa000001-0000-0000-0000-000000000005','5b000001-0000-0000-0000-000000000005','39000001-0000-0000-0000-000000000002','FALLIDA','PAQUETE_DANADO'),
    -- Ruta 3 (39..003): 2 exitosas
    ('4a000001-0000-0000-0000-000000000006','pa000001-0000-0000-0000-000000000006','5b000001-0000-0000-0000-000000000006','39000001-0000-0000-0000-000000000003','EXITOSA', NULL),
    ('4a000001-0000-0000-0000-000000000007','pa000001-0000-0000-0000-000000000007','5b000001-0000-0000-0000-000000000007','39000001-0000-0000-0000-000000000003','EXITOSA', NULL),
    -- Ruta 4 (39..004): 1 exitosa + 1 fallida dirección
    ('4a000001-0000-0000-0000-000000000008','pa000001-0000-0000-0000-000000000008','5b000001-0000-0000-0000-000000000008','39000001-0000-0000-0000-000000000004','EXITOSA', NULL),
    ('4a000001-0000-0000-0000-000000000009','pa000001-0000-0000-0000-000000000009','5b000001-0000-0000-0000-000000000009','39000001-0000-0000-0000-000000000004','FALLIDA','DIRECCION_ERRONEA');

-- ============================================================
-- PAQUETES (novedadEstadoPaquete)
-- Prueba:
--   GET  /api/paquetes/{idPaquete}/historial
--   POST /api/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar
--
-- IDs de prueba:
--   5b000001-0000-0000-0000-000000000001  → sincronizar con idRuta 39000001-...-001
--   5b000001-0000-0000-0000-000000000003  → estado DEVUELTO
-- ============================================================
INSERT INTO paquetes (id_paquete, id_ruta, estado_actual, updated_at) VALUES
    ('5b000001-0000-0000-0000-000000000001','39000001-0000-0000-0000-000000000001','ENTREGADO','2026-04-01 18:30:00'),
    ('5b000001-0000-0000-0000-000000000002','39000001-0000-0000-0000-000000000001','ENTREGADO','2026-04-01 19:00:00'),
    ('5b000001-0000-0000-0000-000000000003','39000001-0000-0000-0000-000000000001','DEVUELTO', '2026-04-01 20:00:00'),
    ('5b000001-0000-0000-0000-000000000004','39000001-0000-0000-0000-000000000002','ENTREGADO','2026-04-10 18:00:00'),
    ('5b000001-0000-0000-0000-000000000005','39000001-0000-0000-0000-000000000002','DANADO',   '2026-04-10 19:30:00'),
    ('5b000001-0000-0000-0000-000000000006','39000001-0000-0000-0000-000000000003','ENTREGADO','2026-04-20 16:30:00'),
    ('5b000001-0000-0000-0000-000000000007','39000001-0000-0000-0000-000000000003','ENTREGADO','2026-04-20 17:00:00');

-- ============================================================
-- HISTORIAL DE ESTADOS (novedadEstadoPaquete)
-- Prueba: GET /api/paquetes/{idPaquete}/historial
-- PKG1: EN_TRANSITO → ENTREGADO
-- PKG3: EN_TRANSITO → INTENTO_FALLIDO → DEVUELTO
-- PKG5: EN_TRANSITO → DANADO
-- ============================================================
INSERT INTO historial_estados (id, id_paquete, estado, fecha) VALUES
    ('60000001-0000-0000-0000-000000000001','5b000001-0000-0000-0000-000000000001','EN_TRANSITO',    '2026-04-01 08:30:00'),
    ('60000001-0000-0000-0000-000000000002','5b000001-0000-0000-0000-000000000001','ENTREGADO',      '2026-04-01 11:00:00'),
    ('60000001-0000-0000-0000-000000000003','5b000001-0000-0000-0000-000000000003','EN_TRANSITO',    '2026-04-01 09:00:00'),
    ('60000001-0000-0000-0000-000000000004','5b000001-0000-0000-0000-000000000003','INTENTO_FALLIDO','2026-04-01 14:00:00'),
    ('60000001-0000-0000-0000-000000000005','5b000001-0000-0000-0000-000000000003','DEVUELTO',       '2026-04-01 18:00:00'),
    ('60000001-0000-0000-0000-000000000006','5b000001-0000-0000-0000-000000000005','EN_TRANSITO',    '2026-04-10 09:30:00'),
    ('60000001-0000-0000-0000-000000000007','5b000001-0000-0000-0000-000000000005','DANADO',         '2026-04-10 16:00:00');

-- ============================================================
-- LOG DE SINCRONIZACIÓN
-- Prueba:
--   GET /api/sincronizacion/logs
--   GET /api/sincronizacion/logs/paquetes/5b000001-0000-0000-0000-000000000001
-- ============================================================
INSERT INTO log_sincronizacion (id, id_paquete, codigo_respuesta_http, json_recibido, created_at) VALUES
    ('70000001-0000-0000-0000-000000000001','5b000001-0000-0000-0000-000000000001', 200,'{"estado":"ENTREGADO","porcentaje_pago":100}','2026-04-01 11:05:00'),
    ('70000001-0000-0000-0000-000000000002','5b000001-0000-0000-0000-000000000003', 200,'{"estado":"DEVUELTO","porcentaje_pago":50}',  '2026-04-01 18:05:00'),
    ('70000001-0000-0000-0000-000000000003','5b000001-0000-0000-0000-000000000005', 200,'{"estado":"DANADO","porcentaje_pago":0}',     '2026-04-10 16:05:00'),
    ('70000001-0000-0000-0000-000000000004','5b000001-0000-0000-0000-000000000002', 404, NULL,                                         '2026-04-01 12:00:00'),
    ('70000001-0000-0000-0000-000000000005','5b000001-0000-0000-0000-000000000004', 200,'{"estado":"ENTREGADO","porcentaje_pago":100}','2026-04-10 18:05:00');

-- ============================================================
-- LIQUIDACIONES REFERENCIA (registro de liquidaciones con pagos)
-- ============================================================
INSERT INTO liquidaciones_referencia (id_liquidacion) VALUES
    ('f6000001-0000-0000-0000-000000000001'),
    ('f6000001-0000-0000-0000-000000000002'),
    ('f6000001-0000-0000-0000-000000000003'),
    ('f6000001-0000-0000-0000-000000000004');

-- ============================================================
-- PAGOS (write model + visualizarEstadoPago read model)
-- Prueba:
--   GET /api/v1/pagos/{idPago}/estado
--   GET /api/v1/liquidaciones/{idLiquidacion}/pago/estado
--   GET /api/pagos              → filtra por id_usuario del JWT
--   GET /api/pagos/{id}         → detalle
--
-- id_usuario = claim "sub" del JWT del transportista:
--   transportista@test.com → sub: "a1b2c3d4-e5f6-7890-1234-567890abcdef"
--   admin@test.com         → sub: "00000000-0000-0000-0000-000000000001"
--
-- PG1 id: 6c000001-...-001  → LIQ1, PENDIENTE,  transportista
-- PG2 id: 6c000001-...-002  → LIQ2, PAGADO,     otro usuario
-- PG3 id: 6c000001-...-003  → LIQ3, RECHAZADO,  transportista
-- PG4 id: 6c000001-...-004  → LIQ4, EN_PROCESO, admin
-- ============================================================
INSERT INTO pagos (id_pago, id_usuario, monto_base, fecha, id_penalidad, monto_neto,
                   id_liquidacion, estado_actual, fecha_ultima_actualizacion,
                   ultima_secuencia_procesada, version) VALUES
    ('6c000001-0000-0000-0000-000000000001','a1b2c3d4-e5f6-7890-1234-567890abcdef',
     96000.00,'2026-04-02 10:00:00', NULL,                                   96000.00,
     'f6000001-0000-0000-0000-000000000001','PENDIENTE',  '2026-04-02 10:00:00', 1, 0),

    ('6c000001-0000-0000-0000-000000000002','a1000001-0000-0000-0000-000000000002',
     80000.00,'2026-04-11 12:00:00', NULL,                                   82000.00,
     'f6000001-0000-0000-0000-000000000002','PAGADO',     '2026-04-15 09:00:00', 3, 2),

    ('6c000001-0000-0000-0000-000000000003','a1b2c3d4-e5f6-7890-1234-567890abcdef',
     78000.00,'2026-04-21 08:00:00','90000001-0000-0000-0000-000000000001', 76500.00,
     'f6000001-0000-0000-0000-000000000003','RECHAZADO',  '2026-04-25 14:00:00', 2, 1),

    ('6c000001-0000-0000-0000-000000000004','00000000-0000-0000-0000-000000000001',
     60000.00,'2026-05-02 08:00:00', NULL,                                   60000.00,
     'f6000001-0000-0000-0000-000000000004','EN_PROCESO', '2026-05-03 10:00:00', 2, 1);

-- ============================================================
-- ESTADOS DE PAGO (historial de transiciones)
-- Prueba: GET /api/v1/pagos/{idPago}/eventos
-- PG2 tiene: PENDIENTE → EN_PROCESO → PAGADO  (flujo completo)
-- PG3 tiene: PENDIENTE → RECHAZADO
-- ============================================================
INSERT INTO estados_pago
    (id_estado_pago, id_pago, estado, fecha_registro, fecha_evento_banco,
     secuencia_evento, id_evento_transaccion) VALUES
    -- PG1: solo PENDIENTE
    ('7d000001-0000-0000-0000-000000000001','6c000001-0000-0000-0000-000000000001',
     'PENDIENTE',  '2026-04-02 10:00:00', NULL,                    1,'8e000001-0000-0000-0000-000000000001'),
    -- PG2: flujo completo
    ('7d000001-0000-0000-0000-000000000002','6c000001-0000-0000-0000-000000000002',
     'PENDIENTE',  '2026-04-11 12:00:00','2026-04-11 11:00:00', 1,'8e000001-0000-0000-0000-000000000002'),
    ('7d000001-0000-0000-0000-000000000003','6c000001-0000-0000-0000-000000000002',
     'EN_PROCESO', '2026-04-12 09:00:00','2026-04-12 08:30:00', 2,'8e000001-0000-0000-0000-000000000003'),
    ('7d000001-0000-0000-0000-000000000004','6c000001-0000-0000-0000-000000000002',
     'PAGADO',     '2026-04-15 09:00:00','2026-04-15 08:00:00', 3,'8e000001-0000-0000-0000-000000000004'),
    -- PG3: PENDIENTE → RECHAZADO
    ('7d000001-0000-0000-0000-000000000005','6c000001-0000-0000-0000-000000000003',
     'PENDIENTE',  '2026-04-21 08:00:00', NULL,                    1,'8e000001-0000-0000-0000-000000000005'),
    ('7d000001-0000-0000-0000-000000000006','6c000001-0000-0000-0000-000000000003',
     'RECHAZADO',  '2026-04-25 14:00:00','2026-04-25 13:00:00', 2,'8e000001-0000-0000-0000-000000000006'),
    -- PG4: PENDIENTE → EN_PROCESO (en curso)
    ('7d000001-0000-0000-0000-000000000007','6c000001-0000-0000-0000-000000000004',
     'PENDIENTE',  '2026-05-02 08:00:00', NULL,                    1,'8e000001-0000-0000-0000-000000000007'),
    ('7d000001-0000-0000-0000-000000000008','6c000001-0000-0000-0000-000000000004',
     'EN_PROCESO', '2026-05-03 10:00:00','2026-05-03 09:30:00', 2,'8e000001-0000-0000-0000-000000000008');

-- ============================================================
-- EVENTOS TRANSACCIÓN
-- Prueba: GET /api/v1/pagos/{idPago}/eventos
-- Incluye un evento DUPLICADO (caso borde) y uno con ERROR
-- ============================================================
INSERT INTO eventos_transaccion
    (id_evento, id_transaccion_banco, id_pago, id_liquidacion, payload_recibido,
     fecha_recepcion, fecha_evento_banco, secuencia, estado_solicitado,
     estado_procesamiento, mensaje_error, procesado) VALUES
    ('8e000001-0000-0000-0000-000000000001','TXN-2026-BANK-001',
     '6c000001-0000-0000-0000-000000000001','f6000001-0000-0000-0000-000000000001',
     '{"banco":"Bancolombia","monto":96000.00,"tipo":"PAGO_INICIAL"}',
     '2026-04-02 10:00:00', NULL,                    1,'PENDIENTE',  'PROCESADO', NULL, TRUE),

    ('8e000001-0000-0000-0000-000000000002','TXN-2026-BANK-002',
     '6c000001-0000-0000-0000-000000000002','f6000001-0000-0000-0000-000000000002',
     '{"banco":"Davivienda","monto":82000.00,"tipo":"PAGO_INICIAL"}',
     '2026-04-11 12:00:00','2026-04-11 11:00:00', 1,'PENDIENTE',  'PROCESADO', NULL, TRUE),

    ('8e000001-0000-0000-0000-000000000003','TXN-2026-BANK-003',
     '6c000001-0000-0000-0000-000000000002','f6000001-0000-0000-0000-000000000002',
     '{"banco":"Davivienda","monto":82000.00,"tipo":"CONFIRMACION"}',
     '2026-04-12 09:00:00','2026-04-12 08:30:00', 2,'EN_PROCESO', 'PROCESADO', NULL, TRUE),

    ('8e000001-0000-0000-0000-000000000004','TXN-2026-BANK-004',
     '6c000001-0000-0000-0000-000000000002','f6000001-0000-0000-0000-000000000002',
     '{"banco":"Davivienda","monto":82000.00,"tipo":"PAGO_FINAL"}',
     '2026-04-15 09:00:00','2026-04-15 08:00:00', 3,'PAGADO',     'PROCESADO', NULL, TRUE),

    ('8e000001-0000-0000-0000-000000000005','TXN-2026-BANK-005',
     '6c000001-0000-0000-0000-000000000003','f6000001-0000-0000-0000-000000000003',
     '{"banco":"BBVA","monto":76500.00,"tipo":"PAGO_INICIAL"}',
     '2026-04-21 08:00:00', NULL,                    1,'PENDIENTE',  'PROCESADO', NULL, TRUE),

    ('8e000001-0000-0000-0000-000000000006','TXN-2026-BANK-006',
     '6c000001-0000-0000-0000-000000000003','f6000001-0000-0000-0000-000000000003',
     '{"banco":"BBVA","monto":76500.00,"tipo":"RECHAZO","motivo":"fondos_insuficientes"}',
     '2026-04-25 14:00:00','2026-04-25 13:00:00', 2,'RECHAZADO',  'PROCESADO', NULL, TRUE),

    ('8e000001-0000-0000-0000-000000000007','TXN-2026-BANK-007',
     '6c000001-0000-0000-0000-000000000004','f6000001-0000-0000-0000-000000000004',
     '{"banco":"Nequi","monto":60000.00,"tipo":"PAGO_INICIAL"}',
     '2026-05-02 08:00:00', NULL,                    1,'PENDIENTE',  'PROCESADO', NULL, TRUE),

    ('8e000001-0000-0000-0000-000000000008','TXN-2026-BANK-008',
     '6c000001-0000-0000-0000-000000000004','f6000001-0000-0000-0000-000000000004',
     '{"banco":"Nequi","monto":60000.00,"tipo":"EN_PROCESO"}',
     '2026-05-03 10:00:00','2026-05-03 09:30:00', 2,'EN_PROCESO', 'RECIBIDO',  NULL, FALSE),

    -- Caso borde: evento duplicado rechazado
    ('8e000001-0000-0000-0000-000000000009','TXN-2026-BANK-DUP',
     '6c000001-0000-0000-0000-000000000001','f6000001-0000-0000-0000-000000000001',
     NULL,
     '2026-04-03 10:00:00', NULL,                    1,'PAGADO',     'DUPLICADO', 'Evento duplicado — ya procesado TXN-2026-BANK-001', FALSE);

/*
  PAYLOAD — POST /api/v1/pagos/webhook/estado   (endpoint público, sin token)
  {
    "id_evento":           "EVT-2026-TEST-NEW",
    "id_transaccion_banco":"TXN-2026-TEST-NEW",
    "id_pago":             "6c000001-0000-0000-0000-000000000001",
    "id_liquidacion":      "f6000001-0000-0000-0000-000000000001",
    "estado":              "PAGADO",
    "fecha_evento":        "2026-05-11T10:00:00",
    "secuencia":           2,
    "payload_original": { "banco": "Bancolombia", "monto": 96000.00 }
  }
*/

-- ============================================================
-- EVENTOS (visualizarEstadoPago — read model)
-- ============================================================
INSERT INTO eventos (id, tipo, fecha, datos) VALUES
    ('9f000001-0000-0000-0000-000000000001','PAGO_REGISTRADO','2026-04-02 10:05:00',
     '{"idPago":"6c000001-0000-0000-0000-000000000001","monto":96000}'),
    ('9f000001-0000-0000-0000-000000000002','PAGO_COMPLETADO', '2026-04-15 09:05:00',
     '{"idPago":"6c000001-0000-0000-0000-000000000002","monto":82000}'),
    ('9f000001-0000-0000-0000-000000000003','PAGO_RECHAZADO',  '2026-04-25 14:05:00',
     '{"idPago":"6c000001-0000-0000-0000-000000000003","monto":76500}'),
    ('9f000001-0000-0000-0000-000000000004','PAGO_EN_PROCESO', '2026-05-03 10:05:00',
     '{"idPago":"6c000001-0000-0000-0000-000000000004","monto":60000}');

-- ================================================================
-- CONSULTAS DE VERIFICACIÓN — Ejecutar después de insertar
-- ================================================================

SELECT tabla, registros FROM (
    SELECT 'transportista'           AS tabla, COUNT(*) AS registros FROM transportista         UNION ALL
    SELECT 'seguros',                           COUNT(*)             FROM seguros                UNION ALL
    SELECT 'vehiculos',                         COUNT(*)             FROM vehiculos              UNION ALL
    SELECT 'contratos',                         COUNT(*)             FROM contratos              UNION ALL
    SELECT 'rutas (read model)',                COUNT(*)             FROM rutas                  UNION ALL
    SELECT 'ruta (write model)',                COUNT(*)             FROM ruta                   UNION ALL
    SELECT 'parada',                            COUNT(*)             FROM parada                 UNION ALL
    SELECT 'liquidaciones',                     COUNT(*)             FROM liquidaciones          UNION ALL
    SELECT 'ajustes',                           COUNT(*)             FROM ajustes                UNION ALL
    SELECT 'auditoria_liquidacion',             COUNT(*)             FROM auditoria_liquidacion  UNION ALL
    SELECT 'paquetes',                          COUNT(*)             FROM paquetes               UNION ALL
    SELECT 'historial_estados',                 COUNT(*)             FROM historial_estados      UNION ALL
    SELECT 'log_sincronizacion',                COUNT(*)             FROM log_sincronizacion     UNION ALL
    SELECT 'liquidaciones_referencia',          COUNT(*)             FROM liquidaciones_referencia UNION ALL
    SELECT 'pagos',                             COUNT(*)             FROM pagos                  UNION ALL
    SELECT 'estados_pago',                      COUNT(*)             FROM estados_pago           UNION ALL
    SELECT 'eventos_transaccion',               COUNT(*)             FROM eventos_transaccion    UNION ALL
    SELECT 'eventos',                           COUNT(*)             FROM eventos
) t ORDER BY tabla;

-- Liquidaciones con contratos y rutas (simula GET /api/liquidaciones)
SELECT
    l.id                AS id_liquidacion,
    l.estado,
    l.valor_final,
    r.tipo_vehiculo,
    r.numero_paradas,
    c.id_contrato
FROM liquidaciones l
JOIN contratos c ON l.id_contrato = c.id
LEFT JOIN rutas r ON l.id_ruta = r.id
ORDER BY l.fecha_calculo;

-- Pagos con su liquidacion (simula GET /api/v1/pagos/{idPago}/estado)
SELECT
    p.id_pago,
    p.estado_actual,
    p.monto_neto,
    p.id_liquidacion,
    p.fecha_ultima_actualizacion
FROM pagos p ORDER BY p.fecha;

-- Historial de un pago (simula GET /api/v1/pagos/{idPago}/eventos)
SELECT ep.id_estado_pago, ep.id_pago, ep.estado, ep.fecha_registro, ep.secuencia_evento
FROM estados_pago ep
WHERE ep.id_pago = '6c000001-0000-0000-0000-000000000002'
ORDER BY ep.secuencia_evento;