-- Indices para consultas y listados de liquidaciones (T009)

CREATE INDEX IF NOT EXISTS idx_liquidaciones_id_ruta
    ON liquidaciones (id_ruta);

CREATE INDEX IF NOT EXISTS idx_liquidaciones_fecha_calculo
    ON liquidaciones (fecha_calculo DESC);

CREATE INDEX IF NOT EXISTS idx_liquidaciones_estado
    ON liquidaciones (estado_liquidacion);

CREATE INDEX IF NOT EXISTS idx_liquidaciones_usuario_id
    ON liquidaciones (usuario_id);

CREATE INDEX IF NOT EXISTS idx_ajustes_id_liquidacion
    ON ajustes (id_liquidacion);
