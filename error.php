<?php
$errorCode = $_GET['code'] ?? '404';
$errorTitle = '';
$errorMessage = '';

switch ($errorCode) {
    case '403':
        $errorTitle = 'Acceso Denegado';
        $errorMessage = 'Lo sentimos, no tienes permiso para acceder a esta página.';
        break;
    case '500':
        $errorTitle = 'Error Interno';
        $errorMessage = 'Algo salió mal en nuestros servidores. Estamos trabajando para solucionarlo.';
        break;
    case '404':
    default:
        $errorCode = '404';
        $errorTitle = 'Página No Encontrada';
        $errorMessage = 'La página que estás buscando no existe o ha sido movida.';
        break;
}
?>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error
        <?php echo $errorCode; ?> - FCS
    </title>
    <link rel="stylesheet" href="css/styleLogin.css">
    <style>
        .error-code {
            font-size: 100px;
            font-weight: 800;
            background: var(--primary-gradient);
            -webkit-background-clip: text;
            background-clip: text;
            -webkit-text-fill-color: transparent;
            margin: 0;
            line-height: 1;
        }

        .error-title {
            font-size: 24px;
            margin: 10px 0;
            color: var(--text-dark);
        }

        .error-message {
            color: var(--text-muted);
            margin-bottom: 30px;
        }

        .back-home {
            display: inline-block;
            margin-top: 20px;
            padding: 12px 24px;
            background: var(--primary-gradient);
            color: white;
            text-decoration: none;
            border-radius: 12px;
            font-weight: 600;
            box-shadow: 0 4px 15px rgba(198, 63, 66, 0.3);
            transition: all 0.3s ease;
        }

        .back-home:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(198, 63, 66, 0.4);
            filter: brightness(1.1);
            color: white;
        }

        .container {
            max-width: 500px;
            padding: 50px 30px;
        }

        .fcs-logo-mini {
            height: 40px;
            margin-bottom: 20px;
        }
    </style>
</head>

<body>
    <div class="container">
        <img src="css/logo.png" alt="Logo FCS" class="fcs-logo-mini">
        <h1 class="error-code">
            <?php echo $errorCode; ?>
        </h1>
        <h2 class="error-title">
            <?php echo $errorTitle; ?>
        </h2>
        <p class="error-message">
            <?php echo $errorMessage; ?>
        </p>
        <a href="index.php" class="back-home">Regresar al Inicio</a>

        <div class="help-links">
            <a href="contacto.html">Contacto</a>
            <a href="ayuda.html">Ayuda</a>
        </div>
    </div>
</body>

</html>