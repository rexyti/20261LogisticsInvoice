-- V1__init_clean_schema.sql
-- Esquema limpio único. Una entidad de dominio = una tabla.
-- CQRS: write model dicta columnas; módulos de visualización leen las mismas tablas.

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- TRANSPORTISTA (entidad unificada: antes duplicada en cierreRuta y contratos)
-- ============================================================

CREATE TABLE transportista (
    id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(255) NOT NULL
);

-- ============================================================
-- CIERRE RUTA
-- ============================================================

CREATE TABLE ruta (
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

CREATE TABLE parada (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paquete_id     UUID,
    parada_id      UUID NOT NULL,
    ruta_entity_id UUID NOT NULL REFERENCES ruta(id),
    estado         VARCHAR(50) NOT NULL,
    motivo_falla   VARCHAR(100),
    CONSTRAINT uk_parada_parada_id_ruta_entity_id UNIQUE (parada_id, ruta_entity_id)
);

-- ============================================================
-- CONTRATOS
-- ============================================================

CREATE TABLE seguros (
    id_seguro     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_poliza VARCHAR(255) NOT NULL,
    estado        VARCHAR(255) NOT NULL
);

CREATE TABLE vehiculos (
    id_vehiculo      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_transportista UUID NOT NULL REFERENCES transportista(id),
    tipo             VARCHAR(255) NOT NULL
);

-- ContratoEntity posee todas las columnas de contratos.
-- tipo_contratacion y tarifa son escritas por el módulo liquidacion.
CREATE TABLE contratos (
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

-- ============================================================
-- VISUALIZAR LIQUIDACION — tabla de rutas para read model
-- ============================================================

CREATE TABLE rutas (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fecha_inicio   TIMESTAMP NOT NULL,
    fecha_cierre   TIMESTAMP,
    tipo_vehiculo  VARCHAR(50),
    precio_parada  NUMERIC(12, 2),
    numero_paradas INTEGER
);

-- ============================================================
-- LIQUIDACION (write + read model comparten tabla)
-- ============================================================

CREATE TABLE liquidaciones (
    id                          UUID PRIMARY KEY,
    -- id_ruta sin FK de BD: el write model referencia ruta(id), el read model referencia rutas(id)
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
    -- read model: VisualizarLiquidacionEntity (nullable: el write model no las rellena)
    estado_liquidacion          VARCHAR(50),
    monto_bruto                 NUMERIC(12, 2),
    monto_neto                  NUMERIC(12, 2),
    usuario_id                  VARCHAR(255)
);

-- Tabla compartida: AjusteEntity (write) y VisualizarLiquidacionAjusteEntity (read).
-- Columna "motivo" unificada (antes "razon" en el read model — se actualiza la entidad).
CREATE TABLE ajustes (
    id             UUID PRIMARY KEY,
    id_liquidacion UUID NOT NULL REFERENCES liquidaciones(id),
    tipo           VARCHAR(100) NOT NULL,
    monto          NUMERIC(19, 4) NOT NULL,
    motivo         VARCHAR(500) NOT NULL DEFAULT '',
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE auditoria_liquidacion (
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

-- ============================================================
-- NOVEDAD ESTADO PAQUETE
-- ============================================================

CREATE TABLE paquetes (
    id_paquete    BIGINT PRIMARY KEY,
    id_ruta       BIGINT NOT NULL,
    estado_actual VARCHAR(50),
    updated_at    TIMESTAMP
);

CREATE TABLE historial_estados (
    id         BIGSERIAL PRIMARY KEY,
    id_paquete BIGINT NOT NULL,
    estado     VARCHAR(50) NOT NULL,
    fecha      TIMESTAMP NOT NULL
);

CREATE TABLE log_sincronizacion (
    id                    BIGSERIAL PRIMARY KEY,
    id_paquete            BIGINT NOT NULL,
    codigo_respuesta_http INTEGER,
    json_recibido         TEXT,
    created_at            TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- REGISTRAR ESTADO PAGO
-- ============================================================

CREATE TABLE liquidaciones_referencia (
    id_liquidacion UUID PRIMARY KEY
);

-- Tabla compartida: RegistrarEstadoPagoPagoEntity (write) y VisualizarEstadoPagoPagoEntity (read).
-- Nombres de columnas del write model; la entidad read model se actualiza para alinearse.
CREATE TABLE pagos (
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

-- Tabla compartida: RegistrarEstadoPagoEstadoPagoEntity (write) y VisualizarEstadoPagoEstadoPagoEntity (read).
-- Write usa id_pago; read model actualizado para usar @Column(name="id_pago").
CREATE TABLE estados_pago (
    id_estado_pago        UUID PRIMARY KEY,
    id_pago               UUID NOT NULL,
    estado                VARCHAR(50) NOT NULL,
    fecha_registro        TIMESTAMP NOT NULL,
    fecha_evento_banco    TIMESTAMP,
    secuencia_evento      BIGINT,
    id_evento_transaccion UUID
);

CREATE TABLE eventos_transaccion (
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

-- ============================================================
-- VISUALIZAR ESTADO PAGO — read model
-- ============================================================

CREATE TABLE eventos (
    id    UUID PRIMARY KEY,
    tipo  VARCHAR(255),
    fecha TIMESTAMP,
    datos VARCHAR(255)
);

-- ============================================================
-- ÍNDICES
-- ============================================================

CREATE INDEX idx_liquidaciones_id_ruta       ON liquidaciones(id_ruta);
CREATE INDEX idx_liquidaciones_fecha_calculo  ON liquidaciones(fecha_calculo DESC);
CREATE INDEX idx_ajustes_id_liquidacion       ON ajustes(id_liquidacion);
CREATE INDEX idx_auditoria_id_liquidacion     ON auditoria_liquidacion(id_liquidacion);
CREATE INDEX idx_historial_id_paquete         ON historial_estados(id_paquete);
CREATE INDEX idx_historial_fecha              ON historial_estados(fecha);
CREATE INDEX idx_log_id_paquete               ON log_sincronizacion(id_paquete);
CREATE INDEX idx_estado_pago_id_pago          ON estados_pago(id_pago);
CREATE INDEX idx_estado_pago_fecha            ON estados_pago(fecha_registro);
CREATE INDEX idx_evento_id_pago               ON eventos_transaccion(id_pago);
CREATE INDEX idx_evento_fecha_recepcion       ON eventos_transaccion(fecha_recepcion);
