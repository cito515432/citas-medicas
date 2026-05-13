-- Usuarios de demostración con roles variados por empresa.
-- Ejecutar en phpMyAdmin sobre la base globalisosecurity (después de roles y empresas creados).
-- Contraseña para todos: password
-- Hash BCrypt (cost 10), compatible con Spring Security:

USE globalisosecurity;

START TRANSACTION;

SET @pwd := '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG';

-- Clínica San Miguel: 1 implementador + 1 auditor (= 2 servicios en el panel)
INSERT INTO usuarios (nombre, email, password, rol_id, empresa_id)
SELECT 'Ana Ríos', 'ana.impl@clinica.demo', @pwd, r.id, e.id
FROM roles r, empresas e
WHERE r.nombre = 'IMPLEMENTADOR' AND e.nombre = 'Clínica San Miguel'
  AND NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.email = 'ana.impl@clinica.demo')
LIMIT 1;

INSERT INTO usuarios (nombre, email, password, rol_id, empresa_id)
SELECT 'Luis Ortega', 'luis.aud@clinica.demo', @pwd, r.id, e.id
FROM roles r, empresas e
WHERE r.nombre = 'AUDITOR' AND e.nombre = 'Clínica San Miguel'
  AND NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.email = 'luis.aud@clinica.demo')
LIMIT 1;

-- Universidad del Norte Digital: capacitador + implementador (= 2)
INSERT INTO usuarios (nombre, email, password, rol_id, empresa_id)
SELECT 'Carla Méndez', 'carla.cap@uninorte.demo', @pwd, r.id, e.id
FROM roles r, empresas e
WHERE r.nombre = 'CAPACITADOR' AND e.nombre = 'Universidad del Norte Digital'
  AND NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.email = 'carla.cap@uninorte.demo')
LIMIT 1;

INSERT INTO usuarios (nombre, email, password, rol_id, empresa_id)
SELECT 'Miguel Paredes', 'miguel.impl@uninorte.demo', @pwd, r.id, e.id
FROM roles r, empresas e
WHERE r.nombre = 'IMPLEMENTADOR' AND e.nombre = 'Universidad del Norte Digital'
  AND NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.email = 'miguel.impl@uninorte.demo')
LIMIT 1;

-- FinanPlus S.A.S.: solo auditor (= 1)
INSERT INTO usuarios (nombre, email, password, rol_id, empresa_id)
SELECT 'Sandra Vélez', 'sandra.aud@finanplus.demo', @pwd, r.id, e.id
FROM roles r, empresas e
WHERE r.nombre = 'AUDITOR' AND e.nombre = 'FinanPlus S.A.S.'
  AND NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.email = 'sandra.aud@finanplus.demo')
LIMIT 1;

-- TechNova Solutions: los tres roles (= 3)
INSERT INTO usuarios (nombre, email, password, rol_id, empresa_id)
SELECT 'Pedro Castillo', 'pedro.impl@technova.demo', @pwd, r.id, e.id
FROM roles r, empresas e
WHERE r.nombre = 'IMPLEMENTADOR' AND e.nombre = 'TechNova Solutions'
  AND NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.email = 'pedro.impl@technova.demo')
LIMIT 1;

INSERT INTO usuarios (nombre, email, password, rol_id, empresa_id)
SELECT 'Laura Gómez', 'laura.cap@technova.demo', @pwd, r.id, e.id
FROM roles r, empresas e
WHERE r.nombre = 'CAPACITADOR' AND e.nombre = 'TechNova Solutions'
  AND NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.email = 'laura.cap@technova.demo')
LIMIT 1;

INSERT INTO usuarios (nombre, email, password, rol_id, empresa_id)
SELECT 'Diego Herrera', 'diego.aud@technova.demo', @pwd, r.id, e.id
FROM roles r, empresas e
WHERE r.nombre = 'AUDITOR' AND e.nombre = 'TechNova Solutions'
  AND NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.email = 'diego.aud@technova.demo')
LIMIT 1;

-- Usuario cliente (no suma en columna Servicios del panel; solo variedad en listado de usuarios)
INSERT INTO usuarios (nombre, email, password, rol_id, empresa_id)
SELECT 'Cliente Demo', 'cliente.usuario@clinica.demo', @pwd, r.id, e.id
FROM roles r, empresas e
WHERE r.nombre = 'USUARIO' AND e.nombre = 'Clínica San Miguel'
  AND NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.email = 'cliente.usuario@clinica.demo')
LIMIT 1;

COMMIT;
