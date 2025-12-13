<?php
header('Content-Type: application/json; charset=utf-8');
require_once '../controllers/reporteController.php';

$controller = new ReporteController();
$data = json_decode(file_get_contents('php://input'), true);

$id = $data['Id_Reporte'] ?? null;

if (!$id) {
    echo json_encode(['success' => false, 'error' => 'No Id_Reporte provided']);
    exit;
}

// Permitir actualizar campos específicos
$allowed = ['JSON_Reporte', 'Estado_Reporte', 'CARTotal_Reporte', 'CARRevisadas_Reporte', 'FechaModificacion_Reporte'];
$updateData = array_intersect_key($data, array_flip($allowed));

if (empty($updateData)) {
    echo json_encode(['success' => false, 'error' => 'No valid fields to update']);
    exit;
}

// Asegurar fecha de modificación
if (!isset($updateData['FechaModificacion_Reporte'])) {
    $updateData['FechaModificacion_Reporte'] = date('Y-m-d H:i:s');
}

$result = $controller->actualizar($id, $updateData);

if ($result) {
    echo json_encode(['success' => true, 'message' => 'Reporte actualizado']);
} else {
    echo json_encode(['success' => false, 'error' => 'Failed to update report']);
}
?>