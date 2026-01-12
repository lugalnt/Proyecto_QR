<?php
session_start();

if (!isset($_SESSION['Id_Usuario'])) {
    header("Location: login.php");
    exit();
}

require_once 'controllers/reporteController.php';
require_once 'utils/ExcelExporter.php';

use Utils\ExcelExporter;

$ReporteController = new ReporteController();

// --- Capturar filtros de la URL (igual que en index.php) ---
$filtros = [];
if (!empty($_GET['maquila']))
    $filtros['id_maquila'] = (int) $_GET['maquila'];
if (!empty($_GET['area']))
    $filtros['id_area'] = (int) $_GET['area'];
if (!empty($_GET['usuario']))
    $filtros['id_usuario'] = (int) $_GET['usuario'];
if (!empty($_GET['estado']))
    $filtros['estado'] = trim($_GET['estado']);
if (!empty($_GET['fecha_inicio']))
    $filtros['fecha_inicio'] = trim($_GET['fecha_inicio']);
if (!empty($_GET['fecha_fin']))
    $filtros['fecha_fin'] = trim($_GET['fecha_fin']);
if (!empty($_GET['id_reporte']))
    $filtros['id_reporte'] = trim($_GET['id_reporte']);

$limit = 5000; // Límite razonable para exportación

try {
    // --- LÓGICA DE EXPORTACIÓN ÚNICA ---
    if (!empty($_GET['id_reporte_unico'])) {
        $idUnico = (int) $_GET['id_reporte_unico'];
        // Usar buscar con el ID específico para obtener todos los joins (Maquila, Area, etc)
        $result = $ReporteController->buscar(['id_reporte' => $idUnico], 1);

        if ($result['success'] && !empty($result['data'])) {
            $reporte = $result['data'][0];
            $filename = 'Reporte_#' . $idUnico . '_' . date('Ymd') . '.xlsx';
            ExcelExporter::exportSingleReport($reporte, $filename);
        } else {
            echo "<script>alert('No se encontró el reporte seleccionado.'); window.history.back();</script>";
        }
        exit;
    }

    // --- LÓGICA DE EXPORTACIÓN MASIVA (POR FILTROS) ---
    $result = $ReporteController->buscar($filtros, $limit);

    if ($result['success'] && !empty($result['data'])) {
        $filename = 'Reportes_' . date('Y-m-d_H-i-s') . '.xlsx';
        ExcelExporter::exportReports($result['data'], $filename);
    } else {
        echo "<script>alert('No hay datos para exportar con los filtros seleccionados.'); window.history.back();</script>";
    }
} catch (\Throwable $e) {
    echo "Error al exportar: " . $e->getMessage();
}
