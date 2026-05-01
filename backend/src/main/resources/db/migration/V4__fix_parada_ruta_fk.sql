-- 1. Eliminar constraint viejo (si existe)
ALTER TABLE parada
DROP CONSTRAINT IF EXISTS uk_parada_parada_id_ruta_id;

-- 2. Eliminar columna vieja
ALTER TABLE parada
DROP COLUMN IF EXISTS ruta_id;

-- 3. Crear nueva columna FK correcta
ALTER TABLE parada
    ADD COLUMN ruta_entity_id UUID NOT NULL;

-- 4. Crear foreign key hacia ruta(id)
ALTER TABLE parada
    ADD CONSTRAINT fk_parada_ruta
        FOREIGN KEY (ruta_entity_id) REFERENCES ruta(id);

-- 5. Crear unique constraint corregido
ALTER TABLE parada
    ADD CONSTRAINT uk_parada_parada_id_ruta_entity_id
        UNIQUE (parada_id, ruta_entity_id);