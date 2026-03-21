<?php
session_start();
if (!isset($_SESSION['Id_Usuario'])) {
    header("Location: login.php");
    exit();
}

// Check de Rol Administrador
if (empty($_SESSION['Puesto_Usuario']) || $_SESSION['Puesto_Usuario'] !== 'Administrador') {
    // Si no es admin, fuera
    session_destroy();
    echo "<script>
        alert('Acceso denegado. Esta sección es solo para Administradores.\\n\\nSi eres un Usuario, por favor utiliza la Aplicación Móvil.');
        window.location.href = 'login.php';
    </script>";
    exit();
}
require_once 'controllers/usuarioController.php';
require_once 'controllers/maquilaController.php';
require_once 'controllers/areaController.php';
require_once 'controllers/maquilaareaController.php';
require_once 'controllers/reporteController.php';

$MaquilaController = new MaquilaController();
$UsuarioController = new UsuarioController();
$AreaController = new AreaController();
$MaquilaAreaController = new MaquilaAreaController();
$ReporteController = new ReporteController();
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

    if (isset($_POST['Registrar_Maquila'])) {
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

    if (isset($_POST['Editar_Maquila'])) {
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

    //AREA/////////////////////////////////////////////

    if (isset($_POST['Registrar_Area'])) {
        $permitidos = ["Nombre_Area", "Descripcion_Area", "NumeroCAR_Area", "JSON_Area"];
        $payload = array_intersect_key($_POST, array_flip($permitidos));
        try {
            $idArea = (int) $AreaController->registrar($payload);
            try {
                $MaquilaAreaController->asignarMaquilaArea($_POST['Id_Maquila'], $idArea);
            } catch (Exception $e) {
                $mensaje = "❌ Error: " . $e->getMessage();
            }
            $_SESSION['mensaje'] = "✅ Sistema registrado con éxito";
            header("Location: " . $_SERVER['PHP_SELF']);
            exit();
        } catch (Exception $e) {
            $mensaje = "❌ Error: " . $e->getMessage();
        }
    }

    // (Logica de BuscarPorMaquila movida a GET abajo en la vista)

    if (isset($_POST['Editar_Area']) && $_POST['Editar_Area'] == '1') {
        // campos permitidos para update (incluye Id_Area)
        $permitidos = ["Id_Area", "Nombre_Area", "Descripcion_Area", "NumeroCAR_Area", "JSON_Area", "Id_Maquila"];
        $payload = array_intersect_key($_POST, array_flip($permitidos));
        try {
            $updated = $AreaController->editarArea($payload);
            // si manejas asignación de maquila con controlador aparte:
            if (!empty($_POST['Id_Maquila']) && !empty($_POST['Id_Area'])) {
                try {
                    $MaquilaAreaController->asignarMaquilaArea((int) $_POST['Id_Maquila'], (int) $_POST['Id_Area']);
                } catch (Exception $e) {
                    // manejar error de asignación, pero la edición ya fue hecha
                    $mensaje = "❌ Error asignando maquila: " . $e->getMessage();
                }
            }
            $_SESSION['mensaje'] = "✅ Sistema actualizado con éxito";
            header("Location: " . $_SERVER['PHP_SELF']);
            exit();
        } catch (Exception $e) {
            $mensaje = "❌ Error: " . $e->getMessage();
        }
    }

    //AREA/////////////////////////////////////////////



}
//AGARRES/////////////////////////////////////////
$usuarios = $UsuarioController->obtenerTodos();
$maquilas = $MaquilaController->obtenerTodos();
if (!empty($_SESSION['areasPorMaquila'])) {
    $areasPorMaquila = $_SESSION['areasPorMaquila'];
}
if (!isset($_SESSION['areasPorMaquilaQueMaquila'])) {
    $_SESSION['areasPorMaquilaQueMaquila'] = 0;
}
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
        <div class="nav-brand">
            <img src="css/logo.png" alt="Logo FCS" class="nav-logo">
            <span>FCS</span>
        </div>

        <div class="nav-menu">
            <button class="action-btn" onclick="showDiv('div1')">👤 Usuarios</button>
            <button class="action-btn" onclick="showDiv('div2')">🏭 Maquila</button>
            <button class="action-btn" onclick="showDiv('div3')">📍 Sistema</button>
            <button class="action-btn" onclick="showDiv('div4')">📊 Reportes</button>
        </div>

        <div class="nav-actions">
            <span style="color: rgba(255,255,255,0.7); font-size: 0.9rem; font-weight: 500;">Hola,
                <?= htmlspecialchars($_SESSION['Nombre_Usuario']) ?></span>
            <a href="logout.php" class="logout-button">Salir</a>
        </div>
    </nav>

    <div class="main-container">

        <?php if ($mensaje): ?>
            <div id="mensaje" class="mensaje <?= (strpos($mensaje, '✅') === 0) ? 'ok' : 'error' ?>">
                <?= htmlspecialchars($mensaje) ?>
            </div>
        <?php endif; ?>

        <!--USUARIOS/////////////////////////////-->
        <div id="div1" class="content-panel">
            <div class="section-header">
                <div class="section-icon">👤</div>
                <h1>Usuarios</h1>
            </div>

            <div class="register-card">
                <h2>Registrar nuevo usuario</h2>
                <form action="" method="POST">
                    <div class="form-grid">
                        <div class="form-field">
                            <label for="reg_nombre_usuario">Nombre</label>
                            <input type="text" id="reg_nombre_usuario" name="Nombre_Usuario" placeholder="Nombre completo" required>
                        </div>
                        <div class="form-field">
                            <label for="reg_tel_usuario">Teléfono</label>
                            <input type="tel" id="reg_tel_usuario" name="Telefono_Usuario" placeholder="Ej: 6441234567" required>
                        </div>
                        <div class="form-field">
                            <label for="reg_pass_usuario">Contraseña</label>
                            <input type="password" id="reg_pass_usuario" name="Password_Usuario" placeholder="Contraseña segura" required>
                        </div>
                        <div class="form-field">
                            <label for="reg_puesto_usuario">Puesto</label>
                            <input type="text" id="reg_puesto_usuario" name="Puesto_Usuario" placeholder="Ej: Operador" required>
                        </div>
                        <div class="form-grid-actions">
                            <input type="hidden" name="Registrar_Usuario" value="1">
                            <button type="submit" class="btn-primary">✚ Registrar</button>
                        </div>
                    </div>
                </form>
            </div>

            <table class="section-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Datos del Usuario</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if ($usuarios): ?>
                        <?php foreach ($usuarios as $usuario): ?>
                            <tr>
                                <td class="row-id">#<?= htmlspecialchars($usuario['Id_Usuario']) ?></td>
                                <td>
                                    <form method="post">
                                        <div class="table-form-row">
                                            <input type="text" name="Nombre_Usuario"
                                                value="<?= htmlspecialchars($usuario['Nombre_Usuario']) ?>" required placeholder="Nombre">
                                            <input type="tel" name="Telefono_Usuario"
                                                value="<?= htmlspecialchars($usuario['Telefono_Usuario'] ?? '') ?>" placeholder="Teléfono">
                                            <input type="text" name="Puesto_Usuario"
                                                value="<?= htmlspecialchars($usuario['Puesto_Usuario'] ?? '') ?>" placeholder="Puesto">
                                            <input type="password" name="Password_Usuario" value=""
                                                placeholder="Nueva contraseña (Opcional)">
                                            <input type="hidden" name="Id_Usuario"
                                                value="<?= htmlspecialchars($usuario['Id_Usuario']) ?>">
                                            <button type="submit" name="Editar_Usuario" value="1" class="btn-edit">✏ Editar</button>
                                        </div>
                                    </form>
                                </td>
                                <td>
                                    <form method="post">
                                        <input type="hidden" name="Id_Usuario"
                                            value="<?= htmlspecialchars($usuario['Id_Usuario']) ?>">
                                        <button type="submit" name="Borrar_Usuario" value="1" class="btn-delete"
                                            onclick="return confirm('¿Eliminar usuario <?= htmlspecialchars($usuario['Nombre_Usuario']) ?>?');">🗑 Borrar</button>
                                    </form>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <tr class="empty-state">
                            <td colspan="3">No hay usuarios registrados aún.</td>
                        </tr>
                    <?php endif; ?>
                </tbody>
            </table>
        </div>
        <!--USUARIOS/////////////////////////////-->

        <!--MAQUILAS/////////////////////////////-->
        <div id="div2" class="content-panel">
            <div class="section-header">
                <div class="section-icon">🏭</div>
                <h1>Maquilas</h1>
            </div>

            <div class="register-card">
                <h2>Registrar nueva maquila</h2>
                <form action="" method="POST">
                    <div class="form-grid">
                        <div class="form-field">
                            <label for="reg_nombre_maquila">Nombre</label>
                            <input type="text" id="reg_nombre_maquila" name="Nombre_Maquila" placeholder="Nombre de la maquila" required>
                        </div>
                        <div class="form-field">
                            <label for="reg_pass_maquila">Contraseña</label>
                            <input type="password" id="reg_pass_maquila" name="Contraseña_Maquila" placeholder="Contraseña de acceso" required>
                        </div>
                        <div class="form-grid-actions">
                            <input type="hidden" name="Registrar_Maquila" value="1">
                            <button type="submit" class="btn-primary">✚ Registrar</button>
                        </div>
                    </div>
                </form>
            </div>

            <table class="section-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Datos de la Maquila</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if ($maquilas): ?>
                        <?php foreach ($maquilas as $maquila): ?>
                            <tr>
                                <td class="row-id">#<?= htmlspecialchars($maquila['Id_Maquila']) ?></td>
                                <td>
                                    <form method="post">
                                        <div class="table-form-row">
                                            <input type="text" name="Nombre_Maquila"
                                                value="<?= htmlspecialchars($maquila['Nombre_Maquila']) ?>" required placeholder="Nombre">
                                            <input type="password" name="Contraseña_Maquila" value=""
                                                placeholder="Nueva contraseña (Opcional)">
                                            <input type="hidden" name="Id_Maquila"
                                                value="<?= htmlspecialchars($maquila['Id_Maquila']) ?>">
                                            <button type="submit" name="Editar_Maquila" value="1" class="btn-edit">✏ Editar</button>
                                        </div>
                                    </form>
                                </td>
                                <td>
                                    <form method="post">
                                        <input type="hidden" name="Id_Maquila"
                                            value="<?= htmlspecialchars($maquila['Id_Maquila']) ?>">
                                        <button type="submit" name="Borrar_Maquila" value="1" class="btn-delete"
                                            onclick="return confirm('¿Eliminar maquila <?= htmlspecialchars($maquila['Nombre_Maquila']) ?>?');">🗑 Borrar</button>
                                    </form>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <tr class="empty-state">
                            <td colspan="3">No hay maquilas registradas aún.</td>
                        </tr>
                    <?php endif; ?>
                </tbody>
            </table>
        </div>
        <!--MAQUILAS/////////////////////////////-->

        <!--AREAS/////////////////////////////-->
        <div id="div3" class="content-panel">

            <div class="section-header">
                <div class="section-icon">📍</div>
                <h1>Sistemas</h1>
            </div>

            <div class="register-card">
                <form id="areaForm" method="post" action="">

                    <!-- ZONA A: Información básica del Sistema -->
                    <div class="zona-label"><span>Información del Sistema</span></div>
                    <div class="sistema-info-grid">
                        <div class="form-field">
                            <label for="area_name">Nombre del Sistema</label>
                            <input type="text" name="Nombre_Area" id="area_name" placeholder="Ej: Línea de ensamble 1" required />
                        </div>

                        <div class="form-field">
                            <label for="area_description">Descripción</label>
                            <input type="text" name="Descripcion_Area" id="area_description" placeholder="Breve descripción del sistema" />
                        </div>

                        <div class="form-field">
                            <label for="maquila_id">Maquila asignada</label>
                            <select name="Id_Maquila" id="maquila_id" required>
                                <?php if ($maquilas): ?>
                                    <?php foreach ($maquilas as $maquila): ?>
                                        <option value="<?= htmlspecialchars($maquila['Id_Maquila']) ?>">
                                            <?= htmlspecialchars($maquila['Nombre_Maquila']) ?>
                                        </option>
                                    <?php endforeach; ?>
                                <?php endif; ?>
                            </select>
                        </div>
                    </div>

                    <!-- ZONA B: Constructor de CARs -->
                    <div class="zona-label"><span>Cosas A Revisar (C.A.R.)</span></div>
                    <div class="car-builder">

                        <!-- COLUMNA IZQUIERDA: definir un C.A.R. -->
                        <div class="car-builder-left">

                            <div class="form-field">
                                <label for="car_name_input">Nombre del C.A.R.</label>
                                <input type="text" id="car_name_input" placeholder="Ej: Válvula principal" />
                            </div>

                            <div>
                                <p style="font-size:0.78rem; font-weight:700; color:#64748b; text-transform:uppercase; letter-spacing:0.05em; margin-bottom:8px;">Tipo de propiedad a agregar</p>
                                <div class="prop-pills">
                                    <button type="button" class="prop-pill prop-btn" data-type="bool" data-tooltip="Ok / No Ok">✅</button>
                                    <button type="button" class="prop-pill prop-btn" data-type="range" data-tooltip="Rango">📏</button>
                                    <button type="button" class="prop-pill prop-btn" data-type="number" data-tooltip="Número">🔢</button>
                                    <button type="button" class="prop-pill prop-btn" data-type="text" data-tooltip="Descripción">📝</button>
                                    <button type="button" class="prop-pill prop-btn" data-type="date" data-tooltip="Fecha">📅</button>
                                </div>
                            </div>

                            <!-- Editor de propiedades (se muestra al seleccionar tipo) -->
                            <div class="prop-editor-panel" id="propEditor" aria-hidden="true">
                                <h3 id="editorTitle">Añadir propiedad</h3>
                                <label class="field small">
                                    <span>Nombre de propiedad</span>
                                    <input type="text" id="prop_label" placeholder="Ej: Funcionamiento" />
                                </label>
                                <div id="typeSettings"></div>
                                <div class="prop-editor-actions editor-actions">
                                    <button type="button" id="addPropertyBtn" class="btn-primary" style="padding:0.45rem 1rem; font-size:0.85rem;">Agregar propiedad</button>
                                    <button type="button" id="cancelPropertyBtn" class="secondary btn-secondary" style="padding:0.45rem 1rem; font-size:0.85rem;">Cancelar</button>
                                </div>
                            </div>

                        </div>

                        <!-- COLUMNA DERECHA: propiedades actuales + CARs confirmados -->
                        <div class="car-builder-right">

                            <div class="car-panel-card">
                                <h4>Propiedades del C.A.R. en construcción</h4>
                                <div id="currentPropsContainer">
                                    <p class="muted">Selecciona un tipo de propiedad con los botones de la izquierda.</p>
                                </div>
                            </div>

                            <div class="car-panel-card">
                                <h4>C.A.R.s confirmados en este sistema</h4>
                                <div id="carsList"></div>
                                <div class="add-car-row">
                                    <button type="button" id="addCarBtn" class="btn-primary" style="padding:0.5rem 1.2rem; font-size:0.88rem;">＋ Confirmar C.A.R.</button>
                                </div>
                            </div>

                        </div>
                    </div>

                    <!-- ZONA C: Campos ocultos y submit -->
                    <div class="sistemas-submit-row final-row">
                        <input type="hidden" name="Editar_Area" id="editar_area_input" value="0" />
                        <input type="hidden" name="Id_Area" id="area_id_input" />
                        <input type="hidden" name="JSON_Area" id="area_json" />
                        <input type="hidden" name="NumeroCAR_Area" id="car_count_input" value="0" />
                        <input type="hidden" name="Registrar_Area" value="1" />
                        <button type="submit" id="submitBtn" class="btn-primary" style="padding:0.7rem 1.8rem; font-size:0.95rem;">🗸 Finalizar y Registrar Sistema</button>
                    </div>

                </form>
            </div>

            <script src="js/area_form.js"></script>

            <!-- Buscador de sistemas por maquila -->
            <div class="search-card">
                <form method="get" action="#div3" style="display:contents;">
                    <input type="hidden" name="view" value="areas_search">
                    <div class="form-field">
                        <label for="selectMaquila">Buscar sistemas por maquila</label>
                        <select name="maquila_search_area" id="selectMaquila" required>
                            <option value="">— Seleccionar maquila —</option>
                            <?php if ($maquilas): ?>
                                <?php foreach ($maquilas as $maquila):
                                    $mid = $maquila['Id_Maquila'] ?? $maquila['id'] ?? '';
                                    $mname = $maquila['Nombre_Maquila'] ?? '';
                                    $selected = (isset($_GET['maquila_search_area']) && $_GET['maquila_search_area'] == $mid) ? 'selected' : '';
                                    ?>
                                    <option value="<?= htmlspecialchars($mid) ?>" <?= $selected ?>>
                                        <?= htmlspecialchars($mname) ?>
                                    </option>
                                <?php endforeach; ?>
                            <?php endif; ?>
                        </select>
                    </div>
                    <button type="submit" class="btn-primary" style="padding:0.6rem 1.4rem; white-space:nowrap;">🔍 Buscar</button>
                </form>
            </div>

            <?php
            // --- Lógica de búsqueda de áreas (GET) ---
            $areasPorMaquilaList = [];
            $laMaquilaBuscadaNombre = '';

            if (!empty($_GET['maquila_search_area'])) {
                $mqSearchId = (int) $_GET['maquila_search_area'];
                try {
                    // Obtener nombre de maquila para titulo
                    if (isset($MaquilaController)) {
                        $mqs = $MaquilaController->obtenerPor('Id_Maquila', $mqSearchId);
                        if (!empty($mqs)) {
                            $laMaquilaBuscadaNombre = $mqs[0]['Nombre_Maquila'] ?? '';
                        }
                    }
                    // Obtener areas
                    if (isset($MaquilaAreaController)) {
                        $areasPorMaquilaList = $MaquilaAreaController->obtenerAreasPorMaquila($mqSearchId);
                        // Normalizar (si devuelve array plano o con success key)
                        if (isset($areasPorMaquilaList['success']))
                            $areasPorMaquilaList = $areasPorMaquilaList['data'] ?? [];
                    }
                } catch (\Throwable $e) {
                    echo "<div class='error'>Error al buscar sistemas: " . $e->getMessage() . "</div>";
                }
            }
            ?>

            <?php if ($laMaquilaBuscadaNombre): ?>
                <div class="sistemas-results-header">
                    <h2>Sistemas de: <?= htmlspecialchars($laMaquilaBuscadaNombre) ?></h2>
                    <span class="result-badge"><?= count($areasPorMaquilaList) ?> sistema<?= count($areasPorMaquilaList) !== 1 ? 's' : '' ?></span>
                </div>
            <?php endif; ?>

            <table class="section-table" style="margin-top: <?= $laMaquilaBuscadaNombre ? '0' : '1.5rem' ?>;">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Descripción</th>
                        <th># CARs / Acciones</th>
                        <th>Código QR</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if (!empty($areasPorMaquilaList)): ?>
                        <?php foreach ($areasPorMaquilaList as $areaPorMaquila): ?>
                            <tr>
                                <td class="row-id">#<?= htmlspecialchars($areaPorMaquila['Id_Area']) ?></td>
                                <td><strong><?= htmlspecialchars($areaPorMaquila['Nombre_Area']) ?></strong></td>
                                <td style="color:#64748b; font-size:0.88rem;"><?= htmlspecialchars($areaPorMaquila['Descripcion_Area']) ?></td>
                                <td>
                                    <div style="display:flex; flex-wrap:wrap; align-items:center; gap:8px;">
                                        <span style="background:#f1f5f9; color:#475569; border-radius:99px; padding:2px 10px; font-size:0.78rem; font-weight:700;">
                                            <?= htmlspecialchars($areaPorMaquila['NumeroCAR_Area']) ?> CARs
                                        </span>

                                        <?php
                                        $areaTmp = $areaPorMaquila['JSON_Area'] ?? '[]';
                                        $areaData = json_decode($areaTmp, true);
                                        if ($areaData === null) $areaData = [];
                                        $areaJsonEscaped = htmlspecialchars(json_encode($areaData, JSON_HEX_TAG | JSON_HEX_APOS | JSON_HEX_QUOT | JSON_HEX_AMP), ENT_QUOTES, 'UTF-8');
                                        ?>

                                        <button type="button" class="mostrarCarsBtn btn-secondary" data-area="<?= $areaJsonEscaped ?>">👁 Ver CARs</button>
                                        <button type="button" class="editarAreaBtn btn-edit" data-area="<?= $areaJsonEscaped ?>"
                                            data-id="<?= htmlspecialchars($areaPorMaquila['Id_Area']) ?>">✏ Editar</button>
                                    </div>
                                </td>
                                <td style="text-align:center;">
                                    <?php if (!empty($areaPorMaquila['Codigo_Area'])): ?>
                                        <a href="qrcodes/<?= htmlspecialchars($areaPorMaquila['Codigo_Area']) ?>.png"
                                            download="CodigoQR_<?= htmlspecialchars($areaPorMaquila['Nombre_Area']) ?>.png"
                                            title="Descargar QR">
                                            <img style="max-width:72px; max-height:72px; border-radius:8px; border:1px solid #e8ecf4;"
                                                src="qrcodes/<?= htmlspecialchars($areaPorMaquila['Codigo_Area']) ?>.png" />
                                        </a>
                                    <?php else: ?>
                                        <span style="color:#94a3b8; font-size:0.82rem;">Sin QR</span>
                                    <?php endif; ?>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <tr class="empty-state">
                            <td colspan="5">
                                <?= $laMaquilaBuscadaNombre ? 'No hay sistemas registrados para esta maquila.' : 'Selecciona una maquila arriba para ver sus sistemas.' ?>
                            </td>
                        </tr>
                    <?php endif; ?>
                </tbody>
            </table>

            <script src="js/popupCars.js?v=2"></script>

            <!-- Script para enganchar los botones y pasarles su JSON al popup -->
            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    const botones = document.querySelectorAll('.mostrarCarsBtn');
                    botones.forEach(btn => {
                        btn.addEventListener('click', function () {
                            const data = btn.getAttribute('data-area');
                            let areaObj = {};
                            try {
                                areaObj = data ? JSON.parse(data) : {};
                            } catch (err) {
                                console.error('JSON inválido en data-area:', err, data);
                                areaObj = {};
                            }
                            if (window.CarsPopup && typeof window.CarsPopup.showCars === 'function') {
                                window.CarsPopup.showCars(areaObj);
                            } else {
                                console.warn('popupCars.js no cargado o CarsPopup no definido');
                            }
                        });
                    });
                });
            </script>

            <script>
                document.addEventListener('DOMContentLoaded', () => {

                    // util: decode HTML entities (para JSON escapado en data-area)
                    const decodeHtml = s => {
                        if (!s) return s;
                        const t = document.createElement('textarea');
                        t.innerHTML = s;
                        return t.value;
                    };

                    // parse JSON robusto (intenta raw, luego decodeHtml)
                    const parseAreaData = raw => {
                        if (!raw) return {};
                        if (typeof raw === 'object') return raw;
                        try { return JSON.parse(raw); }
                        catch (e) {
                            try { return JSON.parse(decodeHtml(raw)); }
                            catch (e2) { console.error('parseAreaData error', e, e2); return {}; }
                        }
                    };

                    // Mostrar C.A.R.s (comportamiento original)
                    const handleMostrarCars = btn => {
                        const area = parseAreaData(btn.getAttribute('data-area'));
                        if (window.CarsPopup?.showCars) window.CarsPopup.showCars(area);
                        else console.warn('CarsPopup no disponible');
                    };

                    // Fallback mínimo para rellenar el form si loadAreaForEdit no existe o falla
                    const fallbackLoad = (areaObj = {}, id = null) => {
                        const set = (id, v) => { const el = document.getElementById(id); if (el) el.value = v ?? ''; };
                        set('area_name', areaObj.Nombre_Area ?? areaObj.area_name ?? areaObj.Nombre ?? '');
                        set('area_description', areaObj.Descripcion_Area ?? areaObj.area_description ?? areaObj.Descripcion ?? '');
                        const mq = document.getElementById('maquila_id');
                        const candidate = areaObj.Id_Maquila ?? areaObj.maquila_id ?? null;
                        if (mq && (candidate !== null && candidate !== undefined)) { mq.value = String(candidate); mq.dispatchEvent(new Event('change', { bubbles: true })); }
                        set('area_id_input', id ?? areaObj.Id_Area ?? areaObj.id ?? '');
                        // marcar edición
                        const editar = document.getElementById('editar_area_input');
                        if (editar) editar.value = '1';
                        const submit = document.getElementById('submitBtn'); if (submit) submit.textContent = 'Actualizar Sistema';
                        // actualizar hidden JSON y contador (si no existe UI para cars)
                        const areaJson = document.getElementById('area_json');
                        const carCount = document.getElementById('car_count_input');
                        let incoming = areaObj.cars ?? areaObj.CARS ?? areaObj.Cars ?? [];
                        if (typeof incoming === 'string') {
                            try { incoming = JSON.parse(incoming); } catch (e) { incoming = []; }
                        }
                        if (!Array.isArray(incoming)) incoming = [];
                        if (areaJson) areaJson.value = JSON.stringify({
                            Nombre_Area: document.getElementById('area_name')?.value ?? '',
                            Descripcion_Area: document.getElementById('area_description')?.value ?? '',
                            Id_Maquila: document.getElementById('maquila_id')?.value ?? null,
                            cars: incoming
                        });
                        if (carCount) carCount.value = incoming.length;
                        // scroll al form
                        document.getElementById('areaForm')?.scrollIntoView({ behavior: 'smooth' });
                    };

                    // Editar: delega a loadAreaForEdit si existe, sino fallback
                    const handleEditarArea = btn => {
                        const raw = btn.getAttribute('data-area');
                        const areaObj = parseAreaData(raw);
                        const id = btn.getAttribute('data-id') || areaObj.Id_Area || areaObj.id || null;

                        if (typeof window.loadAreaForEdit === 'function') {
                            try {
                                window.loadAreaForEdit(areaObj, id);
                                return;
                            } catch (err) {
                                console.warn('loadAreaForEdit falló, usando fallback', err);
                            }
                        }
                        fallbackLoad(areaObj, id);
                    };

                    // Delegación global: cubre botones estáticos y dinámicos
                    document.addEventListener('click', e => {
                        const btn = e.target.closest('button');
                        if (!btn) return;
                        if (btn.classList.contains('mostrarCarsBtn')) { e.preventDefault(); handleMostrarCars(btn); }
                        if (btn.classList.contains('editarAreaBtn')) { e.preventDefault(); handleEditarArea(btn); }
                    });

                }); // DOMContentLoaded
            </script>

        </div>
        <!--AREAS/////////////////////////////-->

        <div id="div4" class="content-panel">
            <h1>Reportes</h1>

            <?php
            // Mensaje de notificación (si existe)
            if (isset($mensaje) && $mensaje) {
                echo '<div class="alert alert-info">' . htmlspecialchars($mensaje) . '</div>';
            }

            // --- Parámetros de la UI / filtros ---
            $limit = isset($_GET['limit']) ? max(1, (int) $_GET['limit']) : 25;

            $filtros = [];
            $maquilaId = isset($_GET['maquila']) ? (int) $_GET['maquila'] : null;
            $areaId = isset($_GET['area']) ? (int) $_GET['area'] : null;
            $userId = isset($_GET['usuario']) ? (int) $_GET['usuario'] : null;
            $estado = isset($_GET['estado']) ? trim($_GET['estado']) : null;
            $keyword = isset($_GET['keyword']) ? trim($_GET['keyword']) : null;

            if ($maquilaId)
                $filtros['id_maquila'] = $maquilaId;
            if ($areaId)
                $filtros['id_area'] = $areaId;
            if ($userId)
                $filtros['id_usuario'] = $userId;
            if ($estado)
                $filtros['estado'] = $estado;
            if ($keyword)
                $filtros['keyword'] = $keyword;

            // --- Helpers para normalizar respuestas de controllers ---
            $normalizeControllerRows = function ($res) {
                if ($res === null)
                    return [];
                if (is_array($res) && isset($res['success'])) {
                    return ($res['success'] === true && isset($res['data']) && is_array($res['data'])) ? $res['data'] : [];
                }
                if (is_array($res)) {
                    $isAssoc = array_keys($res) !== range(0, count($res) - 1);
                    return (!$isAssoc) ? $res : [];
                }
                return [];
            };

            // --- Cargar Catalogos ---
            // Maquilas
            $maquilas = [];
            try {
                if (isset($MaquilaController)) {
                    $mres = method_exists($MaquilaController, 'obtenerTodos') ? $MaquilaController->obtenerTodos() : $MaquilaController->obtenerPor('deleted_at', '');
                    $maquilas = $normalizeControllerRows($mres);
                }
            } catch (\Throwable $e) {
            }

            // Usuarios
            $usuarios = [];
            try {
                if (isset($UsuarioController)) {
                    $ures = method_exists($UsuarioController, 'obtenerTodos') ? $UsuarioController->obtenerTodos() : $UsuarioController->obtenerPor('deleted_at', '');
                    $usuarios = $normalizeControllerRows($ures);
                }
            } catch (\Throwable $e) {
            }

            // Areas (dependientes de maquila)
            $areasForMaquila = [];
            try {
                if ($maquilaId && isset($MaquilaAreaController)) {
                    $ares = $MaquilaAreaController->obtenerAreasPorMaquila($maquilaId);
                    $areasForMaquila = $normalizeControllerRows($ares);
                }
            } catch (\Throwable $e) {
            }


            // --- BUSQUEDA UNIFICADA ---
            $result = ['success' => false, 'data' => []];
            try {
                // Usamos el nuevo método buscar
                $result = $ReporteController->buscar($filtros, $limit);
            } catch (\Throwable $e) {
                $result = ['success' => false, 'error' => $e->getMessage()];
            }

            // Manejo de resultados
            if (!isset($result['success']) || $result['success'] !== true) {
                $err = $result['error'] ?? 'Error desconocido al obtener reportes.';
                echo '<div class="alert alert-danger">Error: ' . htmlspecialchars($err) . '</div>';
                $rows = [];
            } else {
                $rows = $result['data'];
            }
            ?>

            <!-- ESTILOS ADICIONALES PARA TABLA Y FILTROS -->
            <!-- FORMULARIO DE FILTROS -->
            <!-- FORMULARIO DE FILTROS -->
            <form id="filterForm" method="get" class="search-card">
                <div class="compact-filter-bar">
                    <div class="form-field keyword-field">
                        <label>Buscar palabra clave (ID, Nombre, Contenido...)</label>
                        <input type="text" name="keyword" value="<?= htmlspecialchars($keyword ?? '') ?>" placeholder="Ej: Fuga, OK, Juan..." />
                    </div>

                    <div class="form-field">
                        <label>Maquila</label>
                        <select id="maquilaSelect" name="maquila">
                            <option value="">-- Todas --</option>
                            <?php foreach ($maquilas as $m):
                                $mid = $m['Id_Maquila'] ?? $m['id'] ?? '';
                                $mname = $m['Nombre_Maquila'] ?? 'Maquila ' . $mid;
                                ?>
                                <option value="<?= htmlspecialchars($mid) ?>" <?= ($maquilaId == $mid) ? 'selected' : '' ?>>
                                    <?= htmlspecialchars($mname) ?>
                                </option>
                            <?php endforeach; ?>
                        </select>
                    </div>

                    <div class="form-field">
                        <label>Sistema</label>
                        <select id="areaSelect" name="area">
                            <option value="">-- Todos --</option>
                            <?php if (!empty($areasForMaquila)): ?>
                                <?php foreach ($areasForMaquila as $a):
                                    $aid = $a['Id_Area'] ?? $a['id'] ?? '';
                                    $aname = $a['Nombre_Area'] ?? 'Sistema ' . $aid;
                                    ?>
                                    <option value="<?= htmlspecialchars($aid) ?>" <?= ($areaId == $aid) ? 'selected' : '' ?>>
                                        <?= htmlspecialchars($aname) ?>
                                    </option>
                                <?php endforeach; ?>
                            <?php endif; ?>
                        </select>
                    </div>

                    <div class="form-field">
                        <label>Responsable</label>
                        <select name="usuario">
                            <option value="">-- Todos --</option>
                            <?php foreach ($usuarios as $u):
                                $uid = $u['Id_Usuario'] ?? $u['id'] ?? '';
                                $uname = $u['Nombre_Usuario'] ?? 'Usuario ' . $uid;
                                ?>
                                <option value="<?= htmlspecialchars($uid) ?>" <?= ($userId == $uid) ? 'selected' : '' ?>>
                                    <?= htmlspecialchars($uname) ?>
                                </option>
                            <?php endforeach; ?>
                        </select>
                    </div>

                    <div class="form-field">
                        <label>Estado</label>
                        <select name="estado">
                            <option value="">-- Todos --</option>
                            <option value="Abierto" <?= ($estado == 'Abierto') ? 'selected' : '' ?>>Abierto</option>
                            <option value="Cerrado" <?= ($estado == 'Cerrado') ? 'selected' : '' ?>>Cerrado</option>
                            <option value="Pendiente" <?= ($estado == 'Pendiente') ? 'selected' : '' ?>>Pendiente</option>
                            <option value="OK" <?= ($estado == 'OK') ? 'selected' : '' ?>>OK</option>
                            <option value="NOK" <?= ($estado == 'NOK') ? 'selected' : '' ?>>NOK</option>
                        </select>
                    </div>

                    <div class="form-field" style="flex: 1 1 80px; min-width: 80px;">
                        <label>Límite</label>
                        <input type="number" name="limit" value="<?= htmlspecialchars($limit) ?>" min="1" max="500">
                    </div>

                    <div class="filter-actions">
                        <button type="button" id="exportBtn" class="btn-secondary" style="color: #10b981; border-color: #10b981;">📊 Exportar</button>
                        <button type="submit" class="btn-primary">Filtrar</button>
                    </div>
                </div>
            </form>

            <script>
                document.getElementById('exportBtn').addEventListener('click', function () {
                    const form = document.getElementById('filterForm');
                    const params = new URLSearchParams(new FormData(form)).toString();
                    window.downloadExcelAsync('exportarReportes.php?' + params);
                });
            </script>

            <?php if (!empty($rows)): ?>

                <table class="report-table" id="reportTable">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Fecha</th>
                            <th>Maquila / Sistema</th>
                            <th>Responsable</th>
                            <th>Estado</th>
                            <th>Progreso CAR</th>
                            <th>Detalle</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($rows as $row):
                            $id = $row['Id_Reporte'] ?? '';
                            $fecha = $row['FechaRegistro_Reporte'] ?? '';
                            // Formatear fecha
                            $fechaFmt = date('d/m/Y H:i', strtotime($fecha));

                            $totalCAR = $row['CARTotal_Reporte'] ?? 0;
                            $revisadas = $row['CARRevisadas_Reporte'] ?? 0;
                            $progreso = ($totalCAR > 0) ? round(($revisadas / $totalCAR) * 100) . '%' : 'N/A';

                            $estadoR = $row['Estado_Reporte'] ?? 'Desconocido';
                            // Badge class logic
                            $badgeClass = 'badge-open';
                            if (stripos($estadoR, 'OK') !== false)
                                $badgeClass = 'badge-ok';
                            if (stripos($estadoR, 'NOK') !== false || stripos($estadoR, 'Error') !== false)
                                $badgeClass = 'badge-err';

                            $respName = $row['Resp_Nombre'] ?? 'N/A';

                            // Maquila/Area names (now available from join)
                            $mName = $row['Nombre_Maquila'] ?? '-';
                            $aName = $row['Nombre_Area'] ?? '-';

                            $jsonRaw = $row['JSON_Reporte'] ?? '';
                            $jsonB64 = base64_encode((string) $jsonRaw);
                            ?>
                            <tr>
                                <td><strong>#<?= htmlspecialchars($id) ?></strong></td>
                                <td><?= htmlspecialchars($fechaFmt) ?></td>
                                <td>
                                    <div style="font-weight:bold;"><?= htmlspecialchars($mName) ?></div>
                                    <div style="font-size:0.85em; color:#666;"><?= htmlspecialchars($aName) ?></div>
                                </td>
                                <td><?= htmlspecialchars($respName) ?></td>
                                <td><span class="badge <?= $badgeClass ?>"><?= htmlspecialchars($estadoR) ?></span></td>
                                <td>
                                    <?= htmlspecialchars($revisadas) ?> / <?= htmlspecialchars($totalCAR) ?>
                                    <small class="text-muted">(<?= $progreso ?>)</small>
                                </td>
                                <td>
                                    <button class="filter-btn view-json-btn" style="padding:4px 10px; font-size:0.8rem;"
                                        data-report-id="<?= htmlspecialchars($id) ?>" data-json="<?= $jsonB64 ?>">
                                        Ver Respuestas
                                    </button>
                                    <a href="exportarReportes.php?id_reporte_unico=<?= htmlspecialchars($id) ?>"
                                        class="filter-btn export-single-btn"
                                        style="padding:4px 10px; font-size:0.8rem; background-color: #28a745; text-decoration: none; display: inline-block;">
                                        Excel
                                    </a>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>

            <?php else: ?>
                <div class="alert alert-warning" style="margin-top:20px; text-align:center;">
                    No se encontraron reportes con los filtros seleccionados.
                </div>
            <?php endif; ?>

            <!-- Modal overlay (ya está en tu proyecto; si no, asegúrate de incluir rpOverlay markup) -->
            <div id="rpOverlay" class="rp-overlay" aria-hidden="true" style="display:none;">
                <div class="rp-modal" role="dialog" aria-modal="true" aria-labelledby="rpTitle">
                    <button id="rpClose" class="rp-close" title="Cerrar">&times;</button>
                    <h3 id="rpTitle">Detalle del reporte</h3>
                    <div id="rpContent" style="white-space:normal;"></div>
                </div>
            </div>

            <!-- Scripts: enviar el formulario automáticamente cuando cambie la maquila para poblar sistemas -->
            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    var maquilaSelect = document.getElementById('maquilaSelect');
                    if (maquilaSelect) {
                        maquilaSelect.addEventListener('change', function () {
                            // submit al cambiar maquila para que el servidor recargue y traiga las areas correspondientes
                            document.getElementById('filterForm').submit();
                        });
                    }

                    // ocultar/mostrar campos según modo seleccionado
                    var modeSelect = document.getElementById('modeSelect');
                    function updateVisibility() {
                        var v = modeSelect.value;
                        // mostramos los selects/inputs según el modo
                        // Para simplicidad: siempre mostramos todos; si prefieres ocultar algunos según modo, se puede ajustar.
                    }
                    if (modeSelect) {
                        modeSelect.addEventListener('change', function () { document.getElementById('filterForm').submit(); });
                    }
                });
            </script>

            <!-- incluir comportamiento del popout -->
            <script src="js/report_popout.js"></script>

        </div>



    </div>

    <script>
        function showDiv(divId) {
            // Ocultar todos los paneles
            document.querySelectorAll('.content-panel').forEach(panel => panel.style.display = 'none');

            // Quitar clase active de todos los botones
            document.querySelectorAll('.nav-menu .action-btn').forEach(btn => btn.classList.remove('active'));

            const el = document.getElementById(divId);
            if (el) el.style.display = 'block';

            // Agregar clase active al botón correspondiente
            // Mapeo simple: div1 -> primer btn, div2 -> segundo, etc.
            // O mejor, buscar por el onclick
            const buttons = document.querySelectorAll('.nav-menu .action-btn');
            buttons.forEach(btn => {
                if (btn.getAttribute('onclick').includes(divId)) {
                    btn.classList.add('active');
                }
            });

            try { localStorage.setItem('adminDiv', divId); } catch (e) { }
        }

        // Detectar si venimos de una busqueda de sistemas (GET)
        const params = new URLSearchParams(window.location.search);
        let initialDiv = 'div1';
        if (params.has('maquila_search_area') || params.get('view') === 'areas_search') {
            initialDiv = 'div3';
        } else if (params.has('maquila') || params.has('mode') || params.has('usuario')) {
            initialDiv = 'div4';
        }

        const saved = initialDiv !== 'div1' ? initialDiv : (localStorage.getItem('adminDiv') || 'div1');
        showDiv(saved);
    </script>
    <script src="js/excel_export.js"></script>
</body>

</html>