-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 07-04-2026 a las 17:24:20
-- Versión del servidor: 5.7.44-log
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `globalisosecurity`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `capacitaciones`
--

CREATE TABLE `capacitaciones` (
  `id` bigint(20) NOT NULL,
  `descripcion` varchar(2000) DEFAULT NULL,
  `estado` varchar(255) NOT NULL,
  `fecha_finalizacion` datetime(6) DEFAULT NULL,
  `material_url` varchar(1000) DEFAULT NULL,
  `titulo` varchar(255) NOT NULL,
  `video_url` varchar(1000) DEFAULT NULL,
  `servicio_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `checklists`
--

CREATE TABLE `checklists` (
  `id` bigint(20) NOT NULL,
  `descripcion` varchar(1000) DEFAULT NULL,
  `estado` varchar(255) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `servicio_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `checklists`
--

INSERT INTO `checklists` (`id`, `descripcion`, `estado`, `nombre`, `servicio_id`) VALUES
(1, 'Checklist base para el servicio de Clínica San Miguel', 'PENDIENTE', 'Checklist ISO 27001 - Clínica San Miguel', 1),
(2, 'Checklist base para el servicio de Universidad del Norte Digital', 'PENDIENTE', 'Checklist ISO 27001 - Universidad del Norte Digital', 2),
(3, 'Checklist base para el servicio de FinanPlus S.A.S.', 'PENDIENTE', 'Checklist ISO 27001 - FinanPlus S.A.S.', 3),
(4, 'Checklist base para el servicio de TechNova Solutions', 'PENDIENTE', 'Checklist ISO 27001 - TechNova Solutions', 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `constancias_capacitacion`
--

CREATE TABLE `constancias_capacitacion` (
  `id` bigint(20) NOT NULL,
  `cargo` varchar(255) NOT NULL,
  `codigo_interno` varchar(255) DEFAULT NULL,
  `documento` varchar(255) NOT NULL,
  `fecha_firma` datetime(6) DEFAULT NULL,
  `nombre_completo` varchar(255) NOT NULL,
  `capacitacion_id` bigint(20) NOT NULL,
  `servicio_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empresas`
--

CREATE TABLE `empresas` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `empresas`
--

INSERT INTO `empresas` (`id`, `nombre`) VALUES
(1, 'Tecnologia'),
(2, 'Empresa Demo'),
(3, 'Clínica San Miguel'),
(4, 'Universidad del Norte Digital'),
(5, 'FinanPlus S.A.S.'),
(6, 'TechNova Solutions');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `evaluaciones`
--

CREATE TABLE `evaluaciones` (
  `id` bigint(20) NOT NULL,
  `comentarios` varchar(2000) DEFAULT NULL,
  `estado` varchar(255) NOT NULL,
  `fecha_evaluacion` datetime(6) DEFAULT NULL,
  `resultado_general` varchar(1000) NOT NULL,
  `servicio_id` bigint(20) NOT NULL,
  `observacion` varchar(1000) DEFAULT NULL,
  `item_checklist_id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `firmas`
--

CREATE TABLE `firmas` (
  `id` bigint(20) NOT NULL,
  `cargo` varchar(255) NOT NULL,
  `estado` varchar(255) NOT NULL,
  `fecha_firma` datetime(6) DEFAULT NULL,
  `nombre_firmante` varchar(255) NOT NULL,
  `servicio_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `items_checklist`
--

CREATE TABLE `items_checklist` (
  `id` bigint(20) NOT NULL,
  `estado` varchar(255) NOT NULL,
  `observacion` varchar(1000) DEFAULT NULL,
  `pregunta` varchar(1000) NOT NULL,
  `respuesta` varchar(500) DEFAULT NULL,
  `checklist_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `items_checklist`
--

INSERT INTO `items_checklist` (`id`, `estado`, `observacion`, `pregunta`, `respuesta`, `checklist_id`) VALUES
(1, 'PENDIENTE', NULL, '¿La organización tiene una política formal de seguridad de la información aprobada?', NULL, 1),
(2, 'PENDIENTE', NULL, '¿La organización tiene una política formal de seguridad de la información aprobada?', NULL, 2),
(3, 'PENDIENTE', NULL, '¿La organización tiene una política formal de seguridad de la información aprobada?', NULL, 3),
(4, 'PENDIENTE', NULL, '¿La organización tiene una política formal de seguridad de la información aprobada?', NULL, 4),
(8, 'PENDIENTE', NULL, '¿Se encuentran definidos los roles y responsabilidades de seguridad de la información?', NULL, 1),
(9, 'PENDIENTE', NULL, '¿Se encuentran definidos los roles y responsabilidades de seguridad de la información?', NULL, 2),
(10, 'PENDIENTE', NULL, '¿Se encuentran definidos los roles y responsabilidades de seguridad de la información?', NULL, 3),
(11, 'PENDIENTE', NULL, '¿Se encuentran definidos los roles y responsabilidades de seguridad de la información?', NULL, 4),
(15, 'PENDIENTE', NULL, '¿Existe inventario y clasificación de activos de información?', NULL, 1),
(16, 'PENDIENTE', NULL, '¿Existe inventario y clasificación de activos de información?', NULL, 2),
(17, 'PENDIENTE', NULL, '¿Existe inventario y clasificación de activos de información?', NULL, 3),
(18, 'PENDIENTE', NULL, '¿Existe inventario y clasificación de activos de información?', NULL, 4),
(22, 'PENDIENTE', NULL, '¿Se aplican controles de acceso según roles y privilegios mínimos?', NULL, 1),
(23, 'PENDIENTE', NULL, '¿Se aplican controles de acceso según roles y privilegios mínimos?', NULL, 2),
(24, 'PENDIENTE', NULL, '¿Se aplican controles de acceso según roles y privilegios mínimos?', NULL, 3),
(25, 'PENDIENTE', NULL, '¿Se aplican controles de acceso según roles y privilegios mínimos?', NULL, 4),
(29, 'PENDIENTE', NULL, '¿Se realiza gestión de incidentes de seguridad de la información?', NULL, 1),
(30, 'PENDIENTE', NULL, '¿Se realiza gestión de incidentes de seguridad de la información?', NULL, 2),
(31, 'PENDIENTE', NULL, '¿Se realiza gestión de incidentes de seguridad de la información?', NULL, 3),
(32, 'PENDIENTE', NULL, '¿Se realiza gestión de incidentes de seguridad de la información?', NULL, 4),
(36, 'PENDIENTE', NULL, '¿Se ejecutan copias de seguridad y pruebas de restauración?', NULL, 1),
(37, 'PENDIENTE', NULL, '¿Se ejecutan copias de seguridad y pruebas de restauración?', NULL, 2),
(38, 'PENDIENTE', NULL, '¿Se ejecutan copias de seguridad y pruebas de restauración?', NULL, 3),
(39, 'PENDIENTE', NULL, '¿Se ejecutan copias de seguridad y pruebas de restauración?', NULL, 4),
(43, 'PENDIENTE', NULL, '¿Se controlan los accesos físicos y ambientales a los recursos críticos?', NULL, 1),
(44, 'PENDIENTE', NULL, '¿Se controlan los accesos físicos y ambientales a los recursos críticos?', NULL, 2),
(45, 'PENDIENTE', NULL, '¿Se controlan los accesos físicos y ambientales a los recursos críticos?', NULL, 3),
(46, 'PENDIENTE', NULL, '¿Se controlan los accesos físicos y ambientales a los recursos críticos?', NULL, 4),
(50, 'PENDIENTE', NULL, '¿Se capacita al personal en seguridad de la información y buenas prácticas?', NULL, 1),
(51, 'PENDIENTE', NULL, '¿Se capacita al personal en seguridad de la información y buenas prácticas?', NULL, 2),
(52, 'PENDIENTE', NULL, '¿Se capacita al personal en seguridad de la información y buenas prácticas?', NULL, 3),
(53, 'PENDIENTE', NULL, '¿Se capacita al personal en seguridad de la información y buenas prácticas?', NULL, 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `logs_auditoria`
--

CREATE TABLE `logs_auditoria` (
  `id` bigint(20) NOT NULL,
  `accion` varchar(255) NOT NULL,
  `descripcion` varchar(1000) NOT NULL,
  `fecha` datetime(6) NOT NULL,
  `ip` varchar(255) NOT NULL,
  `modulo` varchar(255) NOT NULL,
  `usuario` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `logs_auditoria`
--

INSERT INTO `logs_auditoria` (`id`, `accion`, `descripcion`, `fecha`, `ip`, `modulo`, `usuario`) VALUES
(1, 'CREAR', 'Se creó la empresa: USB', '2026-04-02 23:37:54.000000', '0:0:0:0:0:0:0:1', 'EMPRESAS', 'admin@globalisosecurity.com'),
(2, 'ELIMINAR', 'Se eliminó la empresa con ID: 7 y nombre: USB', '2026-04-02 23:38:16.000000', '0:0:0:0:0:0:0:1', 'EMPRESAS', 'admin@globalisosecurity.com'),
(3, 'CREAR', 'Se creó la empresa: USB', '2026-04-02 23:46:26.000000', '0:0:0:0:0:0:0:1', 'EMPRESAS', 'admin@globalisosecurity.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id`, `nombre`) VALUES
(1, 'ADMINISTRADOR'),
(2, 'IMPLEMENTADOR'),
(3, 'AUDITOR'),
(4, 'CAPACITADOR'),
(5, 'USUARIO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sectores`
--

CREATE TABLE `sectores` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `sectores`
--

INSERT INTO `sectores` (`id`, `nombre`) VALUES
(2, 'Educación'),
(3, 'Financiero'),
(5, 'Manufactura'),
(1, 'Salud'),
(4, 'Tecnología');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `servicios`
--

CREATE TABLE `servicios` (
  `id` bigint(20) NOT NULL,
  `estado` varchar(255) NOT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `empresa_id` bigint(20) NOT NULL,
  `sector_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `servicios`
--

INSERT INTO `servicios` (`id`, `estado`, `fecha_creacion`, `empresa_id`, `sector_id`) VALUES
(1, 'BORRADOR', '2026-04-01 14:56:51.000000', 3, 1),
(2, 'EN_PROCESO', '2026-04-01 14:56:51.000000', 4, 2),
(3, 'FINALIZADO', '2026-04-01 14:56:51.000000', 5, 3),
(4, 'BORRADOR', '2026-04-01 14:56:51.000000', 6, 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `empresa_id` bigint(20) DEFAULT NULL,
  `rol_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `email`, `nombre`, `password`, `empresa_id`, `rol_id`) VALUES
(2, 'admin@globalisosecurity.com', 'Admin', '$2a$10$EAafDJN4cVre3T.VISOWcONvBCf9xesm2ElRwZdzKd.oLFhmIoyFu', 1, 1),
(53, 'af@gmail.com', 'andres', '$2a$10$9K7NOucRHOwp92axbqQmzO3lbXhJP.XLW87nHLj64OKLS/fy3Jee2', 1, 3),
(54, 'camila@gmail.com', 'camila', '$2a$10$.J5/c/q.DUO3Fnq.8QIAUOmAydeqRy6rlfqlRiaXTiHDHEKt1brra', 4, 2),
(56, 'pardo@gmail.com', 'pardo', '$2a$10$evgwgZ7.nKAPdJWso6c2U.KUAR4BQUCssHrAEPquHHLhccgEit7a2', 5, 4);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `capacitaciones`
--
ALTER TABLE `capacitaciones`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK25xe0wghoqrixf93oy3naoxkr` (`servicio_id`);

--
-- Indices de la tabla `checklists`
--
ALTER TABLE `checklists`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKr8lgcu6ia6jxdbinfnk8dp2v5` (`servicio_id`);

--
-- Indices de la tabla `constancias_capacitacion`
--
ALTER TABLE `constancias_capacitacion`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK1yeca148sht17a9nrhgmhf502` (`capacitacion_id`),
  ADD KEY `FK24xf5m59mkgkfe2hb13esh7db` (`servicio_id`);

--
-- Indices de la tabla `empresas`
--
ALTER TABLE `empresas`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `evaluaciones`
--
ALTER TABLE `evaluaciones`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKqc2nt0r4wk6bgmluhe8cl5osc` (`servicio_id`),
  ADD KEY `FKtfenpri64kaoxkxoreigjjs5` (`item_checklist_id`),
  ADD KEY `FKcmy92el8b3ggm9vnqfob2btc` (`usuario_id`);

--
-- Indices de la tabla `firmas`
--
ALTER TABLE `firmas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKfav5tw4gesujkpc8egjdi815m` (`servicio_id`);

--
-- Indices de la tabla `items_checklist`
--
ALTER TABLE `items_checklist`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKlluvqhd61prqatapfayis6qx9` (`checklist_id`);

--
-- Indices de la tabla `logs_auditoria`
--
ALTER TABLE `logs_auditoria`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `sectores`
--
ALTER TABLE `sectores`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_2j7o2b9djcloq4ei5fdvbcgq` (`nombre`);

--
-- Indices de la tabla `servicios`
--
ALTER TABLE `servicios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKd05y97js7fi4dnemp51g3pg3q` (`empresa_id`),
  ADD KEY `FK5ag51bcdrn24s3cu84ijeq5np` (`sector_id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_kfsp0s1tflm1cwlj8idhqsad0` (`email`),
  ADD KEY `FK9v93lqnass5yqhhsyprr9fdv2` (`empresa_id`),
  ADD KEY `FKqf5elo4jcq7qrt83oi0qmenjo` (`rol_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `capacitaciones`
--
ALTER TABLE `capacitaciones`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `checklists`
--
ALTER TABLE `checklists`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `constancias_capacitacion`
--
ALTER TABLE `constancias_capacitacion`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `empresas`
--
ALTER TABLE `empresas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `evaluaciones`
--
ALTER TABLE `evaluaciones`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `firmas`
--
ALTER TABLE `firmas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `items_checklist`
--
ALTER TABLE `items_checklist`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=57;

--
-- AUTO_INCREMENT de la tabla `logs_auditoria`
--
ALTER TABLE `logs_auditoria`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `sectores`
--
ALTER TABLE `sectores`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `servicios`
--
ALTER TABLE `servicios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=57;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `capacitaciones`
--
ALTER TABLE `capacitaciones`
  ADD CONSTRAINT `FK25xe0wghoqrixf93oy3naoxkr` FOREIGN KEY (`servicio_id`) REFERENCES `servicios` (`id`);

--
-- Filtros para la tabla `checklists`
--
ALTER TABLE `checklists`
  ADD CONSTRAINT `FKr8lgcu6ia6jxdbinfnk8dp2v5` FOREIGN KEY (`servicio_id`) REFERENCES `servicios` (`id`);

--
-- Filtros para la tabla `constancias_capacitacion`
--
ALTER TABLE `constancias_capacitacion`
  ADD CONSTRAINT `FK1yeca148sht17a9nrhgmhf502` FOREIGN KEY (`capacitacion_id`) REFERENCES `capacitaciones` (`id`),
  ADD CONSTRAINT `FK24xf5m59mkgkfe2hb13esh7db` FOREIGN KEY (`servicio_id`) REFERENCES `servicios` (`id`);

--
-- Filtros para la tabla `evaluaciones`
--
ALTER TABLE `evaluaciones`
  ADD CONSTRAINT `FKcmy92el8b3ggm9vnqfob2btc` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `FKqc2nt0r4wk6bgmluhe8cl5osc` FOREIGN KEY (`servicio_id`) REFERENCES `servicios` (`id`),
  ADD CONSTRAINT `FKtfenpri64kaoxkxoreigjjs5` FOREIGN KEY (`item_checklist_id`) REFERENCES `items_checklist` (`id`);

--
-- Filtros para la tabla `firmas`
--
ALTER TABLE `firmas`
  ADD CONSTRAINT `FKfav5tw4gesujkpc8egjdi815m` FOREIGN KEY (`servicio_id`) REFERENCES `servicios` (`id`);

--
-- Filtros para la tabla `items_checklist`
--
ALTER TABLE `items_checklist`
  ADD CONSTRAINT `FKlluvqhd61prqatapfayis6qx9` FOREIGN KEY (`checklist_id`) REFERENCES `checklists` (`id`);

--
-- Filtros para la tabla `servicios`
--
ALTER TABLE `servicios`
  ADD CONSTRAINT `FK5ag51bcdrn24s3cu84ijeq5np` FOREIGN KEY (`sector_id`) REFERENCES `sectores` (`id`),
  ADD CONSTRAINT `FKd05y97js7fi4dnemp51g3pg3q` FOREIGN KEY (`empresa_id`) REFERENCES `empresas` (`id`);

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `FK9v93lqnass5yqhhsyprr9fdv2` FOREIGN KEY (`empresa_id`) REFERENCES `empresas` (`id`),
  ADD CONSTRAINT `FKqf5elo4jcq7qrt83oi0qmenjo` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
