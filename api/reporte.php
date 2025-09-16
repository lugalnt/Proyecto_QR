<?php
header('Content-Type: application/json; charset=utf-8');

// Ajusta rutas según tu proyecto
require_once __DIR__ . '/../controllers/ReporteController.php';
// Si usas un archivo de configuración / conexión global, descomenta la siguiente línea
// require_once __DIR__ . '/../config/db.php';

try {
    // 1) Obtener payload:
    // Soportamos application/json (php://input) y application/x-www-form-urlencoded (B4A PostString)
    $payload = null;

    // Si hay datos en $_POST (form-urlencoded) los usamos directamente
    if (!empty($_POST)) {
        // convertimos a array asociativo
        $payload = $_POST;
    } else {
        // Intentar leer raw input (JSON)
        $raw = file_get_contents('php://input');
        if ($raw !== false && trim($raw) !== '') {
            // Intentar decodificar JSON
            $decoded = json_decode($raw, true);
            if (json_last_error() === JSON_ERROR_NONE && is_array($decoded)) {
                $payload = $decoded;
            } else {
                // Si no es JSON válido, intentar parsear como query string (por si B4A envía body sin content-type correcto)
                parse_str($raw, $parsed);
                if (!empty($parsed)) {
                    $payload = $parsed;
                } else {
                    // No se pudo interpretar el body
                    http_response_code(400);
                    echo json_encode(['success' => false, 'error' => 'Payload inválido: no es JSON ni form-urlencoded.']);
                    exit;
                }
            }
        } else {
            http_response_code(400);
            echo json_encode(['success' => false, 'error' => 'No se recibió payload.']);
            exit;
        }
    }

    // 2) Validación mínima: asegurarnos que venga algo (el controller hará validaciones más finas)
    if (!is_array($payload) || count($payload) === 0) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Payload vacío o inválido.']);
        exit;
    }

    // 3) Instanciar el controller y delegar
    $rc = new ReporteController();

    // Nota: ReporteController->registrar espera array (no objeto)
    $result = $rc->registrar($payload);

    // 4) Responder con lo que devuelva el controller (asumimos que ya es un array con 'success' y otros datos)
    if (is_array($result) && isset($result['success']) && $result['success'] === true) {
        http_response_code(200);
        echo json_encode($result);
        exit;
    } else {
        // si el controller devolvió info de fallo, responder 400 con esa info
        http_response_code(400);
        echo json_encode($result);
        exit;
    }

} catch (\InvalidArgumentException $e) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => $e->getMessage()]);
    exit;
} catch (\Throwable $e) {
    // En producción evita mostrar $e->getMessage() al cliente; aquí lo incluyo para debugging mínimo.
    http_response_code(500);
    echo json_encode(['success' => false, 'error' => 'Error interno del servidor', 'detail' => $e->getMessage()]);
    // Opcional: logea $e->getMessage() en tus logs.
    exit;
}
?>
