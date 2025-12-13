<?php
header('Content-Type: application/json; charset=utf-8');
require_once '../controllers/reporteController.php';

$controller = new ReporteController();
$data = json_decode(file_get_contents('php://input'), true);

$id = $_GET['user'] ?? $data['user'] ?? null;
$limit = $_GET['limit'] ?? $data['limit'] ?? 10;

if (!$id) {
    echo json_encode(['success' => false, 'error' => 'No user ID provided']);
    exit;
}

$result = $controller->getByUsuario($id, $limit);
echo json_encode($result);
?>