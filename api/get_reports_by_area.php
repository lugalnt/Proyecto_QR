<?php
// api/get_reports_by_area.php (robusto, agrega JSON_Reporte_parsed)
declare(strict_types=1);
ini_set('display_errors', '0');
error_reporting(0);

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

try {
    // Ajuste: ruta al controller
    require_once __DIR__ . '/../controllers/reporteController.php';

    $areaParam = null;
    if (isset($_GET['area']) && $_GET['area'] !== '')
        $areaParam = trim((string) $_GET['area']);
    if (($areaParam === null || $areaParam === '') && isset($_POST['area']))
        $areaParam = trim((string) $_POST['area']);

    $limit = 100;
    if (isset($_GET['limit']))
        $limit = max(1, (int) $_GET['limit']);
    if (isset($_POST['limit']))
        $limit = max(1, (int) $_POST['limit']);

    if ($areaParam === null || $areaParam === '') {
        echo json_encode(['success' => false, 'error' => 'Missing parameter: area']);
        exit;
    }

    // --- SEGURIDAD: CHECKEO DE MAQUILA (si viene el parámetro) ---
    // El usuario pidió: "pon un simple checkeo"
    $idMaquilaSeguridad = null;
    if (isset($_GET['id_maquila']))
        $idMaquilaSeguridad = (int) $_GET['id_maquila'];
    elseif (isset($_POST['id_maquila']))
        $idMaquilaSeguridad = (int) $_POST['id_maquila'];

    if ($idMaquilaSeguridad) {
        require_once __DIR__ . '/../controllers/maquilaareaController.php';
        $maControl = new MaquilaAreaController();
        // Verificamos si esta area pertenece a la maquila que solicita
        $esValido = $maControl->verificarRelacion($idMaquilaSeguridad, (int) $areaParam);

        if (!$esValido) {
            // No autorizada
            echo json_encode(['success' => false, 'error' => 'Esta área no pertenece a su maquila.']);
            exit;
        }
    }
    // -------------------------------------------------------------

    $reporteCtrl = new ReporteController();
    $result = $reporteCtrl->getByArea($areaParam, $limit);

    // Si la estructura es la esperada y trae 'data', agregamos JSON_Reporte_parsed para cada fila
    if (is_array($result) && isset($result['data']) && is_array($result['data'])) {
        foreach ($result['data'] as $i => $row) {
            // proteger si JSON_Reporte existe y es string
            if (isset($row['JSON_Reporte']) && is_string($row['JSON_Reporte']) && $row['JSON_Reporte'] !== '') {
                $decoded = json_decode($row['JSON_Reporte'], true);
                if (json_last_error() === JSON_ERROR_NONE) {
                    $result['data'][$i]['JSON_Reporte_parsed'] = $decoded;
                } else {
                    // Si falla el parseo, dejar null (no interrumpir)
                    $result['data'][$i]['JSON_Reporte_parsed'] = null;
                }
            } else {
                $result['data'][$i]['JSON_Reporte_parsed'] = null;
            }
        }
    }

    // Forzar JSON_UNESCAPED_UNICODE (legible) y evitar errores de codificación
    echo json_encode($result, JSON_UNESCAPED_UNICODE);
    exit;

} catch (\Throwable $e) {
    http_response_code(500);
    // NO imprimir $e->getTrace() ni debug en producción
    echo json_encode(['success' => false, 'error' => $e->getMessage()]);
    exit;
}
