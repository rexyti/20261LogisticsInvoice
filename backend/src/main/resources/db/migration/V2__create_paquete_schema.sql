-- V2__create_paquete.sql

CREATE TABLE paquetes (
                          id_paquete BIGINT PRIMARY KEY,
                          id_ruta BIGINT NOT NULL,
                          estado_actual VARCHAR(50),
                          updated_at TIMESTAMP
);

CREATE TABLE historial_estados (
                                   id BIGSERIAL PRIMARY KEY,
                                   id_paquete BIGINT NOT NULL,
                                   estado VARCHAR(50) NOT NULL,
                                   fecha TIMESTAMP NOT NULL
);

CREATE TABLE log_sincronizacion (
                                    id BIGSERIAL PRIMARY KEY,
                                    id_paquete BIGINT NOT NULL,
                                    codigo_respuesta_http INTEGER,
                                    json_recibido TEXT,
                                    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_historial_id_paquete ON historial_estados (id_paquete);
CREATE INDEX idx_historial_fecha ON historial_estados (fecha);
CREATE INDEX idx_log_id_paquete ON log_sincronizacion (id_paquete);