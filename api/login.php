<?php
header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/../controllers/UsuarioController.php';
require_once __DIR__ . '/../controllers/maquilaController.php';

try {
    // Params de POST
    $nombre_usuario = isset($_POST['nombre_usuario']) ? trim($_POST['nombre_usuario']) : null;
    $password = isset($_POST['password']) ? $_POST['password'] : null;

    if (empty($nombre_usuario) || empty($password)) {
        http_response_code(400);
        echo json_encode([
            'success' => false,
            'message' => 'Nombre de usuario y contraseña requeridos.'
        ]);
        exit;
    }

    /*
    |--------------------------------------------------------------------------
    | 1) INTENTAR LOGIN COMO USUARIO NORMAL
    |--------------------------------------------------------------------------
    */
    $usuarioController = new UsuarioController();
    $usuarioId = null;

    try {
        // login() EXISTE en UsuarioController
        $usuarioId = $usuarioController->login($nombre_usuario, $password);
    } catch (\Throwable $e) {
        // ignoramos aquí, pasamos a maquila
        $usuarioId = null;
    }

    if ($usuarioId !== null) {
        // obtener datos del usuario (obtenerPor SÍ existe)
        $usuarios = $usuarioController->obtenerPor('Id_Usuario', $usuarioId);

        if (!$usuarios || count($usuarios) === 0) {
            throw new \RuntimeException('Error interno: usuario no encontrado después del login.');
        }

        $usuario = $usuarios[0];

        $token = bin2hex(random_bytes(16));

        echo json_encode([
            'success' => true,
            'data' => [
                'role' => 'usuario',
                'token' => $token,
                'Id_Usuario' => (int)$usuario['Id_Usuario'],
                'Nombre_Usuario' => $usuario['Nombre_Usuario'],
                'Puesto_Usuario' => $usuario['Puesto_Usuario'] ?? null,
                'Telefono_Usuario' => $usuario['Telefono_Usuario'] ?? null
            ]
        ]);
        exit;
    }



    /*
    |--------------------------------------------------------------------------
    | 2) INTENTAR LOGIN COMO MAQUILA
    |--------------------------------------------------------------------------
    */

    $maquilaController = new MaquilaController();
    $maquilaId = null;

    try {
        // login() EXISTE en MaquilaController
        $maquilaId = $maquilaController->login($nombre_usuario, $password);
    } catch (\Throwable $e) {
        $maquilaId = null;
    }

    if ($maquilaId !== null) {
        // Obtener datos reales de maquila
        $maquilas = $maquilaController->obtenerPor('Id_Maquila', $maquilaId);

        if (!$maquilas || count($maquilas) === 0) {
            throw new \RuntimeException('Error interno: maquila no encontrada después del login.');
        }

        $maquila = $maquilas[0];

        $token = bin2hex(random_bytes(16));

        echo json_encode([
            'success' => true,
            'data' => [
                'role' => 'maquila',
                'token' => $token,
                'Id_Maquila' => (int)$maquila['Id_Maquila'],
                'Nombre_Maquila' => $maquila['Nombre_Maquila']
            ]
        ]);
        exit;
    }



    /*
    |--------------------------------------------------------------------------
    | 3) SI NO ES NI USUARIO NI MAQUILA
    |--------------------------------------------------------------------------
    */
    http_response_code(401);
    echo json_encode([
        'success' => false,
        'message' => 'Credenciales incorrectas o usuario no registrado.'
    ]);
    exit;


} catch (\InvalidArgumentException $e) {
    http_response_code(401);
    echo json_encode(['success' => false, 'message' => $e->getMessage()]);
    exit;

} catch (\Throwable $e) {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Error en el servidor.']);
    exit;
}
