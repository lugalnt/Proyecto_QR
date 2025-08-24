-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 24-08-2025 a las 23:49:29
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `fcs`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `area`
--

CREATE TABLE `area` (
  `Id_Area` int(11) NOT NULL,
  `Nombre_Area` varchar(40) NOT NULL,
  `Descripcion_Area` text NOT NULL,
  `NumeroCAR_Area` int(3) NOT NULL,
  `Codigo_Area` varchar(43) NOT NULL,
  `JSON_Area` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`JSON_Area`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `area_reporte`
--

CREATE TABLE `area_reporte` (
  `Id_Reporte` int(11) NOT NULL,
  `Id_Area` int(11) NOT NULL,
  `Nombre_Area` varchar(40) DEFAULT NULL,
  `NumeroCAR_Area` int(3) DEFAULT NULL,
  `FechaRegistro_Reporte` datetime NOT NULL,
  `FechaModificacion_Reporte` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `maquila`
--

CREATE TABLE `maquila` (
  `Id_Maquila` int(11) NOT NULL,
  `Nombre_Maquila` varchar(50) NOT NULL,
  `Contraseña_Maquila` int(66) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `maquila_area`
--

CREATE TABLE `maquila_area` (
  `Id_Maquila` int(11) NOT NULL,
  `Id_Area` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reporte`
--

CREATE TABLE `reporte` (
  `Id_Reporte` int(11) NOT NULL,
  `FechaRegistro_Reporte` datetime NOT NULL DEFAULT current_timestamp(),
  `FechaModificacion_Reporte` int(11) DEFAULT NULL,
  `CARTotal_Reporte` int(3) NOT NULL,
  `CARRevisadas_Reporte` int(3) NOT NULL,
  `Estado_Reporte` varchar(20) NOT NULL,
  `JSON_Reporte` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`JSON_Reporte`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `Id_Usuario` int(11) NOT NULL,
  `Nombre_Usuario` varchar(50) NOT NULL,
  `Password_Usuario` varchar(66) NOT NULL,
  `Telefono_Usuario_Usuario` varchar(10) NOT NULL,
  `Puesto_Usuario` varchar(35) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`Id_Usuario`, `Nombre_Usuario`, `Password_Usuario`, `Telefono_Usuario_Usuario`, `Puesto_Usuario`) VALUES
(1, 'Yoz', '$2y$10$naZhveR7uw3PNNg//5OFn.bw.LT/FlC3x.5NoVEhhwUKw.DjONHwu', '631111111', 'Administrador');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_reporte`
--

CREATE TABLE `usuario_reporte` (
  `Id_Usuario` int(11) NOT NULL,
  `Id_Reporte` int(11) NOT NULL,
  `FechaRegistro_Reporte` datetime NOT NULL,
  `FechaModificacion_Reporte` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `area`
--
ALTER TABLE `area`
  ADD PRIMARY KEY (`Id_Area`);

--
-- Indices de la tabla `area_reporte`
--
ALTER TABLE `area_reporte`
  ADD PRIMARY KEY (`Id_Reporte`,`Id_Area`),
  ADD KEY `Id_Area` (`Id_Area`);

--
-- Indices de la tabla `maquila`
--
ALTER TABLE `maquila`
  ADD PRIMARY KEY (`Id_Maquila`);

--
-- Indices de la tabla `maquila_area`
--
ALTER TABLE `maquila_area`
  ADD PRIMARY KEY (`Id_Maquila`,`Id_Area`),
  ADD KEY `Id_Area` (`Id_Area`);

--
-- Indices de la tabla `reporte`
--
ALTER TABLE `reporte`
  ADD PRIMARY KEY (`Id_Reporte`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`Id_Usuario`);

--
-- Indices de la tabla `usuario_reporte`
--
ALTER TABLE `usuario_reporte`
  ADD PRIMARY KEY (`Id_Usuario`,`Id_Reporte`),
  ADD KEY `Id_Reporte` (`Id_Reporte`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `area`
--
ALTER TABLE `area`
  MODIFY `Id_Area` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `maquila`
--
ALTER TABLE `maquila`
  MODIFY `Id_Maquila` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `reporte`
--
ALTER TABLE `reporte`
  MODIFY `Id_Reporte` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `Id_Usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `area_reporte`
--
ALTER TABLE `area_reporte`
  ADD CONSTRAINT `area_reporte_ibfk_1` FOREIGN KEY (`Id_Reporte`) REFERENCES `reporte` (`Id_Reporte`) ON UPDATE CASCADE,
  ADD CONSTRAINT `area_reporte_ibfk_2` FOREIGN KEY (`Id_Area`) REFERENCES `area` (`Id_Area`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `maquila_area`
--
ALTER TABLE `maquila_area`
  ADD CONSTRAINT `maquila_area_ibfk_1` FOREIGN KEY (`Id_Maquila`) REFERENCES `maquila` (`Id_Maquila`) ON UPDATE CASCADE,
  ADD CONSTRAINT `maquila_area_ibfk_2` FOREIGN KEY (`Id_Area`) REFERENCES `area` (`Id_Area`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `usuario_reporte`
--
ALTER TABLE `usuario_reporte`
  ADD CONSTRAINT `usuario_reporte_ibfk_1` FOREIGN KEY (`Id_Usuario`) REFERENCES `usuario` (`Id_Usuario`) ON UPDATE CASCADE,
  ADD CONSTRAINT `usuario_reporte_ibfk_2` FOREIGN KEY (`Id_Reporte`) REFERENCES `reporte` (`Id_Reporte`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
