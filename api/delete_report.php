<?php
header('Content-Type: application/json; charset=utf-8');
require_once '../controllers/reporteController.php';

$controller = new ReporteController();
$data = json_decode(file_get_contents('php://input'), true);

$id = $data['Id_Reporte'] ?? $_GET['id'] ?? null;

if (!$id) {
    echo json_encode(['success' => false, 'error' => 'No Id_Reporte provided']);
    exit;
}

$result = $controller->eliminar($id);

if ($result) {
    echo json_encode(['success' => true, 'message' => 'Reporte eliminado']);
} else {
    echo json_encode(['success' => false, 'error' => 'Failed to delete report']);
}
?>