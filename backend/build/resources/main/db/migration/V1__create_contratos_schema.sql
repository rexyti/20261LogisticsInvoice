CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario BIGSERIAL PRIMARY KEY,
    nombre     VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS vehiculos (
    id_vehiculo BIGSERIAL PRIMARY KEY,
    id_usuario  BIGINT       NOT NULL REFERENCES usuarios (id_usuario),
    tipo        VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS seguros (
    id_seguro  BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT       NOT NULL REFERENCES usuarios (id_usuario),
    estado     VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS contratos (
    id               BIGSERIAL    PRIMARY KEY,
    id_contrato      VARCHAR(100) NOT NULL UNIQUE,
    tipo_contrato    VARCHAR(50)  NOT NULL,
    nombre_conductor VARCHAR(255) NOT NULL,
    precio_paradas   NUMERIC(15, 2),
    precio           NUMERIC(15, 2),
    tipo_vehiculo    VARCHAR(100) NOT NULL,
    fecha_inicio     DATE         NOT NULL,
    fecha_final      DATE         NOT NULL,
    id_usuario       BIGINT       NOT NULL REFERENCES usuarios (id_usuario),
    id_vehiculo      BIGINT       NOT NULL REFERENCES vehiculos (id_vehiculo),
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);
