<?php
session_start();
if (!isset($_SESSION['Id_Usuario'])) {
    header("Location: login.php");
    exit();
}
if ($_SESSION['Id_Usuario'] != 1) {
    header("Location: escanear.php");
    exit();
}

?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Menú Principal</title>
    <link rel="stylesheet" href="css/styleIndex.css">
</head>
<body>
    <nav class="header">
        <span class="nav-brand">Bienvenido: <?= htmlspecialchars($_SESSION['Nombre_Usuario']) ?></span>
        <a href="login.php" class="logout-button">Salir</a>
    </nav>

    <div class="main-container">
        <div class="button-grid">
            <button class="action-btn" onclick="showDiv('div1')">Usuarios</button>
            <button class="action-btn" onclick="showDiv('div2')">2</button>
            <button class="action-btn" onclick="showDiv('div3')">3</button>
            <button class="action-btn" onclick="showDiv('div4')">4</button>
        </div>

        <div id="div1" class="content-panel">
            <h1>Usuarios</h1>
            <br>
            <form action="" method="POST">
                <label>Registra un usuario al sistema</label>
                <input type="text" name="Nombre_Usuario" placeholder="Nombre del usuario" required>
                <input type="tel" name="Telefono" placeholder="Telefono del Usuario" required>
                <input type="text" name="Contra_Usuario" placeholder="Una contraseña para el usuario" required>
                <input type="hidden" name="Registrar_Usuario" value="1">
                <button type="submit"> Registrar </button>
            </form>

            <?php
            require_once('controllers/usuarioController.php');

            $UsuarioController = new UsuarioController();
            $usuarios = $UsuarioController->obtenerTodos();

            ?>

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre de Usuario</th>
                        <th>Teléfono</th>
                        <th>Editar</th>
                        <th>Borrar</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if ($usuarios): ?>
                        <?php foreach ($usuarios as $usuario): ?>
                            <tr>
                                <form> 
                                <td><input type="text" name="Id_Usuario" value="<?= htmlspecialchars($usuario['Id_Usuario']) ?>"></td>
                                <td><input type="text" name="Nombre_Usuario" value="<?= htmlspecialchars($usuario['Nombre_Usuario']) ?>"></td>
                                <td><input type="text" name="Telefono" value="<?= htmlspecialchars($usuario['Telefono_Usuario']) ?>"></td>
                                
                                <td><?= htmlspecialchars($usuario['Nombre_Usuario']) ?></td>
                                <td><?= htmlspecialchars($usuario['Telefono']) ?></td>
                                <td>
                                </td>
                                </form>
                                <td>
                            </tr>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <tr><td colspan="4">No hay usuarios registrados</td></tr>
                    <?php endif; ?>
                </tbody>
            </table>
        
        </div>

        <div id="div2" class="content-panel">
            <h1>2</h1>
        </div>
        <div id="div3" class="content-panel">
            <h1>3</h1>
        </div>
        <div id="div4" class="content-panel">
            <h1>4</h1>
        </div>
    </div>

    <script>
        function showDiv(divId) {
            // Ocultar todos los paneles
            document.querySelectorAll('.content-panel').forEach(panel => {
                panel.style.display = 'none';
            });
            // Mostrar el panel seleccionado
            document.getElementById(divId).style.display = 'block';
        }

        // Mostrar el panel guardado en sesión (o el primero por defecto)
        <?php if (isset($_SESSION['divID'])): ?>
            showDiv('div<?= $_SESSION['divID'] ?>');
        <?php else: ?>
            showDiv('div1');
        <?php endif; ?>
    </script>

    <?php
    require_once('conn.php');

    if ($_SERVER["REQUEST_METHOD"] == "POST")
    {

        if(isset($_POST['Registrar_Usuario']))
        {

            $UsuarioController = new UsuarioController();
            $payload = [
                        "Nombre_Usuario" => $_POST["Nombre_Usuario"] ?? null,
                        "Telefono"       => $_POST["Telefono"] ?? null,
                        "Contra_Usuario" => $_POST["Contra_Usuario"] ?? null,
                       ];
                
            try {
                $UsuarioController->registrar($payload);
                echo "<script>alert('✅ Usuario registrado con éxito');</script>";
            } catch (Exception $e) {
                echo "<script>alert('❌ Error: " . $e->getMessage() . "');</script>";
            }
            unset($_POST['Registrar_Usuario']);
        }
        


    }


    ?>
</body>
</html>
