-- Permite usuarios sin empresa (empresa_id NULL).
-- Ejecutar en phpMyAdmin si al guardar usuarios sin empresa MySQL devuelve error de NOT NULL.
-- Si Hibernate (ddl-auto=update) ya actualizó la columna, este script no es necesario.

USE globalisosecurity;

ALTER TABLE usuarios
  MODIFY COLUMN empresa_id BIGINT NULL;
