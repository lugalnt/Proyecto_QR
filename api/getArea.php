<?php
header('Content-Type: application/json; charset=utf-8');

// Ajusta la ruta si tu estructura es distinta:
require_once __DIR__ . '/../controllers/areaController.php';

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        http_response_code(405);
        echo json_encode(['success' => false, 'message' => 'Método inválido']);
        exit;
    }

    $codigo = isset($_POST['codigo']) ? trim($_POST['codigo']) : null;
    if (!$codigo) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Código no proporcionado']);
        exit;
    }

    $ctrl = new AreaController();

    // usar obtenerPor con el campo real de la tabla
    $result = $ctrl->obtenerPor('Codigo_Area', $codigo);

    // $result puede ser array vacío o false si no existe
    if ($result && is_array($result) && count($result) > 0) {
        // tomar el primer registro (obtenerPor devuelve lista de filas)
        $area = $result[0];

        // Asegurarse de no enviar campos sensibles
        // Opcional: limpiar / mapear campos si quieres nombres distintos
        echo json_encode([
            'success' => true,
            'data' => $area
        ]);
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Área no encontrada'
        ]);
    }
    exit;
} catch (Throwable $t) {
    // En producción no devolver $t->getMessage() directamente; se devuelve para debug.
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Error en servidor: ' . $t->getMessage()]);
    exit;
}
