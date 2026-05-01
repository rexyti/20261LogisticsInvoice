-- V4__fix_parada_ruta_fk.sql

-- 1. Crear nueva columna
ALTER TABLE parada
    ADD COLUMN IF NOT EXISTS ruta_entity_id UUID;

-- 2. Eliminar constraint viejo
ALTER TABLE parada
DROP CONSTRAINT IF EXISTS uk_parada_parada_id_ruta_id;

-- 3. Eliminar columna vieja
ALTER TABLE parada
DROP COLUMN IF EXISTS ruta_id;

-- 4. Hacer NOT NULL
ALTER TABLE parada
    ALTER COLUMN ruta_entity_id SET NOT NULL;

-- 5. Crear FK
ALTER TABLE parada
    ADD CONSTRAINT fk_parada_ruta_entity
        FOREIGN KEY (ruta_entity_id) REFERENCES ruta(id);

-- 6. Unique correcto
ALTER TABLE parada
    ADD CONSTRAINT uk_parada_parada_id_ruta_entity_id
        UNIQUE (parada_id, ruta_entity_id);