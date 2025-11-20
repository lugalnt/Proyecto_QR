<?php
// api/get_reports_by_area.php
// Devuelve JSON: { success: true, data: [ ... ] }
// Parámetros: GET|POST 'area' (id numérico o código de área), opcional 'limit' (int)

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

try {
    // Ajusta la ruta si tu carpeta controllers está en otro lugar
    require_once __DIR__ . '/../controllers/reporteController.php';

    $areaParam = null;
    if (isset($_GET['area']) && $_GET['area'] !== '') $areaParam = trim($_GET['area']);
    if ((empty($areaParam) || $areaParam === '') && isset($_POST['area'])) $areaParam = trim($_POST['area']);

    $limit = 100;
    if (isset($_GET['limit'])) $limit = max(1, (int)$_GET['limit']);
    if (isset($_POST['limit'])) $limit = max(1, (int)$_POST['limit']);

    if ($areaParam === null || $areaParam === '') {
        echo json_encode(['success' => false, 'error' => 'Missing parameter: area']);
        exit;
    }

    $reporteCtrl = new ReporteController();

    // El controller getByArea acepta tanto Id_Area (numérico) como Codigo_Area (string)
    $result = $reporteCtrl->getByArea($areaParam, $limit);

    // Asegurar un formato mínimo de salida
    if (!is_array($result)) {
        echo json_encode(['success' => false, 'error' => 'Controller returned unexpected result']);
        exit;
    }

    echo json_encode($result);
    exit;

} catch (\Throwable $e) {
    http_response_code(500);
    echo json_encode(['success' => false, 'error' => $e->getMessage()]);
    exit;
}
