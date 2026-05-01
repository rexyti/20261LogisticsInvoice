-- EXTENSION
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- contratos
CREATE TABLE contratos (
                           id UUID PRIMARY KEY,
                           tipo_contratacion VARCHAR(255) NOT NULL,
                           tarifa NUMERIC(19, 4) NOT NULL
);

-- transportista
CREATE TABLE transportista (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               conductor_id UUID NOT NULL UNIQUE,
                               nombre VARCHAR(255) NOT NULL
);

-- ruta
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

-- liquidaciones
CREATE TABLE liquidaciones (
                               id UUID PRIMARY KEY,
                               id_ruta UUID NOT NULL,
                               id_contrato UUID NOT NULL,
                               fecha_calculo TIMESTAMP NOT NULL,
                               estado_liquidacion VARCHAR(50),
                               usuario_id UUID,

                               CONSTRAINT uq_liquidacion_id_ruta UNIQUE (id_ruta),
                               CONSTRAINT uq_liquidacion_id_contrato UNIQUE (id_contrato),

                               CONSTRAINT fk_liquidacion_contrato
                                   FOREIGN KEY (id_contrato) REFERENCES contratos(id),

                               CONSTRAINT fk_liquidacion_ruta
                                   FOREIGN KEY (id_ruta) REFERENCES ruta(id)
);

-- parada
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

                        UNIQUE (parada_id, ruta_id)
);

-- ajustes
CREATE TABLE ajustes (
                         id UUID PRIMARY KEY,
                         id_liquidacion UUID NOT NULL,
                         tipo VARCHAR(50) NOT NULL,
                         monto NUMERIC(19, 4) NOT NULL,
                         motivo VARCHAR(255) NOT NULL,
                         motivo_falla VARCHAR(100),
                         created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                         updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                         CONSTRAINT fk_ajuste_liquidacion
                             FOREIGN KEY (id_liquidacion) REFERENCES liquidaciones(id)
);

-- auditoria
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

                                       CONSTRAINT fk_auditoria_liquidacion
                                           FOREIGN KEY (id_liquidacion) REFERENCES liquidaciones(id)
);

-- índices básicos
CREATE INDEX idx_liquidaciones_id_ruta ON liquidaciones(id_ruta);
CREATE INDEX idx_ajustes_id_liquidacion ON ajustes(id_liquidacion);
CREATE INDEX idx_auditoria_id_liquidacion ON auditoria_liquidacion(id_liquidacion);