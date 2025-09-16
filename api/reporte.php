<?php
header('Content-Type: application/json; charset=utf-8');

// Ajusta rutas según tu proyecto
require_once __DIR__ . '/../controllers/ReporteController.php';
// require_once __DIR__ . '/../config/db.php';

try {
    // 1) Obtener payload: priorizar php://input si parece JSON,
    // y manejar el caso donde $_POST contiene una sola clave que es el JSON bruto.
    $payload = null;

    $raw = file_get_contents('php://input');
    $rawTrim = ($raw !== false) ? trim($raw) : '';

    // Si raw parece JSON, intentar decodificarlo primero (PRIORIDAD).
    if ($rawTrim !== '' && ($rawTrim[0] === '{' || $rawTrim[0] === '[')) {
        $decoded = json_decode($rawTrim, true);
        if (json_last_error() === JSON_ERROR_NONE && is_array($decoded)) {
            $payload = $decoded;
        } else {
            // raw no es JSON válido: intentamos parse_str como fallback más abajo
            $payload = null;
        }
    }

    // Si no obtuvimos payload válido del raw, revisar $_POST
    if ($payload === null) {
        if (!empty($_POST)) {
            // Caso especial: a veces PHP pone todo el JSON como *una clave* en $_POST.
            // Si $_POST tiene una sola clave que empieza con { o [, intentamos decodificarla.
            if (count($_POST) === 1) {
                $keys = array_keys($_POST);
                $firstKey = $keys[0];
                if (is_string($firstKey) && strlen($firstKey) > 0 && ($firstKey[0] === '{' || $firstKey[0] === '[')) {
                    $maybe = json_decode($firstKey, true);
                    if (json_last_error() === JSON_ERROR_NONE && is_array($maybe)) {
                        $payload = $maybe;
                    } else {
                        // si no pudo decodificar, pero puede que el *value* tenga el json
                        $firstVal = $_POST[$firstKey];
                        if (is_string($firstVal)) {
                            $maybe2 = json_decode($firstVal, true);
                            if (json_last_error() === JSON_ERROR_NONE && is_array($maybe2)) {
                                $payload = $maybe2;
                            } else {
                                $payload = $_POST; // dejar tal cual
                            }
                        } else {
                            $payload = $_POST;
                        }
                    }
                } else {
                    // caso normal con varias claves o una clave normal
                    $payload = $_POST;
                }
            } else {
                // $_POST con varias claves (form-urlencoded normal)
                $payload = $_POST;
            }
        } else {
            // $_POST vacío y raw no era JSON válido — intentar parsear raw como query string
            if ($rawTrim !== '') {
                parse_str($rawTrim, $parsed);
                if (!empty($parsed)) {
                    $payload = $parsed;
                } else {
                    http_response_code(400);
                    echo json_encode(['success' => false, 'error' => 'Payload inválido: no es JSON ni form-urlencoded.']);
                    exit;
                }
            } else {
                http_response_code(400);
                echo json_encode(['success' => false, 'error' => 'No se recibió payload.']);
                exit;
            }
        }
    }

    // Ahora $payload debe ser un array (o algo que convertiremos a array)
    // ------------------------------
    // Normalizar payload (closure local)
    // - convierte stdClass -> array
    // - decodifica strings JSON salvo cuando la clave actual sea 'JSON_Reporte'
    // - protección por profundidad
    // ------------------------------
    $normalize = function($value, $key = null, $depth = 0) use (&$normalize) {
        $MAX_DEPTH = 10;
        if ($depth > $MAX_DEPTH) {
            return $value;
        }

        if ($value instanceof \stdClass) {
            $value = (array)$value;
        }

        if (is_array($value)) {
            $new = [];
            foreach ($value as $k => $v) {
                $new[$k] = $normalize($v, $k, $depth + 1);
            }
            return $new;
        }

        if (is_string($value)) {
            $trim = ltrim($value);
            if ($trim !== '' && ($trim[0] === '{' || $trim[0] === '[')) {
                if ($key === 'JSON_Reporte') {
                    // no decodificamos JSON_Reporte aquí (lo preservamos como string)
                    return $value;
                }
                $decoded = json_decode($value, true);
                if (json_last_error() === JSON_ERROR_NONE) {
                    return $normalize($decoded, $key, $depth + 1);
                }
            }
        }

        return $value;
    };

    $payload = $normalize($payload);

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

    // 4) Responder con lo que devuelva el controller
    if (is_array($result) && isset($result['success']) && $result['success'] === true) {
        http_response_code(200);
        echo json_encode($result);
        exit;
    } else {
        http_response_code(400);
        echo json_encode($result);
        exit;
    }

} catch (\InvalidArgumentException $e) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => $e->getMessage()]);
    exit;
} catch (\Throwable $e) {
    http_response_code(500);
    echo json_encode(['success' => false, 'error' => 'Error interno del servidor', 'detail' => $e->getMessage()]);
    exit;
}
?>
