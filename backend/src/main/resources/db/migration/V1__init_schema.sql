CREATE TABLE transportista (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conductor_id UUID NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL
);

CREATE TABLE ruta (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ruta_id UUID NOT NULL UNIQUE,
    transportista_id UUID NOT NULL REFERENCES transportista(id),
    tipo_vehiculo VARCHAR(50),
    modelo_contrato VARCHAR(255),
    fecha_inicio_transito TIMESTAMP NOT NULL,
    fecha_cierre TIMESTAMP NOT NULL,
    estado_procesamiento VARCHAR(50) NOT NULL
);

CREATE TABLE parada (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parada_id UUID NOT NULL,
    ruta_id UUID NOT NULL REFERENCES ruta(id),
    estado VARCHAR(50) NOT NULL,
    motivo_falla VARCHAR(100),
    responsable VARCHAR(50)
);
