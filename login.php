<?php

session_start();
?>

<html>

<head>
    <title>FCS - Login</title>
    <link rel="stylesheet" href="css/styleLogin.css">
</head>

<body>
    <div class="container">
        <input type="checkbox" id="check" style="display: none;">
        <div class="form">
            <header>
                <h1 id="form-title">Iniciar Sesión</h1>
            </header>

            <!-- Formulario de Login -->
            <form id="login-form" action="" method="post" style="display: block;">
                <input type="hidden" name="form_type" value="login">
                <div>
                    <input type="text" name="NombreL" placeholder="Nombre" maxlength="50" required>
                    <input type="password" name="passL" placeholder="Contraseña" maxlength="30" required>
                </div>
                <button type="submit" class="button">Iniciar Sesión</button>
            </form>

            <!-- Formulario de Registro -->
        </div>
    </div>

    <script>
        function toggleForm() {
            const loginForm = document.getElementById('login-form');
            const registerForm = document.getElementById('register-form');
            const formTitle = document.getElementById('form-title');
            const toggleText = document.getElementById('toggle-text');
            const toggleLabel = document.querySelector('label[for="check"]');

            if (loginForm.style.display === 'block') {
                loginForm.style.display = 'none';
                registerForm.style.display = 'block';
                formTitle.textContent = 'Registro';
                toggleText.innerHTML = '<h2>¿Ya Tiene Cuenta?</h2>';
                toggleLabel.textContent = 'Ingresar';
            } else {
                loginForm.style.display = 'block';
                registerForm.style.display = 'none';
                formTitle.textContent = 'Iniciar Sesión';
                toggleText.innerHTML = '<h2>¿No Tienes Cuenta?</h2>';
                toggleLabel.textContent = 'Registrate';
            }
        }
    </script>

</body>

</html>

<?php

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    require_once('controllers/usuarioController.php');

    $nombre = $_POST['NombreL'];
    $contra = $_POST['passL'];

    $UsuarioController = new UsuarioController();

    try {
        $login = $UsuarioController->login($nombre, $contra);
        $_SESSION['Id_Usuario'] = $login;
        $_SESSION['Nombre_Usuario'] = $nombre;

        // Obtener datos completos para rol
        $datosUsuario = $UsuarioController->obtenerPor('Id_Usuario', $login);
        if ($datosUsuario && isset($datosUsuario[0])) {
            $_SESSION['Puesto_Usuario'] = $datosUsuario[0]['Puesto_Usuario'] ?? 'Usuario';
        } else {
            $_SESSION['Puesto_Usuario'] = 'Usuario';
        }

        echo "<script> window.location.href = 'index.php';</script>";
    } catch (Exception $e) {
        // Intentar con Maquila
        try {
            require_once('controllers/maquilaController.php');
            $MaquilaController = new MaquilaController();
            $idMaquila = $MaquilaController->login($nombre, $contra);

            $_SESSION['Id_Maquila'] = $idMaquila;
            $_SESSION['Nombre_Usuario'] = $nombre; // Para compatibilidad visual en header

            echo "<script> window.location.href = 'indexMaquila.php';</script>";
        } catch (Exception $eMaquila) {
            // Si ambos fallan, mostrar error genérico o específico
            echo '<script>alert("Credenciales incorrectas")</script>';
        }
    }
}

?>