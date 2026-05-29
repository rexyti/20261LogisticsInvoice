-- V4__add_voucher_to_pagos.sql
-- Agrega campos de voucher bancario a la tabla pagos.
-- Necesario para el pago sincrónico con el banco externo.

ALTER TABLE pagos
    ADD COLUMN IF NOT EXISTS numero_voucher      VARCHAR(100),
    ADD COLUMN IF NOT EXISTS fecha_procesamiento  TIMESTAMPTZ;
