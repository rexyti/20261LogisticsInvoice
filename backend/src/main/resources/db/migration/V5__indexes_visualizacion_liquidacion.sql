-- V5__indexes_visualizacion_liquidacion.sql

CREATE INDEX IF NOT EXISTS idx_liquidaciones_fecha_calculo
    ON liquidaciones (fecha_calculo DESC);

CREATE INDEX IF NOT EXISTS idx_ajustes_id_liquidacion
    ON ajustes (id_liquidacion);