<?php
session_start();
if (!isset($_SESSION['Id_Usuario'])) {
    header("Location: login.php");
    exit();
}
require_once 'controllers/usuarioController.php';
require_once 'controllers/maquilaController.php';
$MaquilaController = new MaquilaController();
$UsuarioController = new UsuarioController();
$mensaje = $_SESSION['mensaje'] ?? '';
unset($_SESSION['mensaje']);

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

//USUARIOS/////////////////////////////////////////

    if (isset($_POST['Registrar_Usuario'])) {
        $permitidos = ["Nombre_Usuario", "Telefono_Usuario", "Password_Usuario", "Puesto_Usuario"];
        $payload = array_intersect_key($_POST, array_flip($permitidos));
        try {
            $UsuarioController->registrar($payload);
            $_SESSION['mensaje'] = "✅ Usuario registrado con éxito";
            header("Location: " . $_SERVER['PHP_SELF']);
            exit();
        } catch (Exception $e) {
            $mensaje = "❌ Error: " . $e->getMessage();
        }
    }

    if (isset($_POST['Editar_Usuario'])) {
        $permitidos = ["Nombre_Usuario", "Telefono_Usuario", "Password_Usuario", "Puesto_Usuario"];
        $payload = array_intersect_key($_POST, array_flip($permitidos));
        $id = isset($_POST['Id_Usuario']) ? intval($_POST['Id_Usuario']) : 0;
        if ($id > 0) {
            try {
                $UsuarioController->actualizar($id, $payload);
                $_SESSION['mensaje'] = "✅ Usuario modificado con éxito";
                header("Location: " . $_SERVER['PHP_SELF']);
                exit();
            } catch (Exception $e) {
                $mensaje = "❌ Error: " . $e->getMessage();
            }
        } else {
            $mensaje = "❌ Id de usuario inválido";
        }
    }

    if (isset($_POST['Borrar_Usuario'])) {
        $id = isset($_POST['Id_Usuario']) ? intval($_POST['Id_Usuario']) : 0;
        if ($id > 0) {
            try {
                $UsuarioController->eliminar($id);
                $_SESSION['mensaje'] = "✅ Usuario eliminado con éxito";
                header("Location: " . $_SERVER['PHP_SELF']);
                exit();
            } catch (Exception $e) {
                $mensaje = "❌ Error: " . $e->getMessage();
            }
        } else {
            $mensaje = "❌ Id de usuario inválido";
        }
    }

//USUARIOS///////////////////////////////////////// 

//MAQUILA//////////////////////////////////////////

    if (isset($_POST['Registrar_Maquila'])){
        $permitidos = ["Nombre_Maquila", "Contraseña_Maquila"];
        $payload = array_intersect_key($_POST, array_flip($permitidos));
        try {
            $MaquilaController->registrar($payload);
            $_SESSION['mensaje'] = "✅ Maquila registrada con éxito";
            header("Location: " . $_SERVER['PHP_SELF']);
            exit();
        } catch (Exception $e) {
            $mensaje = "❌ Error: " . $e->getMessage();
        }
    }

    if (isset($_POST['Editar_Maquila'])){
        $permitidos = ["Nombre_Maquila", "Contraseña_Maquila"];
        $payload = array_intersect_key($_POST, array_flip($permitidos));
        $id = isset($_POST['Id_Maquila']) ? intval($_POST['Id_Maquila']) : 0;
        if ($id > 0) {
            try {
                $MaquilaController->actualizar($id, $payload);
                $_SESSION['mensaje'] = "✅ Maquila modificado con éxito";
                header("Location: " . $_SERVER['PHP_SELF']);
                exit();
            } catch (Exception $e) {
                $mensaje = "❌ Error: " . $e->getMessage();
            }
        } else {
            $mensaje = "❌ Id de maquila inválido";
        }
    }

    if (isset($_POST['Borrar_Maquila'])) {
        $id = isset($_POST['Id_Maquila']) ? intval($_POST['Id_Maquila']) : 0;
        if ($id > 0) {
            try {
                $MaquilaController->eliminar($id);
                $_SESSION['mensaje'] = "✅ Maquila eliminada con éxito";
                header("Location: " . $_SERVER['PHP_SELF']);
                exit();
            } catch (Exception $e) {
                $mensaje = "❌ Error: " . $e->getMessage();
            }
        } else {
            $mensaje = "❌ Id de maquila inválido";
        }
    }


//MAQUILA//////////////////////////////////////////



}
//AGARRES/////////////////////////////////////////
$usuarios = $UsuarioController->obtenerTodos();
$maquilas = $MaquilaController->obtenerTodos();
//AGARRES/////////////////////////////////////////

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
            <button class="action-btn" onclick="showDiv('div2')">Maquila</button>
            <button class="action-btn" onclick="showDiv('div3')">3</button>
            <button class="action-btn" onclick="showDiv('div4')">4</button>
        </div>

        <?php if ($mensaje): ?>
            <div id="mensaje" class="mensaje <?= (strpos($mensaje,'✅')===0) ? 'ok' : 'error' ?>">
                <?= htmlspecialchars($mensaje) ?>
            </div>
        <?php endif; ?>
        
        <!--USUARIOS/////////////////////////////-->
        <div id="div1" class="content-panel">
            <h1>Usuarios</h1>
            <form action="" method="POST" style="margin-bottom:16px;">
                <label>Registra un usuario al sistema</label><br>
                <input type="text" name="Nombre_Usuario" placeholder="Nombre del usuario" required>
                <input type="tel" name="Telefono_Usuario" placeholder="Telefono del Usuario" required>
                <input type="password" name="Password_Usuario" placeholder="Una contraseña para el usuario" required>
                <input type="text" name="Puesto_Usuario" placeholder="Puesto del usuario" required>
                <input type="hidden" name="Registrar_Usuario" value="1">
                <button type="submit">Registrar</button>
            </form>

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Datos</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if ($usuarios): ?>
                        <?php foreach ($usuarios as $usuario): ?>
                            <tr>
                                <td><?= htmlspecialchars($usuario['Id_Usuario']) ?></td>
                                <td>
                                    <form method="post" style="display:flex; gap:8px; align-items:center;">
                                        <input type="text" name="Nombre_Usuario" value="<?= htmlspecialchars($usuario['Nombre_Usuario']) ?>" required>
                                        <input type="tel" name="Telefono_Usuario" value="<?= htmlspecialchars($usuario['Telefono_Usuario'] ?? '') ?>">
                                        <input type="text" name="Puesto_Usuario" value="<?= htmlspecialchars($usuario['Puesto_Usuario'] ?? '') ?>">
                                        <input type="password" name="Password_Usuario" value="" placeholder="Nueva contraseña" required>
                                        <input type="hidden" name="Id_Usuario" value="<?= htmlspecialchars($usuario['Id_Usuario']) ?>">
                                        <button type="submit" name="Editar_Usuario" value="1">Editar</button>
                                    </form>
                                </td>
                                <td>
                                    <form method="post" style="display:inline;">
                                        <input type="hidden" name="Id_Usuario" value="<?= htmlspecialchars($usuario['Id_Usuario']) ?>">
                                        <button type="submit" name="Borrar_Usuario" value="1" onclick="return confirm('¿Eliminar usuario <?= htmlspecialchars($usuario['Nombre_Usuario']) ?>?');">Borrar</button>
                                    </form>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <tr><td colspan="3">No hay usuarios registrados</td></tr>
                    <?php endif; ?>
                </tbody>
            </table>
        </div>
        <!--USUARIOS/////////////////////////////-->

        <!--MAQUILAS/////////////////////////////-->
        <div id="div2" class="content-panel">
            <h1>Maquilas</h1>
            <form action="" method="POST" style="margin-bottom:16px;">
                <label>Registra una maquila al sistema</label><br>
                <input type="text" name="Nombre_Maquila" placeholder="Nombre de la maquila" required>
                <input type="password" name="Contraseña_Maquila" placeholder="Una contraseña para el usuario" required>
                <input type="hidden" name="Registrar_Maquila" value="1">
                <button type="submit">Registrar</button>
            </form>
        
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Datos</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if ($maquilas): ?>
                        <?php foreach ($maquilas as $maquila): ?>
                            <tr>
                                <td><?= htmlspecialchars($maquila['Id_Maquila']) ?></td>
                                <td>
                                    <form method="post" style="display:flex; gap:8px; align-items:center;">
                                        <input type="text" name="Nombre_Maquila" value="<?= htmlspecialchars($maquila['Nombre_Maquila']) ?>" required>
                                        <input type="password" name="Contraseña_Maquila" value="" placeholder="Nueva contraseña" required>
                                        <input type="hidden" name="Id_Maquila" value="<?= htmlspecialchars($maquila['Id_Maquila']) ?>">
                                        <button type="submit" name="Editar_Maquila" value="1">Editar</button>
                                    </form>
                                </td>
                                <td>
                                    <form method="post" style="display:inline;">
                                        <input type="hidden" name="Id_Maquila" value="<?= htmlspecialchars($maquila['Id_Maquila']) ?>">
                                        <button type="submit" name="Borrar_Maquila" value="1" onclick="return confirm('¿Eliminar maquila <?= htmlspecialchars($maquila['Nombre_Maquila']) ?>?');">Borrar</button>
                                    </form>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <tr><td colspan="3">No hay maquilas registradas</td></tr>
                    <?php endif; ?>
                </tbody>
            </table>
        </div>
        <!--MAQUILAS/////////////////////////////-->

        <!--AREAS/////////////////////////////-->
        <div id="div3" class="content-panel">
        <h1></h1>
        </div>
        <!--AREAS/////////////////////////////-->

        <div id="div4" class="content-panel"><h1>4</h1></div>
    </div>

    <script>
        function showDiv(divId) {
            document.querySelectorAll('.content-panel').forEach(panel => panel.style.display = 'none');
            const el = document.getElementById(divId);
            if (el) el.style.display = 'block';
            try { localStorage.setItem('adminDiv', divId); } catch(e){}
        }
        const defaultDiv = <?= isset($_SESSION['divID']) ? json_encode('div'.intval($_SESSION['divID'])) : 'null' ?>;
        const saved = defaultDiv || (localStorage.getItem('adminDiv') || 'div1');
        showDiv(saved);
    </script>
</body>
</html>
