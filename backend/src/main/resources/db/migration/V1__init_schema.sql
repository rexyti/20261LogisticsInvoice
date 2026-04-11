-- V1__init_schema.sql

-- Tabla para almacenar las liquidaciones calculadas
CREATE TABLE liquidaciones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_ruta UUID NOT NULL,
    id_contrato UUID NOT NULL,
    estado VARCHAR(50) NOT NULL, -- Ej: 'CALCULADA', 'EN_REVISION', 'PAGADA', 'ERROR'
    valor_final NUMERIC(19, 4) NOT NULL,
    fecha_calculo TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Restricción para evitar liquidaciones duplicadas por ruta
    CONSTRAINT uq_liquidacion_id_ruta UNIQUE (id_ruta)
);

-- Tabla para almacenar los ajustes (bonos, descuentos, penalizaciones) de una liquidación
CREATE TABLE ajustes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_liquidacion UUID NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- Ej: 'BONO', 'PENALIZACION', 'DESCUENTO'
    monto NUMERIC(19, 4) NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Llave foránea hacia la tabla de liquidaciones
    CONSTRAINT fk_ajuste_liquidacion FOREIGN KEY (id_liquidacion) REFERENCES liquidaciones(id)
);

-- Tabla para el historial de auditoría de cada liquidación
CREATE TABLE auditoria_liquidacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_liquidacion UUID NOT NULL,
    operacion VARCHAR(50) NOT NULL, -- Ej: 'CALCULO_INICIAL', 'RECALCULO_ADMIN'
    valor_anterior NUMERIC(19, 4),
    valor_nuevo NUMERIC(19, 4) NOT NULL,
    fecha_operacion TIMESTAMP WITH TIME ZONE NOT NULL,
    responsable VARCHAR(100) NOT NULL, -- Ej: 'SISTEMA', 'admin@logistica.com'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Llave foránea hacia la tabla de liquidaciones
    CONSTRAINT fk_auditoria_liquidacion FOREIGN KEY (id_liquidacion) REFERENCES liquidaciones(id)
);

-- Índices para optimizar consultas comunes
CREATE INDEX idx_liquidaciones_id_ruta ON liquidaciones(id_ruta);
CREATE INDEX idx_liquidaciones_fecha_calculo ON liquidaciones(fecha_calculo);
CREATE INDEX idx_ajustes_id_liquidacion ON ajustes(id_liquidacion);
CREATE INDEX idx_auditoria_id_liquidacion ON auditoria_liquidacion(id_liquidacion);

-- Triggers para actualizar el campo updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = NOW();
   RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_liquidaciones_updated_at
BEFORE UPDATE ON liquidaciones
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_ajustes_updated_at
BEFORE UPDATE ON ajustes
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
