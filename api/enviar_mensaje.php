<?php
session_start();
header('Content-Type: application/json; charset=utf-8');

// Cargar dependencias de Composer (PHPMailer)
require_once __DIR__ . '/../vendor/autoload.php';

// Variables de entorno o configuración de base de datos
// Dependiendo de dónde guardes (DB_HOST, SMTP_USER, etc.), ajusta este require.
require_once __DIR__ . '/../config/env.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

// 1. RATE LIMITING BÁSICO (1 mensaje cada 2 minutos por sesión)
$tiempo_espera = 120; // Segundos
if (isset($_SESSION['last_message_time'])) {
    $tiempo_transcurrido = time() - $_SESSION['last_message_time'];
    if ($tiempo_transcurrido < $tiempo_espera) {
        $faltan = $tiempo_espera - $tiempo_transcurrido;
        http_response_code(429); // Too Many Requests
        echo json_encode(['success' => false, 'message' => "Por favor espera {$faltan} segundos antes de enviar otro mensaje."]);
        exit;
    }
}

// 2. VALIDACIÓN DEL MÉTODO POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['success' => false, 'message' => 'Método no permitido.']);
    exit;
}

// 3. HONEYPOT (Protección contra Spam)
$honeypot = isset($_POST['website_url']) ? trim($_POST['website_url']) : '';
if (!empty($honeypot)) {
    // Si el honeypot está lleno, es un bot. Simulamos éxito pero no enviamos nada.
    http_response_code(200);
    echo json_encode(['success' => true, 'message' => 'Mensaje enviado correctamente.']);
    exit;
}

// 4. SANITIZAR Y VALIDAR INPUTS
$nombre = isset($_POST['nombre']) ? htmlspecialchars(trim($_POST['nombre']), ENT_QUOTES, 'UTF-8') : '';
$email = isset($_POST['email']) ? filter_var(trim($_POST['email']), FILTER_SANITIZE_EMAIL) : '';
$mensaje = isset($_POST['mensaje']) ? htmlspecialchars(trim($_POST['mensaje']), ENT_QUOTES, 'UTF-8') : '';

if (empty($nombre) || empty($email) || empty($mensaje)) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Todos los campos visibles son obligatorios.']);
    exit;
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'El correo electrónico no es válido.']);
    exit;
}

// 5. ENVÍO DE EMAIL CON PHPMAILER
$mail = new PHPMailer(true);

try {
    // Configuración del Servidor SMTP (Usando variables de entorno o valores quemados seguros)
    $mail->isSMTP();
    $mail->Host       = getenv('SMTP_HOST') ?: 'smtp.hostinger.com'; // Ej: smtp.hostinger.com
    $mail->SMTPAuth   = true;
    
    // Obtenemos el correo empresarial desde una variable de entorno, si existe, o ponemos la que nos indicó el usuario
    // Recomendación: Colocar estas credenciales reales en config/env.php
    $mail->Username   = getenv('SMTP_USER') ?: 'fcscontactoysoporte@fcscontrolpanel.vaqui.net'; 
    $mail->Password   = getenv('SMTP_PASS') ?: ''; // ¡NO PONER CONTRASEÑAS AQUÍ! USAR ENV
    
    // Si no hay password en ENV, fallará. Es vital configurar el SMTP_PASS en el servidor de Hostinger.
    if(empty($mail->Password)){
        throw new Exception("Configuración SMTP incompleta en el servidor interno.");
    }

    $mail->SMTPSecure = PHPMailer::ENCRYPTION_SMTPS; // O ENCRYPTION_STARTTLS (puerto 587)
    $mail->Port       = getenv('SMTP_PORT') ?: 465;

    // Remitente y Destinatario
    $mail->setFrom($mail->Username, 'Soporte Web - ' . $nombre); // Se envía DESDE la cuenta Hostinger
    $mail->addAddress('fcscontactoysoporte@fcscontrolpanel.vaqui.net', 'Equipo de Soporte'); // A quién le llega
    
    // Opcional: Para poder "Responder al remitente real" desde nuestro cliente de correo
    $mail->addReplyTo($email, $nombre);

    // Contenido del Correo
    $mail->isHTML(false); // Texto plano para evitar inyecciones HTML en el body del correo
    $mail->Subject = "Nuevo mensaje de chat desde la web: $nombre";
    $mail->Body    = "Has recibido un nuevo mensaje desde el bot\xF3n de chat flotante de la p\xE1gina de contacto.\n\n" .
                     "Detalles del remitente:\n" .
                     "Nombre: $nombre\n" .
                     "Email: $email\n\n" .
                     "Mensaje:\n" .
                     $mensaje;

    // Enviar
    $mail->send();
    
    // Si se envía con éxito, actualizamos el tiempo del último mensaje en la sesión
    $_SESSION['last_message_time'] = time();

    http_response_code(200);
    echo json_encode(['success' => true, 'message' => 'Mensaje enviado correctamente. Nos pondremos en contacto pronto.']);

} catch (Exception $e) {
    // Loguear el error internamente (opcional)
    error_log("Error al enviar email: {$mail->ErrorInfo}");
    
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Ocurrió un error al enviar el mensaje. Inténtalo más tarde.']);
}
