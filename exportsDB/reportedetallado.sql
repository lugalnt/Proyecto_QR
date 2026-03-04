-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 05-03-2026 a las 00:55:12
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
  `JSON_Area` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`JSON_Area`)),
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `area`
--

INSERT INTO `area` (`Id_Area`, `Nombre_Area`, `Descripcion_Area`, `NumeroCAR_Area`, `Codigo_Area`, `JSON_Area`, `deleted_at`) VALUES
(1, 'AreaPrueba', 'AreaPrueba123', 2, 'c81265c1f76210f', '{\"Nombre_Area\":\"AreaPrueba\",\"Descripcion_Area\":\"AreaPrueba123\",\"Id_Maquila\":\"2\",\"cars\":[{\"name\":\"Prueba2\",\"properties\":[{\"label\":\"Prueba\",\"type\":\"bool\",\"default\":true},{\"label\":\"Prueba2\",\"type\":\"number\",\"min\":0,\"max\":12,\"step\":1}]},{\"name\":\"Cosa nueva que edite\",\"properties\":[{\"label\":\"Si\",\"type\":\"bool\",\"default\":true}]}]}', NULL),
(2, 'PruebaMasCar', 'Si', 0, 'f79d80533ed73be', '{\"area_name\":\"PruebaMasCar\",\"area_description\":\"Si\",\"maquila_id\":\"3\",\"cars\":[]}', NULL),
(3, 'Prueba3', 'Esta es la prueba 3 de las areas con varios Car', 2, 'c1da3dbbc3cc6a4', '{\"area_name\":\"Prueba3\",\"area_description\":\"Esta es la prueba 3 de las areas con varios Car\",\"maquila_id\":\"3\",\"cars\":[{\"name\":\"CAR 1\",\"properties\":[{\"label\":\"Si o No\",\"type\":\"bool\",\"default\":true},{\"label\":\"Un numero\",\"type\":\"number\",\"min\":1,\"max\":2,\"step\":1}]},{\"name\":\"CAR 2\",\"properties\":[{\"label\":\"Descripcion\",\"type\":\"text\",\"placeholder\":\"\"}]}]}', NULL),
(4, 'Otra Area', 'Esta area esta entre aqui y alla y si', 1, 'f092ac9b708ef6c', '{\"area_name\":\"Otra Area\",\"area_description\":\"Esta area esta entre aqui y alla y si\",\"maquila_id\":\"2\",\"cars\":[{\"name\":\"Valvula\",\"properties\":[{\"label\":\"Funcionamiento\",\"type\":\"bool\",\"default\":true},{\"label\":\"Giros\",\"type\":\"number\",\"min\":0,\"max\":10,\"step\":1},{\"label\":\"Ultima revision\",\"type\":\"date\"}]}]}', NULL),
(5, 'Ala Izquiuerda', 'El ala Izquierda de la maquila', 1, '54155a5acb0d5fc', '{\"Nombre_Area\":\"Ala Izquiuerda\",\"Descripcion_Area\":\"El ala Izquierda de la maquila\",\"Id_Maquila\":\"4\",\"cars\":[{\"name\":\"Baño\",\"properties\":[{\"label\":\"Hay extintor\",\"type\":\"bool\",\"default\":true}]}]}', NULL),
(6, 'Area de platinado', 'Areas ', 2, 'b4ed65fb3b76cd6', '{\"area_name\":\"Area de platinado\",\"area_description\":\"Areas\",\"maquila_id\":\"4\",\"cars\":[{\"name\":\"Maquina 1\",\"properties\":[{\"label\":\"Funcionamiento\",\"type\":\"bool\",\"default\":true},{\"label\":\"Numero de la valvula\",\"type\":\"number\",\"min\":0,\"max\":100,\"step\":1}]},{\"name\":\"Maquina 2\",\"properties\":[{\"label\":\"Chequeo\",\"type\":\"bool\",\"default\":true}]}]}', NULL),
(7, 'Hidrante Platinadora', 'asa', 1, '3b9e6afbd391f02', '{\"area_name\":\"Hidrante Platinadora\",\"area_description\":\"asa\",\"maquila_id\":\"2\",\"cars\":[{\"name\":\"Funcionamiento del Hidrante\",\"properties\":[{\"label\":\"Funcionamiento\",\"type\":\"bool\",\"default\":true}]}]}', NULL),
(8, 'si', 'si', 1, '47fe52d28b1f858', '{\"area_name\":\"si\",\"area_description\":\"si\",\"maquila_id\":\"5\",\"cars\":[{\"name\":\"si\",\"properties\":[{\"label\":\"si\",\"type\":\"bool\",\"default\":true}]}]}', NULL),
(9, 'pruebaqr', 'pruebaqrdesc', 1, '33ed2b0ae854e7a', '{\"area_name\":\"pruebaqr\",\"area_description\":\"pruebaqrdesc\",\"maquila_id\":\"2\",\"cars\":[{\"name\":\"si\",\"properties\":[{\"label\":\"si\",\"type\":\"bool\",\"default\":true}]}]}', NULL),
(10, 'Hola', 'Prueba', 0, 'ea1b501ae67c440', '{\"area_name\":\"Hola\",\"area_description\":\"Prueba\",\"maquila_id\":\"2\",\"cars\":[]}', NULL),
(11, 'AreaIzNueva', 'si', 0, 'ab2e3a48e2b1152', '{\"area_name\":\"AreaIzNueva\",\"area_description\":\"si\",\"maquila_id\":\"4\",\"cars\":[]}', NULL),
(12, 'A_Prueba1217', 'Prueba del 17 del 12', 1, '4439c06fb586d28', '{\"Nombre_Area\":\"A_Prueba1217\",\"Descripcion_Area\":\"Prueba del 17 del 12\",\"Id_Maquila\":\"6\",\"cars\":[{\"name\":\"Cosa 1\",\"properties\":[{\"label\":\"Funcionamiento\",\"type\":\"bool\",\"default\":true},{\"label\":\"Numero 1\",\"type\":\"range\",\"min\":0,\"max\":20,\"step\":1}]}]}', NULL),
(13, 'AreaRapida', '123', 1, 'c6cae0fbfc3a738', '{\"Nombre_Area\":\"AreaRapida\",\"Descripcion_Area\":\"123\",\"Id_Maquila\":\"2\",\"cars\":[{\"name\":\"Rapida\",\"properties\":[{\"label\":\"Fun\",\"type\":\"bool\",\"default\":true}]}]}', NULL),
(14, 'Valvulas ', 'Valvulas', 2, '3b47d977e08ddbc', '{\"Nombre_Area\":\"Valvulas \",\"Descripcion_Area\":\"Valvulas\",\"Id_Maquila\":\"6\",\"cars\":[{\"name\":\"Valvula 1\",\"properties\":[{\"label\":\"Funcionamiento\",\"type\":\"bool\",\"default\":true},{\"label\":\"Rango\",\"type\":\"range\",\"min\":0,\"max\":10,\"step\":1}]},{\"name\":\"Valvula 20\",\"properties\":[{\"label\":\"Funciona\",\"type\":\"bool\",\"default\":true}]}]}', NULL),
(15, 'Sistema de Hidrantes y Rociadores', 'Area detallada', 12, '494777b21ed5247', '{\"Nombre_Area\":\"Sistema de Hidrantes y Rociadores\",\"Descripcion_Area\":\"Area detallada\",\"Id_Maquila\":\"6\",\"cars\":[{\"name\":\"Bomba Principal\",\"properties\":[{\"label\":\"Estado operativo\",\"type\":\"bool\",\"default\":true},{\"label\":\"PSI\",\"type\":\"number\",\"min\":0,\"max\":150,\"step\":1},{\"label\":\"Nivel Combustible\",\"type\":\"number\",\"min\":0,\"max\":100,\"step\":1}]},{\"name\":\"Bomba Jockey\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Estado Funcionamiento\",\"type\":\"text\",\"placeholder\":\"Ej.Automatico\"},{\"label\":\"Presion Arranque\",\"type\":\"number\",\"min\":0,\"max\":129,\"step\":1}]},{\"name\":\"Valvula de descarga\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Posicion\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Sello\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Hidrante Exterior H-01\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Acceso\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Tapones\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Fugas\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Hidrante Exterior H-02\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Acceso\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Presion Estatica\",\"type\":\"number\",\"min\":0,\"max\":120,\"step\":1}]},{\"name\":\"Gabinete de Manguera G-01\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Manguera\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Llave de paso\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Gabinete de Manguera G-02\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Manguera\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Llave de paso\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Tanque de reserva\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Nivel de agua\",\"type\":\"range\",\"min\":0,\"max\":100,\"step\":1},{\"label\":\"Valvula\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Manómetro de Red\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Lectura\",\"type\":\"number\",\"min\":0,\"max\":200,\"step\":1},{\"label\":\"Calibración\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Sensor de Flujo SF-01\",\"properties\":[{\"label\":\"Señal\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Voltaje\",\"type\":\"number\",\"min\":0,\"max\":30,\"step\":1}]},{\"name\":\"Tuberia\",\"properties\":[{\"label\":\"Pintura\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Soportes\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Corrosion\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Valvula de succión\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Posición\",\"type\":\"text\",\"placeholder\":\"Abierta\\/No Abierta\"},{\"label\":\"Existencia de candado\",\"type\":\"text\",\"placeholder\":\"Si\\/No\"}]}]}', NULL);

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
  `FechaModificacion_Reporte` datetime DEFAULT NULL,
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `area_reporte`
--

INSERT INTO `area_reporte` (`Id_Reporte`, `Id_Area`, `Nombre_Area`, `NumeroCAR_Area`, `FechaRegistro_Reporte`, `FechaModificacion_Reporte`, `deleted_at`) VALUES
(1, 3, NULL, NULL, '2025-09-16 04:17:55', '2025-09-16 04:17:55', NULL),
(2, 5, NULL, NULL, '2025-10-06 15:07:28', '2025-10-06 15:07:28', NULL),
(3, 6, NULL, NULL, '2025-10-12 13:24:54', '2025-10-12 13:24:54', NULL),
(4, 7, NULL, NULL, '2025-10-12 13:32:07', '2025-10-12 13:32:07', NULL),
(5, 5, NULL, NULL, '2025-11-15 17:15:56', '2025-11-15 17:15:56', NULL),
(6, 3, NULL, NULL, '2025-12-13 17:43:35', '2025-12-13 17:43:35', NULL),
(7, 12, NULL, NULL, '2025-12-17 14:45:28', '2025-12-17 14:45:28', NULL),
(8, 13, NULL, NULL, '2026-02-09 16:58:40', '2026-02-09 16:58:40', NULL),
(9, 15, NULL, NULL, '2026-03-04 13:44:56', '2026-03-04 13:44:56', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `maquila`
--

CREATE TABLE `maquila` (
  `Id_Maquila` int(11) NOT NULL,
  `Nombre_Maquila` varchar(50) NOT NULL,
  `Contraseña_Maquila` varchar(66) NOT NULL,
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `maquila`
--

INSERT INTO `maquila` (`Id_Maquila`, `Nombre_Maquila`, `Contraseña_Maquila`, `deleted_at`) VALUES
(1, 'PruebaMaquila', '0', '2025-08-24 18:16:28'),
(2, 'Maquila233', '$2y$10$eLQrs6r2lz13y0D0HIoP7OWEyF3xXOpVj31KGKB1qJRzA68/f/3Ly', NULL),
(3, 'MaquilaNueva', '$2y$10$YIb5q1pJyGPr6Pzdj4vSVuZkEBKUUqVYCKqv4RZV8HogKhgSrx82G', NULL),
(4, 'MaqPruebaBn', '$2y$10$QgAWBTfypEwahF.4QtpVXOkJcq1oHkPwtYDvlwXGimU5j41EcV61G', NULL),
(5, 'NuevaMaquila', '$2y$10$T5P8W4hOSnFsz2FN8VPXEOtF07Nw6gfeR4ftlVhTR5MdsLEzam4t6', NULL),
(6, 'M_Prueba1712', '$2y$10$h0.s8G40rbM37MgkPdYHi.9PPji7b4EyZU6LL1cAeWEK2l8XkdUAS', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `maquila_area`
--

CREATE TABLE `maquila_area` (
  `Id_Maquila` int(11) NOT NULL,
  `Id_Area` int(11) NOT NULL,
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `maquila_area`
--

INSERT INTO `maquila_area` (`Id_Maquila`, `Id_Area`, `deleted_at`) VALUES
(2, 1, NULL),
(2, 4, NULL),
(2, 7, NULL),
(2, 9, NULL),
(2, 10, NULL),
(2, 13, NULL),
(3, 2, NULL),
(3, 3, NULL),
(4, 5, NULL),
(4, 6, NULL),
(4, 11, NULL),
(5, 8, NULL),
(6, 12, NULL),
(6, 14, NULL),
(6, 15, NULL);

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
  `JSON_Reporte` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`JSON_Reporte`)),
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `reporte`
--

INSERT INTO `reporte` (`Id_Reporte`, `FechaRegistro_Reporte`, `FechaModificacion_Reporte`, `CARTotal_Reporte`, `CARRevisadas_Reporte`, `Estado_Reporte`, `JSON_Reporte`, `deleted_at`) VALUES
(1, '2025-09-16 04:17:55', 2025, 2, 2, 'COMPLETADO', '{\"area\":{\"area_name\":\"Prueba3\",\"area_description\":\"Esta es la prueba 3 de las areas con varios Car\"},\"car_reports\":[{\"car_name\":\"CAR 1\",\"responses\":{\"Si o No\":true,\"Un numero\":\"1\"},\"observacion\":\"si\"},{\"car_name\":\"CAR 2\",\"responses\":{\"Descripcion\":\"si\"},\"incidencia\":\"no\"}]}', NULL),
(2, '2025-10-06 15:07:28', 2025, 1, 1, 'COMPLETADO', '{\"area\":{\"area_name\":\"Ala Izquiuerda\",\"area_description\":\"El ala Izquierda de la maquila\",\"cars\":[{\"name\":\"Baño\",\"properties\":[{\"label\":\"Hay extintor\",\"type\":\"bool\",\"default\":true}]}]},\"car_reports\":[{\"car_name\":\"Baño\",\"responses\":{\"Hay extintor\":true},\"observacion\":\"hola\"}]}', NULL),
(3, '2025-10-12 13:24:54', 2025, 2, 2, 'COMPLETADO', '{\"area\":{\"area_name\":\"Area de platinado\",\"area_description\":\"Areas\"},\"car_reports\":[{\"car_name\":\"Maquina 1\",\"responses\":{\"Funcionamiento\":true,\"Numero de la valvula\":\"55\"},\"observacion\":\"Todo bien\"},{\"car_name\":\"Maquina 2\",\"responses\":{\"Chequeo\":false},\"incidencia\":\"Algo mal\"}]}', NULL),
(4, '2025-10-12 13:32:07', 2025, 1, 1, 'COMPLETADO', '{\"area\":{\"area_name\":\"Hidrante Platinadora\",\"area_description\":\"asa\"},\"car_reports\":[{\"car_name\":\"Funcionamiento del Hidrante\",\"responses\":{\"Funcionamiento\":false},\"incidencia\":\"no\"}]}', NULL),
(5, '2025-11-15 17:15:56', 2025, 1, 1, 'COMPLETADO', '{\"area\":{\"area_name\":\"Ala Izquiuerda\",\"area_description\":\"El ala Izquierda de la maquila\"},\"car_reports\":[{\"car_name\":\"Baño\",\"responses\":{\"Hay extintor\":true}}]}', '2025-12-13 01:58:07'),
(6, '2025-12-13 17:43:35', 2025, 2, 2, 'COMPLETADO', '{\"area\":{\"area_name\":\"Prueba3\",\"area_description\":\"Esta es la prueba 3 de las areas con varios Car\",\"cars\":[{\"name\":\"CAR 1\",\"properties\":[{\"label\":\"Si o No\",\"type\":\"bool\",\"default\":true},{\"label\":\"Un numero\",\"type\":\"number\",\"min\":1,\"max\":2,\"step\":1}]},{\"name\":\"CAR 2\",\"properties\":[{\"label\":\"Descripcion\",\"type\":\"text\",\"placeholder\":\"\"}]}]},\"car_reports\":[{\"car_name\":\"CAR 1\",\"responses\":{\"Si o No\":true,\"Un numero\":\"20\"},\"observacion\":\"si\"},{\"car_name\":\"CAR 2\",\"responses\":{\"Descripcion\":\"Descripcida\"},\"incidencia\":\"Algo malo\"}]}', NULL),
(7, '2025-12-17 14:45:28', 2025, 1, 1, 'COMPLETADO', '{\"area\":{\"area_name\":\"A_Prueba1217\",\"area_description\":\"Prueba del 17 del 12\",\"cars\":[{\"name\":\"Cosa 1\",\"properties\":[{\"label\":\"Funcionamiento\",\"type\":\"bool\",\"default\":true},{\"label\":\"Numero 1\",\"type\":\"range\",\"min\":0,\"max\":20,\"step\":1}]}]},\"car_reports\":[{\"car_name\":\"Cosa 1\",\"responses\":{\"Funcionamiento\":true,\"Numero 1\":\"20\"},\"observacion\":\"bien\"}]}', NULL),
(8, '2026-02-09 16:58:40', 2026, 1, 1, 'COMPLETADO', '{\"area\":{\"area_name\":\"AreaRapida\",\"area_description\":\"123\",\"cars\":[{\"name\":\"Rapida\",\"properties\":[{\"label\":\"Fun\",\"type\":\"bool\",\"default\":true}]}]},\"car_reports\":[{\"car_name\":\"Rapida\",\"responses\":{\"Fun\":true},\"observacion\":\"si\"}]}', NULL),
(9, '2026-03-04 13:44:56', 2026, 12, 12, 'COMPLETADO', '{\"area\":{\"area_name\":\"Sistema de Hidrantes y Rociadores\",\"area_description\":\"Area detallada\",\"cars\":[{\"name\":\"Bomba Principal\",\"properties\":[{\"label\":\"Estado operativo\",\"type\":\"bool\",\"default\":true},{\"label\":\"PSI\",\"type\":\"number\",\"min\":0,\"max\":150,\"step\":1},{\"label\":\"Nivel Combustible\",\"type\":\"number\",\"min\":0,\"max\":100,\"step\":1}]},{\"name\":\"Bomba Jockey\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Estado Funcionamiento\",\"type\":\"text\",\"placeholder\":\"Ej.Automatico\"},{\"label\":\"Presion Arranque\",\"type\":\"number\",\"min\":0,\"max\":129,\"step\":1}]},{\"name\":\"Valvula de descarga\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Posicion\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Sello\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Hidrante Exterior H-01\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Acceso\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Tapones\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Fugas\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Hidrante Exterior H-02\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Acceso\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Presion Estatica\",\"type\":\"number\",\"min\":0,\"max\":120,\"step\":1}]},{\"name\":\"Gabinete de Manguera G-01\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Manguera\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Llave de paso\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Gabinete de Manguera G-02\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Manguera\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Llave de paso\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Tanque de reserva\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Nivel de agua\",\"type\":\"range\",\"min\":0,\"max\":100,\"step\":1},{\"label\":\"Valvula\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Manómetro de Red\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Lectura\",\"type\":\"number\",\"min\":0,\"max\":200,\"step\":1},{\"label\":\"Calibración\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Sensor de Flujo SF-01\",\"properties\":[{\"label\":\"Señal\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Voltaje\",\"type\":\"number\",\"min\":0,\"max\":30,\"step\":1}]},{\"name\":\"Tuberia\",\"properties\":[{\"label\":\"Pintura\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Soportes\",\"type\":\"text\",\"placeholder\":\"\"},{\"label\":\"Corrosion\",\"type\":\"text\",\"placeholder\":\"\"}]},{\"name\":\"Valvula de succión\",\"properties\":[{\"label\":\"Estado\",\"type\":\"bool\",\"default\":true},{\"label\":\"Posición\",\"type\":\"text\",\"placeholder\":\"Abierta\\/No Abierta\"},{\"label\":\"Existencia de candado\",\"type\":\"text\",\"placeholder\":\"Si\\/No\"}]}]},\"car_reports\":[{\"car_name\":\"Bomba Principal\",\"responses\":{\"Estado operativo\":true,\"PSI\":\"145\",\"Nivel Combustible\":\"60\"},\"observacion\":\"Funcional, combustible casi a la mitad\"},{\"car_name\":\"Bomba Jockey\",\"responses\":{\"Estado\":true,\"Estado Funcionamiento\":\"En Automático \",\"Presion Arranque\":\"120\"},\"observacion\":\"Alta presion\"},{\"car_name\":\"Valvula de descarga\",\"responses\":{\"Estado\":true,\"Posicion\":\"abierta\",\"Sello\":\"presente\"},\"observacion\":\"todo bien\"},{\"car_name\":\"Hidrante Exterior H-01\",\"responses\":{\"Estado\":true,\"Acceso\":\"Con acceso\",\"Tapones\":\"No existentes\",\"Fugas\":\"No existentes\"},\"observacion\":\"Buen estado\"},{\"car_name\":\"Hidrante Exterior H-02\",\"responses\":{\"Estado\":true,\"Acceso\":\"Existente\",\"Presion Estatica\":\"125\"},\"observacion\":\"Alta presión \"},{\"car_name\":\"Gabinete de Manguera G-01\",\"responses\":{\"Estado\":false,\"Manguera\":\"No presente\",\"Llave de paso\":\"Pasada\"},\"incidencia\":\"No está presente la Manguera\"},{\"car_name\":\"Gabinete de Manguera G-02\",\"responses\":{\"Estado\":true,\"Manguera\":\"Presente\",\"Llave de paso\":\"Pasada\"},\"observacion\":\"Todo bien\"},{\"car_name\":\"Tanque de reserva\",\"responses\":{\"Estado\":true,\"Nivel de agua\":\"Alto\",\"Valvula\":\"Buena Condición \"}},{\"car_name\":\"Manómetro de Red\",\"responses\":{\"Estado\":false,\"Lectura\":\"180\",\"Calibración\":\"Poca\"},\"incidencia\":\"Lecturas Inusuales\"},{\"car_name\":\"Sensor de Flujo SF-01\",\"responses\":{\"Señal\":\"Presente\",\"Voltaje\":\"50\"}},{\"car_name\":\"Tuberia\",\"responses\":{\"Pintura\":\"Existente\",\"Soportes\":\"Existentes\",\"Corrosion\":\"No existente\"}},{\"car_name\":\"Valvula de succión\",\"responses\":{\"Estado\":true,\"Posición\":\"Abierta\",\"Existencia de candado\":\"Si\"}}]}', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `Id_Usuario` int(11) NOT NULL,
  `Nombre_Usuario` varchar(50) NOT NULL,
  `Password_Usuario` varchar(66) NOT NULL,
  `Telefono_Usuario` varchar(10) NOT NULL,
  `Puesto_Usuario` varchar(35) NOT NULL,
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`Id_Usuario`, `Nombre_Usuario`, `Password_Usuario`, `Telefono_Usuario`, `Puesto_Usuario`, `deleted_at`) VALUES
(1, 'Yoz', '$2y$10$naZhveR7uw3PNNg//5OFn.bw.LT/FlC3x.5NoVEhhwUKw.DjONHwu', '631111111', 'Administrador', NULL),
(2, 'Prueba', '$2y$10$20z21BTp9ohMitIJKFLaK.TRIKv2uzByjBqyvxH5gVbakIERSIyQS', '121212', 'Puesto2', NULL),
(3, 'PruebaBn', '$2y$10$Jc7GzU32rgNl86rbL3tMj.fk9EkFCy8Tv7Rt2iWC6e2mYWCwAI/9q', '631111111', 'RevisionistaSi', NULL),
(4, 'Santiago', '$2y$10$ONbQusOJQd31vmrqvJKsSOZufq.MljLpmej1Pft2XagZ53WMwYWc6', '121212121', 'Revision', NULL),
(5, 'R_Prueba1217', '$2y$10$DTHjKX//zfczoV67t1Z82OT4pD/heF2OnAS7uCJ7FB21zAlmSQ4mm', '6311121122', 'Reportista', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_reporte`
--

CREATE TABLE `usuario_reporte` (
  `Id_Usuario` int(11) NOT NULL,
  `Id_Reporte` int(11) NOT NULL,
  `FechaRegistro_Reporte` datetime NOT NULL,
  `FechaModificacion_Reporte` datetime DEFAULT NULL,
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario_reporte`
--

INSERT INTO `usuario_reporte` (`Id_Usuario`, `Id_Reporte`, `FechaRegistro_Reporte`, `FechaModificacion_Reporte`, `deleted_at`) VALUES
(2, 1, '2025-09-16 04:17:55', '2025-09-16 04:17:55', NULL),
(2, 8, '2026-02-09 16:58:40', '2026-02-09 16:58:40', NULL),
(3, 2, '2025-10-06 15:07:28', '2025-10-06 15:07:28', NULL),
(3, 3, '2025-10-12 13:24:54', '2025-10-12 13:24:54', NULL),
(3, 5, '2025-11-15 17:15:56', '2025-11-15 17:15:56', NULL),
(3, 6, '2025-12-13 17:43:35', '2025-12-13 17:43:35', NULL),
(4, 4, '2025-10-12 13:32:07', '2025-10-12 13:32:07', NULL),
(4, 9, '2026-03-04 13:44:56', '2026-03-04 13:44:56', NULL),
(5, 7, '2025-12-17 14:45:28', '2025-12-17 14:45:28', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_session`
--

CREATE TABLE `usuario_session` (
  `id` int(11) NOT NULL,
  `Id_Usuario` int(11) NOT NULL,
  `token` varchar(128) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `expires_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario_session`
--

INSERT INTO `usuario_session` (`id`, `Id_Usuario`, `token`, `created_at`, `expires_at`) VALUES
(1, 1, 'abb4aeef4f7c3b8752cedb7e8b3b364d95f0119623b44e13', '2025-09-03 12:09:46', '2025-09-10 21:09:46'),
(2, 1, '600631316bf8438254a1904a99aea08b12387504a18908d2', '2025-09-03 12:21:32', '2025-09-10 21:21:32'),
(3, 1, '9ed2f0a026b3daad7d1ff185ef62cf8da0a49de9b7afea7d', '2025-09-03 14:46:45', '2025-09-10 23:46:45'),
(4, 1, 'b0e304e0b77f614f28d0e5dd69ec1ce3428a457c29e098f8', '2025-09-03 14:48:42', '2025-09-10 23:48:42'),
(5, 1, '82eb1ce739117c4f63ff7ede1f95740dfc1dc329c528c8e2', '2025-09-03 14:59:45', '2025-09-10 23:59:45'),
(6, 1, '6602a497b84fdcc7963a1d190494ecd686334836bccc398b', '2025-09-03 15:02:57', '2025-09-11 00:02:57'),
(7, 2, '8c4577fc6c59a8a46c33ae1320182e0a6bcf172adfb00739', '2025-09-09 11:45:22', '2025-09-16 20:45:22'),
(8, 2, '531d9640bcc2984bd509bcfa4e7b0b4a72c284368f10300f', '2025-09-09 12:19:38', '2025-09-16 21:19:38'),
(9, 2, '5efdad3970094376fef0e18e875e513b0b6a3c0030760eae', '2025-09-15 16:14:49', '2025-09-23 01:14:49'),
(10, 2, '5d97f79767e8e7b7eecb05c9f084ac73a4dade2fa4272a95', '2025-09-15 19:15:43', '2025-09-23 04:15:43'),
(11, 2, 'fd50a3f57c32b19822bf9ac69b3d81a0a30821a05801b223', '2025-09-15 19:21:37', '2025-09-23 04:21:37'),
(12, 2, '32fe8b99e6fe6721fbd78402af8e7f76c990d566888693a9', '2025-09-15 19:28:39', '2025-09-23 04:28:39'),
(13, 2, 'a5de484f21fe308739c24e62a984466bbea237972bdb0bfb', '2025-09-15 19:39:50', '2025-09-23 04:39:50'),
(14, 2, 'ca0fb90e2362d411f94963294ab1d84b313c1c4e8afc21c4', '2025-09-15 19:45:40', '2025-09-23 04:45:40'),
(15, 2, '0be8cd3f351304b4d69b774e7f8580568b36b23e51ddf422', '2025-09-15 20:47:39', '2025-09-23 05:47:39'),
(16, 2, 'ef93cd6b46022c8ec77dd9a67dd5e8c979d0d7b9d4a9c262', '2025-09-15 20:54:24', '2025-09-23 05:54:24'),
(17, 2, 'ec93084dd7b2b6dbaa60004c554b946535646b261c7059ac', '2025-09-16 04:11:19', '2025-09-23 13:11:19'),
(18, 3, '5180635fe34e3dd81b47bff9e7199829ef9c8e34c11effbe', '2025-10-06 15:06:43', '2025-10-14 00:06:43'),
(19, 3, 'fb60bd1a700b7744a6a6bfe687318ebbb0e3280d7c3dbeec', '2025-10-12 13:23:55', '2025-10-19 22:23:55'),
(20, 4, 'e61cd76cb1227bec52ece4f4c3b2499da891ed66be96d4fe', '2025-10-12 13:27:36', '2025-10-19 22:27:36');

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
-- Indices de la tabla `usuario_session`
--
ALTER TABLE `usuario_session`
  ADD PRIMARY KEY (`id`),
  ADD KEY `Id_Usuario` (`Id_Usuario`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `area`
--
ALTER TABLE `area`
  MODIFY `Id_Area` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT de la tabla `maquila`
--
ALTER TABLE `maquila`
  MODIFY `Id_Maquila` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `reporte`
--
ALTER TABLE `reporte`
  MODIFY `Id_Reporte` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `Id_Usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `usuario_session`
--
ALTER TABLE `usuario_session`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

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
