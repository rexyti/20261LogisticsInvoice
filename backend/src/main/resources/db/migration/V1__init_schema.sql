-- V1__init_schema.sql

-- Tabla para almacenar los contratos
CREATE TABLE contratos (
    id UUID PRIMARY KEY,
    tipo_contratacion VARCHAR(255) NOT NULL,
    tarifa NUMERIC(19, 4) NOT NULL
CREATE TABLE transportista (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conductor_id UUID NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL
);

CREATE TABLE ruta (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ruta_id UUID NOT NULL UNIQUE,
    transportista_id UUID NOT NULL REFERENCES transportista(id),
    vehiculo_id UUID,
    tipo_vehiculo VARCHAR(50),
    modelo_contrato VARCHAR(255),
    fecha_inicio_transito TIMESTAMP NOT NULL,
    fecha_cierre TIMESTAMP NOT NULL,
    estado_procesamiento VARCHAR(50) NOT NULL
);

-- Tabla para almacenar las liquidaciones calculadas
-- Sincronizada exactamente con LiquidacionEntity.java
CREATE TABLE liquidaciones (
    id UUID PRIMARY KEY,
    id_ruta UUID NOT NULL,
    id_contrato UUID NOT NULL,
CREATE TABLE parada (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paquete_id UUID,
    parada_id UUID NOT NULL,
    ruta_id UUID NOT NULL REFERENCES ruta(id),
    estado VARCHAR(50) NOT NULL,
    valor_base NUMERIC(19, 4) NOT NULL,
    valor_final NUMERIC(19, 4) NOT NULL,
    fecha_calculo TIMESTAMP WITH TIME ZONE NOT NULL,
    solicitud_revision_aceptada BOOLEAN NOT NULL DEFAULT FALSE,
    id_admin_revisor UUID,
    fecha_aceptacion_revision TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Restricciones
    CONSTRAINT uq_liquidacion_id_ruta UNIQUE (id_ruta),
    -- La relación OneToOne se refuerza con un UNIQUE en la columna de la FK
    CONSTRAINT uq_liquidacion_id_contrato UNIQUE (id_contrato),
    CONSTRAINT fk_liquidacion_contrato FOREIGN KEY (id_contrato) REFERENCES contratos(id)
);

-- Tabla para almacenar los ajustes (bonos, descuentos, penalizaciones) de una liquidación
CREATE TABLE ajustes (
    id UUID PRIMARY KEY,
    id_liquidacion UUID NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    monto NUMERIC(19, 4) NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_ajuste_liquidacion FOREIGN KEY (id_liquidacion) REFERENCES liquidaciones(id)
    motivo_falla VARCHAR(100),
    UNIQUE (parada_id, ruta_id)
);

-- Tabla para el historial de auditoría de cada liquidación
CREATE TABLE auditoria_liquidacion (
    id UUID PRIMARY KEY,
    id_liquidacion UUID NOT NULL,
    operacion VARCHAR(50) NOT NULL,
    valor_anterior NUMERIC(19, 4),
    valor_nuevo NUMERIC(19, 4) NOT NULL,
    fecha_operacion TIMESTAMP WITH TIME ZONE NOT NULL,
    tipo_responsable VARCHAR(50) NOT NULL,
    id_responsable VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_auditoria_liquidacion FOREIGN KEY (id_liquidacion) REFERENCES liquidaciones(id)
);

-- Índices para optimizar consultas comunes
CREATE INDEX idx_liquidaciones_id_ruta ON liquidaciones(id_ruta);
CREATE INDEX idx_liquidaciones_fecha_calculo ON liquidaciones(fecha_calculo);
CREATE INDEX idx_ajustes_id_liquidacion ON ajustes(id_liquidacion);
CREATE INDEX idx_auditoria_id_liquidacion ON auditoria_liquidacion(id_liquidacion);
