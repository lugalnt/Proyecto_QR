<?php
require_once('conn.php');

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
                    <form id="register-form" action="" method="post" style="display: none;">
                        <input type="hidden" name="form_type" value="register">
                        <div>
                            <input type="text" name="NombreR" placeholder="Nombre" maxlength="50" required>
                            <input type="password" name="passR" placeholder="Contraseña" maxlength="30" required>
                            <input type="text" name="telefonoR" placeholder="Telefono" maxlength="10" required>
                        </div>
                        <button type="submit" class="button">Registrarse</button>
                    </form>
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

if ($_SERVER["REQUEST_METHOD"] == "POST")
{
    $tipo = $_POST['form_type'];


    if($tipo == "login")
    {

        $nombre = $_POST['NombreL'];
        $contra = $_POST['passL'];
        $queryL = $conn->prepare("SELECT * FROM usuario WHERE Nombre_Usuario = ? AND Contra_Usuario = ?");
        $queryL->bind_param("ss", $nombre, $contra);
        $queryL->execute();
        $resultL = $queryL->get_result();
        if ($resultL->num_rows > 0) {

            session_start();
            $_SESSION['Nombre_Usuario'] = $nombre;
            $_SESSION['Id_Usuario'] = $resultL->fetch_assoc()['Id_Usuario'];
            echo "<script> window.location.href = 'index.php';</script>";
            exit();
        } else {
            echo "<script>alert('Usuario o contraseña incorrectos');</script>";
        }
    }

}


?>