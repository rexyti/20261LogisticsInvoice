-- Esquema inicial para el modulo de liquidaciones

CREATE TABLE IF NOT EXISTS rutas (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fecha_inicio    TIMESTAMP        NOT NULL,
    fecha_cierre    TIMESTAMP,
    tipo_vehiculo   VARCHAR(50),
    precio_parada   NUMERIC(12, 2),
    numero_paradas  INTEGER
);

CREATE TABLE IF NOT EXISTS liquidaciones (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_ruta             UUID         NOT NULL REFERENCES rutas (id),
    id_contrato         UUID,
    estado_liquidacion  VARCHAR(50)  NOT NULL,
    monto_bruto         NUMERIC(12, 2),
    monto_neto          NUMERIC(12, 2),
    fecha_calculo       TIMESTAMP    NOT NULL,
    usuario_id          VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS ajustes (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_liquidacion  UUID          NOT NULL REFERENCES liquidaciones (id) ON DELETE CASCADE,
    tipo            VARCHAR(100)  NOT NULL,
    monto           NUMERIC(12, 2) NOT NULL,
    razon           VARCHAR(500)
);
