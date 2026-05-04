-- V6__update_schema_to_match_entities.sql
-- Sincroniza el esquema de BD con las entidades JPA implementadas.

-- ============================================================
-- 1. ALTER TABLE liquidaciones — columnas requeridas por LiquidacionEntity
--    y VisualizarLiquidacionEntity
-- ============================================================
ALTER TABLE liquidaciones
    ADD COLUMN IF NOT EXISTS valor_base                  NUMERIC(19, 4) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS valor_final                 NUMERIC(19, 4) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS solicitud_revision_aceptada BOOLEAN        NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS id_admin_revisor            UUID,
    ADD COLUMN IF NOT EXISTS fecha_aceptacion_revision   TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS version                     BIGINT                  DEFAULT 0,
    ADD COLUMN IF NOT EXISTS created_at                  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at                  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS monto_bruto                 NUMERIC(12, 2),
    ADD COLUMN IF NOT EXISTS monto_neto                  NUMERIC(12, 2);

-- ============================================================
-- 2. ALTER TABLE ajustes — columna requerida por VisualizarLiquidacionAjusteEntity
-- ============================================================
ALTER TABLE ajustes
    ADD COLUMN IF NOT EXISTS razon VARCHAR(500);

-- ============================================================
-- 3. DROP tablas obsoletas de V3 (ninguna entidad JPA las referencia)
--    Orden respeta dependencias de FK: contratos_logistica → vehiculos → seguros → usuarios
-- ============================================================
DROP TABLE IF EXISTS contratos_logistica;
DROP TABLE IF EXISTS vehiculos;
DROP TABLE IF EXISTS seguros;
DROP TABLE IF EXISTS usuarios;

-- ============================================================
-- 4. CREATE TABLE transportistas (ContratosTransportistaEntity)
--    Tabla separada de `transportista` (cierreRuta); contexto distinto.
-- ============================================================
CREATE TABLE transportistas (
    id_transportista UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre           VARCHAR(255) NOT NULL
);

-- ============================================================
-- 5. CREATE TABLE vehiculos — UUID-based (VehiculoEntity, módulo contratos)
-- ============================================================
CREATE TABLE vehiculos (
    id_vehiculo      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_transportista UUID         NOT NULL REFERENCES transportistas(id_transportista),
    tipo             VARCHAR(255) NOT NULL
);

-- ============================================================
-- 6. CREATE TABLE seguros — UUID-based (SeguroEntity, módulo contratos)
-- ============================================================
CREATE TABLE seguros (
    id_seguro     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_poliza VARCHAR(255) NOT NULL,
    estado        VARCHAR(255) NOT NULL
);

-- ============================================================
-- 7. ALTER TABLE contratos — columnas requeridas por ContratosContratoEntity
--    (LiquidacionContratoEntity sólo usa id/tipo_contratacion/tarifa, ya presentes)
-- ============================================================
ALTER TABLE contratos
    ADD COLUMN IF NOT EXISTS id_contrato     VARCHAR(100),
    ADD COLUMN IF NOT EXISTS tipo_contrato   VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS es_por_parada   BOOLEAN      NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS precio_paradas  NUMERIC(15, 2),
    ADD COLUMN IF NOT EXISTS precio          NUMERIC(15, 2),
    ADD COLUMN IF NOT EXISTS tipo_vehiculo   VARCHAR(100) NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS fecha_inicio    TIMESTAMP    NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS fecha_final     TIMESTAMP    NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS id_transportista UUID,
    ADD COLUMN IF NOT EXISTS id_seguro        UUID,
    ADD COLUMN IF NOT EXISTS created_at      TIMESTAMP    NOT NULL DEFAULT NOW();

ALTER TABLE contratos
    ADD CONSTRAINT uq_contratos_id_contrato
        UNIQUE (id_contrato);

ALTER TABLE contratos
    ADD CONSTRAINT fk_contratos_transportista
        FOREIGN KEY (id_transportista) REFERENCES transportistas(id_transportista);

ALTER TABLE contratos
    ADD CONSTRAINT fk_contratos_seguro
        FOREIGN KEY (id_seguro) REFERENCES seguros(id_seguro);

-- ============================================================
-- 8. CREATE TABLE rutas (VisualizarLiquidacionRutaEntity)
--    Modelo de lectura independiente de `ruta` (cierreRuta).
-- ============================================================
CREATE TABLE rutas (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fecha_inicio   TIMESTAMP    NOT NULL,
    fecha_cierre   TIMESTAMP,
    tipo_vehiculo  VARCHAR(50),
    precio_parada  NUMERIC(12, 2),
    numero_paradas INTEGER
);

-- ============================================================
-- 9. CREATE TABLE liquidaciones_referencia (LiquidacionReferenciaEntity)
-- ============================================================
CREATE TABLE liquidaciones_referencia (
    id_liquidacion UUID PRIMARY KEY
);

-- ============================================================
-- 10. CREATE TABLE pagos (RegistrarEstadoPagoPagoEntity)
-- ============================================================
CREATE TABLE pagos (
    id_pago                    UUID           PRIMARY KEY,
    id_usuario                 UUID,
    monto_base                 NUMERIC(18, 2),
    fecha                      TIMESTAMP,
    id_penalidad               UUID,
    monto_neto                 NUMERIC(18, 2),
    id_liquidacion             UUID           NOT NULL,
    estado_actual              VARCHAR(50)    NOT NULL,
    fecha_ultima_actualizacion TIMESTAMP      NOT NULL,
    ultima_secuencia_procesada BIGINT         NOT NULL,
    version                    BIGINT
);

-- ============================================================
-- 11. CREATE TABLE estados_pago (RegistrarEstadoPagoEstadoPagoEntity)
-- ============================================================
CREATE TABLE estados_pago (
    id_estado_pago        UUID        PRIMARY KEY,
    id_pago               UUID        NOT NULL,
    estado                VARCHAR(50) NOT NULL,
    fecha_registro        TIMESTAMP   NOT NULL,
    fecha_evento_banco    TIMESTAMP,
    secuencia_evento      BIGINT,
    id_evento_transaccion UUID
);

CREATE INDEX idx_estado_pago_id_pago ON estados_pago (id_pago);
CREATE INDEX idx_estado_pago_fecha   ON estados_pago (fecha_registro);

-- ============================================================
-- 12. CREATE TABLE eventos_transaccion (EventoTransaccionEntity)
-- ============================================================
CREATE TABLE eventos_transaccion (
    id_evento            UUID         PRIMARY KEY,
    id_transaccion_banco VARCHAR(255) NOT NULL,
    id_pago              UUID         NOT NULL,
    id_liquidacion       UUID         NOT NULL,
    payload_recibido     TEXT,
    fecha_recepcion      TIMESTAMP    NOT NULL,
    fecha_evento_banco   TIMESTAMP,
    secuencia            BIGINT,
    estado_solicitado    VARCHAR(50),
    estado_procesamiento VARCHAR(50)  NOT NULL,
    mensaje_error        TEXT,
    procesado            BOOLEAN      NOT NULL,

    CONSTRAINT uk_id_transaccion_banco UNIQUE (id_transaccion_banco)
);

CREATE INDEX idx_evento_id_pago        ON eventos_transaccion (id_pago);
CREATE INDEX idx_evento_fecha_recepcion ON eventos_transaccion (fecha_recepcion);

-- ============================================================
-- 13. CREATE TABLE eventos (EventoEntity, módulo VisualizarEstadoPago)
-- ============================================================
CREATE TABLE eventos (
    id    UUID PRIMARY KEY,
    tipo  VARCHAR(255),
    fecha TIMESTAMP,
    datos VARCHAR(255)
);
