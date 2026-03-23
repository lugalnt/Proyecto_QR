<?php
session_start();
header('Content-Type: application/json; charset=utf-8');

// Cargar dependencias de Composer (PHPMailer)
require_once __DIR__ . '/../vendor/autoload.php';
require_once __DIR__ . '/../config/env.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

// Rate limiting preventivo (1 mensaje cada minuto)
$tiempo_espera = 60; // Segundos
if (isset($_SESSION['last_recovery_time'])) {
    $tiempo_transcurrido = time() - $_SESSION['last_recovery_time'];
    if ($tiempo_transcurrido < $tiempo_espera) {
        $faltan = $tiempo_espera - $tiempo_transcurrido;
        http_response_code(429);
        echo json_encode(['success' => false, 'message' => "Por favor espera {$faltan} segundos antes de solicitar otra vez."]);
        exit;
    }
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['success' => false, 'message' => 'Método no permitido.']);
    exit;
}

$typeAcc = isset($_POST['typeAcc']) ? trim($_POST['typeAcc']) : '';
$nombre = isset($_POST['Nombre']) ? trim($_POST['Nombre']) : '';

if (empty($typeAcc) || empty($nombre)) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Falta información requerida.']);
    exit;
}

$emailDestino = null;
$userId = null;
$controller = null;
$record = null; // Store full record
$passwordField = '';

// Buscar usuario o maquila
if ($typeAcc === 'usuario') {
    require_once __DIR__ . '/../controllers/usuarioController.php';
    $controller = new UsuarioController();
    $resultados = $controller->obtenerPor('Nombre_Usuario', $nombre);
    
    if ($resultados && count($resultados) > 0) {
        $record = $resultados[0];
        $userId = $record['Id_Usuario'];
        $emailDestino = $record['Email_Usuario'] ?? null;
        $passwordField = 'Password_Usuario';
    }
} elseif ($typeAcc === 'maquila') {
    require_once __DIR__ . '/../controllers/maquilaController.php';
    $controller = new MaquilaController();
    $resultados = $controller->obtenerPor('Nombre_Maquila', $nombre);
    
    if ($resultados && count($resultados) > 0) {
        $record = $resultados[0];
        $userId = $record['Id_Maquila'];
        $emailDestino = $record['Email_Maquila'] ?? null;
        $passwordField = 'Contraseña_Maquila';
    }
} else {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Tipo de cuenta inválido.']);
    exit;
}

if (!$userId) {
    echo json_encode(['success' => false, 'message' => 'No se encontró una cuenta con ese nombre.']);
    exit;
}

if (empty($emailDestino)) {
    echo json_encode(['success' => false, 'message' => 'La cuenta no tiene un correo registrado. Contacta con el administrador.']);
    exit;
}

// Generar nueva contraseña temporal
$tempPass = bin2hex(random_bytes(4)) . 'A1!'; // Ejemplo: 4a3b2c1dA1! para cumplir regex

// Actualizar en BD (agregando la nueva contraseña al record original)
try {
    $record[$passwordField] = $tempPass;
    $controller->actualizar($userId, $record);
} catch (\Exception $e) {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Error al actualizar contraseña.']);
    exit;
}

// Enviar correo con PHPMailer
$mail = new PHPMailer(true);

try {
    $mail->isSMTP();
    $mail->Host       = getenv('SMTP_HOST') ?: 'smtp.hostinger.com';
    $mail->SMTPAuth   = true;
    $mail->Username   = getenv('SMTP_USER') ?: 'fcscontactoysoporte@fcscontrolpanel.vaqui.net'; 
    $mail->Password   = getenv('SMTP_PASS') ?: '';
    
    if(empty($mail->Password)){
        throw new Exception("Configuración SMTP incompleta en el servidor interno.");
    }

    $mail->SMTPSecure = PHPMailer::ENCRYPTION_SMTPS;
    $mail->Port       = getenv('SMTP_PORT') ?: 465;
    $mail->CharSet    = 'UTF-8';

    // Remitente y Destinatario
    $mail->setFrom($mail->Username, 'Soporte FCS');
    $mail->addAddress($emailDestino, $nombre);

    $mail->isHTML(true);
    $mail->Subject = 'Recuperación de contraseña FCS';
    $mail->Body    = "
        <h2>Hola, $nombre</h2>
        <p>Has solicitado recuperar tu contraseña en <strong>FCS Control Panel</strong>.</p>
        <p>Tu nueva contraseña temporal es:</p>
        <h3 style='background:#f4f4f4; padding:10px; display:inline-block;'>$tempPass</h3>
        <p>Por favor, usa esta contraseña para iniciar sesión. Te recomendamos cambiarla lo antes posible desde tu panel o solicitar al administrador que lo haga.</p>
        <br>
        <p>Si no solicitaste este cambio, por favor ignora este correo.</p>
    ";

    $mail->send();
    
    $_SESSION['last_recovery_time'] = time();
    echo json_encode(['success' => true, 'message' => 'Se ha enviado una nueva contraseña a tu correo.']);

} catch (Exception $e) {
    error_log("Error al enviar email recuperación: {$mail->ErrorInfo}");
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'No se pudo enviar el correo.']);
}
