-- V3__add_fecha_hora_gestion_to_parada.sql
-- Agrega columna fecha_hora_gestion a la tabla parada.
-- Requerida por ParadaEntity para registrar cuándo se gestionó la parada.

ALTER TABLE parada
    ADD COLUMN fecha_hora_gestion TIMESTAMP;