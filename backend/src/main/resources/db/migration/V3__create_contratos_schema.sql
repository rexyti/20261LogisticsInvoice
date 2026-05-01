-- V3__create_contratos_schema.sql

CREATE TABLE usuarios (
                          id_usuario BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(255) NOT NULL
);

CREATE TABLE vehiculos (
                           id_vehiculo BIGSERIAL PRIMARY KEY,
                           id_usuario BIGINT NOT NULL REFERENCES usuarios(id_usuario),
                           tipo VARCHAR(100) NOT NULL
);

CREATE TABLE seguros (
                         id_seguro BIGSERIAL PRIMARY KEY,
                         id_usuario BIGINT NOT NULL REFERENCES usuarios(id_usuario),
                         estado VARCHAR(100) NOT NULL
);

CREATE TABLE contratos_logistica (
                                     id BIGSERIAL PRIMARY KEY,
                                     codigo_contrato VARCHAR(100) NOT NULL UNIQUE,
                                     tipo_contrato VARCHAR(50) NOT NULL,
                                     nombre_conductor VARCHAR(255) NOT NULL,
                                     precio_paradas NUMERIC(15, 2),
                                     precio NUMERIC(15, 2),
                                     tipo_vehiculo VARCHAR(100) NOT NULL,
                                     fecha_inicio DATE NOT NULL,
                                     fecha_final DATE NOT NULL,
                                     id_usuario BIGINT NOT NULL REFERENCES usuarios(id_usuario),
                                     id_vehiculo BIGINT NOT NULL REFERENCES vehiculos(id_vehiculo),
                                     created_at TIMESTAMP NOT NULL DEFAULT NOW(),

                                     CHECK (fecha_final >= fecha_inicio)
);