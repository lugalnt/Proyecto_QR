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
                    <input type="text" name="NombreL" placeholder="Nombre" maxlength="50" required oninput="this.value = this.value.replace(/[0-9]/g, '')">
                    <input type="password" name="passL" placeholder="Contraseña" maxlength="30" required>
                </div>
                <button type="submit" class="button">Iniciar Sesión</button>
            </form>

            <!-- Formulario de Registro -->
            <form id="register-form" action="" method="post" style="display: none;" onsubmit="validateRegisterForm(event)">
                <input type="hidden" name="form_type" value="register">
                <div>
                    <select id="typeAcc" name="typeAcc" onchange="toggleUserFields()" style="margin-bottom: 15px; padding: 10px; width: 100%;">
                        <option value="usuario">Usuario</option>
                        <option value="maquila">Maquila</option>
                    </select>
                    
                    <input type="text" id="reg-name" name="NombreR" placeholder="Nombre Usuario" maxlength="50" required oninput="this.value = this.value.replace(/[0-9]/g, '')">
                    <input type="password" id="reg-pass" name="passR" placeholder="Contraseña" maxlength="30" required>
                    
                    <div id="user-fields">
                        <input type="text" id="reg-phone" name="TeleR" placeholder="Teléfono" maxlength="10" required>
                        <input type="text" id="reg-puesto" name="PuestoR" placeholder="Puesto" maxlength="35" required>
                    </div>
                </div>
                <button type="submit" class="button">Registrarse</button>
            </form>

            <div class="help-links">
                <a href="contacto.html">Contacto</a>
                <a href="ayuda.html">Ayuda</a>
            </div>
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

        function validateRegisterForm(event) {
            const typeAcc = document.getElementById('typeAcc').value;
            const name = document.getElementById('reg-name').value;
            const pass = document.getElementById('reg-pass').value;
            
            // Regex Común: Al menos 8 caracteres, 1 mayúscula, 1 minúscula, 1 número
            const passRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d@$!%*?&]{8,}$/;
            
            // Regex Nombre: No números (solo para usuarios)
            const nameNoNumbers = /^[^0-9]*$/;

            if (typeAcc === 'usuario') {
                if (!nameNoNumbers.test(name)) {
                    alert('El nombre de usuario no puede contener números.');
                    event.preventDefault();
                    return false;
                }
                const phone = document.getElementById('reg-phone').value;
                 // Validación simple de teléfono (solo números, longitud exacta o minima)
                 if (phone.length > 10) {
                     alert('El teléfono no puede tener más de 10 caracteres.');
                     event.preventDefault();
                     return false;
                 }
            }

            if (!passRegex.test(pass)) {
                alert('La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.');
                event.preventDefault();
                return false;
            }

            return true;
        }

        function toggleUserFields() {
            const type = document.getElementById('typeAcc').value;
            const userFields = document.getElementById('user-fields');
            const regName = document.getElementById('reg-name');

            if (type === 'usuario') {
                userFields.style.display = 'block';
                regName.placeholder = 'Nombre Usuario';
                document.getElementById('reg-phone').required = true;
                document.getElementById('reg-puesto').required = true;
            } else {
                userFields.style.display = 'none';
                regName.placeholder = 'Nombre Maquila';
                document.getElementById('reg-phone').required = false;
                document.getElementById('reg-puesto').required = false;
            }
        }
    </script>

</body>

</html>

<?php

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    
    $formType = $_POST['form_type'] ?? 'login';

    if ($formType === 'login') {
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
    } elseif ($formType === 'register') {
        $typeAcc = $_POST['typeAcc'];
        $nombre = $_POST['NombreR'];
        $contra = $_POST['passR'];

        try {
            if ($typeAcc === 'usuario') {
                require_once('controllers/usuarioController.php');
                $UsuarioController = new UsuarioController();
                
                $telefono = $_POST['TeleR'];
                $puesto = $_POST['PuestoR'];

                $UsuarioController->registrar([
                    'Nombre_Usuario' => $nombre,
                    'Password_Usuario' => $contra,
                    'Telefono_Usuario' => $telefono,
                    'Puesto_Usuario' => $puesto
                ]);
                echo '<script>alert("Usuario registrado con éxito. Inicia sesión."); window.location.href = "login.php";</script>';

            } elseif ($typeAcc === 'maquila') {
                require_once('controllers/maquilaController.php');
                $MaquilaController = new MaquilaController();

                $MaquilaController->registrar([
                    'Nombre_Maquila' => $nombre,
                    'Contraseña_Maquila' => $contra
                ]);
                echo '<script>alert("Maquila registrada con éxito. Inicia sesión."); window.location.href = "login.php";</script>';
            }
        } catch (Exception $e) {
            echo '<script>alert("Error en registro: ' . addslashes($e->getMessage()) . '")</script>';
        }
    }
}
?>