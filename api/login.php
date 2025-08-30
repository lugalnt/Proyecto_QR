<?php

header('Content-Type: application/json; charset=utf-8');

// Ajusta rutas según tu proyecto
require_once __DIR__ . '/../controllers/UsuarioController.php'; // ruta al controlador
// Si tu proyecto usa config global y DB::getConnection, asegúrate que config/db.php se carga desde los controladores o agregarlo aquí.
// require_once __DIR__ . '/../config/db.php';

try {
    // Obtener POST params (soporta application/x-www-form-urlencoded desde B4A)
    $nombre_usuario = isset($_POST['nombre_usuario']) ? trim($_POST['nombre_usuario']) : null;
    $password = isset($_POST['password']) ? $_POST['password'] : null;

    if (empty($nombre_usuario) || empty($password)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Nombre de usuario y contraseña requeridos.']);
        exit;
    }

    // Instanciar controlador (ajusta si tu constructor pide argumentos)
    $uc = new UsuarioController();

    // Llamar al nuevo método que devuelve datos y token
    $result = $uc->loginConToken($nombre_usuario, $password);

    // Responder
    echo json_encode($result);
    exit;

} catch (\InvalidArgumentException $e) {
    // Errores esperados de validación / login
    http_response_code(401);
    echo json_encode(['success' => false, 'message' => $e->getMessage()]);
    exit;
} catch (\Throwable $e) {
    // Errores inesperados
    http_response_code(500);
    // En producción no mostrar $e->getMessage() directamente, usar un mensaje genérico y loggear el detalle.
    echo json_encode(['success' => false, 'message' => 'Error en el servidor.']);
    // Opcional: loggear $e->getMessage()
    exit;
}


?>