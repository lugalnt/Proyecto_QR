<?php
session_start();
if (!isset($_SESSION['Id_Usuario'])) {
    header("Location: login.php");
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

//AREA/////////////////////////////////////////////

    if (isset($_POST['Registrar_Area'])){
        $permitidos = ["Nombre_Area","Descripcion_Area","NumeroCAR_Area","Descripcion_Area","JSON_Area"];
        $payload = array_intersect_key($_POST, array_flip($permitidos));
        try {
           $idArea = (int) $AreaController->registrar($payload);
                try{
                    $MaquilaAreaController->asignarMaquilaArea($_POST['Id_Maquila'], $idArea);
                } 
                catch (Exception $e) {
                    $mensaje = "❌ Error: " . $e->getMessage();
                }
            $_SESSION['mensaje'] = "✅ Area registrada con éxito";
            header("Location: " . $_SERVER['PHP_SELF']);
            exit();
        } catch (Exception $e) {
            $mensaje = "❌ Error: " . $e->getMessage();
        }
    }

    if(isset($_POST['Area_BuscarPorMaquila'])){
        $idMaquila = $_POST['Id_Maquila'];

        try {
                $areasPorMaquila = $MaquilaAreaController->obtenerAreasPorMaquila($idMaquila);
                $_SESSION['areasPorMaquila'] = $areasPorMaquila;
                $_SESSION['areasPorMaquilaQueMaquila'] = $idMaquila;
                $_SESSION['mensaje'] = "✅ Areas de maquila econtradas con éxito";
                header("Location: " . $_SERVER['PHP_SELF']);
                exit();
        } catch (Exception $e) {
                $mensaje = "❌ Error: " . $e->getMessage();
        }
    }

    if (isset($_POST['Editar_Area']) && $_POST['Editar_Area'] == '1'){
            // campos permitidos para update (incluye Id_Area)
        $permitidos = ["Id_Area","Nombre_Area","Descripcion_Area","NumeroCAR_Area","JSON_Area","Id_Maquila"];
        $payload = array_intersect_key($_POST, array_flip($permitidos));
        try {
        $updated = $AreaController->editarArea($payload);
        // si manejas asignación de maquila con controlador aparte:
        if (!empty($_POST['Id_Maquila']) && !empty($_POST['Id_Area'])) {
            try {
                $MaquilaAreaController->asignarMaquilaArea((int)$_POST['Id_Maquila'], (int)$_POST['Id_Area']);
            } catch (Exception $e) {
                // manejar error de asignación, pero la edición ya fue hecha
                $mensaje = "❌ Error asignando maquila: " . $e->getMessage();
            }
        }
        $_SESSION['mensaje'] = "✅ Area actualizada con éxito";
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
if(!empty($_SESSION['areasPorMaquila'])){$areasPorMaquila = $_SESSION['areasPorMaquila'];}
if(!isset($_SESSION['areasPorMaquilaQueMaquila'])){$_SESSION['areasPorMaquilaQueMaquila'] = 0;}
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
            <button class="action-btn" onclick="showDiv('div3')">Area</button>
            <button class="action-btn" onclick="showDiv('div4')">Reportes</button>
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
                  <h1>Area</h1>

                    <main class="container">
              <form id="areaForm" method="post" action="">

                <div class="row top">
                  <label class="field">
                    <span>Nombre Area:</span>
                    <input type="text" name="Nombre_Area" id="area_name" required />
                  </label>

                  <label class="field field-wide">
                    <span>Descripcion area:</span>
                    <textarea name="Descripcion_Area" id="area_description" rows="3"></textarea>
                  </label>

                  <label class="field">
                    <span>Maquila del Area</span>
                    <select name="Id_Maquila" id="maquila_id" required>
                        <?php if ($maquilas): ?>
                            <?php foreach ($maquilas as $maquila): ?>
                                <option value="<?= htmlspecialchars($maquila['Id_Maquila']) ?>"><?= htmlspecialchars($maquila['Nombre_Maquila'])  ?></option>

                            <?php endforeach; ?>
                        <?php endif; ?>
                    </select>
                  </label>
                </div>

                <hr />

                <section class="car-section">
                  <h2>Agregar C.A.R (Cosas A Revisar)</h2>

                  <div class="car-left">
                    <label class="field">
                      <span>Nombre C.A.R</span>
                      <input type="text" id="car_name_input" placeholder="Ej: Valvula principal" />
                    </label>

                    <div class="prop-buttons">
                      <button type="button" class="prop-btn" data-type="bool">Ok/ No Ok</button>
                      <button type="button" class="prop-btn" data-type="range">Rango</button>
                      <button type="button" class="prop-btn" data-type="number">Numero</button>
                      <button type="button" class="prop-btn" data-type="text">Descripcion</button>
                      <button type="button" class="prop-btn" data-type="date">Fecha</button>
                    </div>

                    <div class="editor" id="propEditor" aria-hidden="true">
                      <h3 id="editorTitle">Añadir propiedad</h3>
                      <label class="field small">
                        <span>Nombre de propiedad</span>
                        <input type="text" id="prop_label" placeholder="Ej: Funcionamiento" />
                      </label>
                      <div id="typeSettings"></div>

                      <div class="editor-actions">
                        <button type="button" id="addPropertyBtn">Agregar propiedad</button>
                        <button type="button" id="cancelPropertyBtn" class="secondary">Cancelar</button>
                      </div>
                    </div>

                  </div>

                  <div class="car-right">
                    <div class="card">
                      <h4>Propiedades añadidas.</h4>
                      <div id="currentPropsContainer">
                        <p class="muted">No hay propiedades agregadas (selecciona un tipo con los botones).</p>
                      </div>

                    </div>

                    <div class="card">
                      <h4>Listado de C.A.R agregados</h4>
                      <div id="carsList"></div>

                      <div class="add-car-row">
                        <button type="button" id="addCarBtn">Agregar C.A.R</button>
                      </div>
                    </div>

                  </div>
                </section>

                <hr />

                <div class="final-row">
                  <input type="hidden" name="Editar_Area" id="editar_area_input" value="0" />
                  <input type="hidden" name="Id_Area" id="area_id_input" />
                  <input type="hidden" name="JSON_Area" id="area_json" />
                  <input type="hidden" name="NumeroCAR_Area" id="car_count_input" value="0" />
                  <input type="hidden" name="Registrar_Area" value="1" />
                  <button type="submit" id="submitBtn">Finalizar y Registrar Area</button>
                </div>
              </form>
            </main>

            <script src="js/area_form.js"></script>

            <br>
            <div>
                <form method="post">
                <select name="Id_Maquila" id="selectMaquila" required>
                    <?php if ($maquilas): ?>
                        <?php foreach ($maquilas as $maquila): ?>
                            <option value="<?= htmlspecialchars($maquila['Id_Maquila']) ?>"><?= htmlspecialchars($maquila['Nombre_Maquila'])  ?></option>
                        <?php endforeach; ?>
                    <?php endif; ?>
                </select>
                <input type="hidden" name="Area_BuscarPorMaquila"/>
                <button type="submit"> Buscar </button>
                </form>
                <br>
                <?php
                if(!empty($_SESSION['areasPorMaquilaQueMaquila'])){
                    $queMaquilas = $MaquilaController->obtenerPor('Id_Maquila', $_SESSION['areasPorMaquilaQueMaquila']);
                    $laMaquilaBuscada = $queMaquilas[0];
                }

                ?>
                <h2> Areas de la maquila: <?= $laMaquilaBuscada['Nombre_Maquila'] ?> </h2>
                <table>
                <thead>
                    <tr>
                        <th>ID del Area</th>
                        <th>Nombre del Area</th>
                        <th>Descripcion del Area</th>
                        <th>Numero del C.A.R del Area</th>
                        <th>Codigo QR del Area</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if (!empty($areasPorMaquila)): ?>
                        <?php foreach ($areasPorMaquila as $areaPorMaquila): ?>
                            <tr>
                                <td><?= htmlspecialchars($areaPorMaquila['Id_Area']) ?></td>
                                <td><?= htmlspecialchars($areaPorMaquila['Nombre_Area']) ?></td>
                                <td><?= htmlspecialchars($areaPorMaquila['Descripcion_Area']) ?></td>
                                <td>
                                <?= htmlspecialchars($areaPorMaquila['NumeroCAR_Area']) ?>
                                
                                <?php
                                    // Decodificamos y re-encodeamos el JSON para asegurarlo
                                    $areaData = json_decode($areaPorMaquila['JSON_Area'], true);
                                    if ($areaData === null) $areaData = [];
                                    // json_encode con flags y luego escapar para atributo HTML
                                    $areaJsonEscaped = htmlspecialchars(json_encode($areaData, JSON_HEX_TAG|JSON_HEX_APOS|JSON_HEX_QUOT|JSON_HEX_AMP), ENT_QUOTES, 'UTF-8');
                                ?>

                                <!-- botón que abre el popup; data-area contiene el JSON escapado -->
                                <button type="button" class="mostrarCarsBtn" data-area="<?= $areaJsonEscaped ?>">Mostrar CARS</button>

                                <button type="button"class="editarAreaBtn"data-area="<?= $areaJsonEscaped ?>"data-id="<?= htmlspecialchars($areaPorMaquila['Id_Area']) ?>">Editar</button>

                                </td>
                                <td style="max-width:150px; max-height: 150px">
                                <a href="qrcodes/<?= htmlspecialchars($areaPorMaquila['Codigo_Area']) ?>.png" download="CodigoQR_Area<?= htmlspecialchars($areaPorMaquila['Nombre_Area']) ?>.png">
                                <img style="max-width:150px; max-height: 150px" src="qrcodes/<?= htmlspecialchars($areaPorMaquila['Codigo_Area']) ?>.png"/>
                                </a>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <tr><td colspan="3">No hay areas o no has buscado aun.</td></tr>
                    <?php endif; ?>
                </tbody>
                </table>            

            </div>

            <script src="js/popupCars.js"></script>

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
            const submit = document.getElementById('submitBtn'); if (submit) submit.textContent = 'Actualizar Area';
            // actualizar hidden JSON y contador (si no existe UI para cars)
            const areaJson = document.getElementById('area_json');
            const carCount = document.getElementById('car_count_input');
            let incoming = areaObj.cars ?? areaObj.CARS ?? areaObj.Cars ?? [];
            if (typeof incoming === 'string') {
            try { incoming = JSON.parse(incoming); } catch(e) { incoming = []; }
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

  



 
            
            <!-- Script para encganchar los botonos, pero para editar -->







        </div>
        <!--AREAS/////////////////////////////-->

        <div id="div4" class="content-panel">
            <h1>Reportes</h1>

            <?php
            // Mensaje de notificación (si existe)
            if (isset($mensaje) && $mensaje) {
                echo '<div class="alert alert-info">' . htmlspecialchars($mensaje) . '</div>';
            }

            // --- Parámetros de la UI / filtros (vienen por GET al recargar) ---
            $mode = $_GET['mode'] ?? 'latest'; // latest, maquila, usuario, estado
            $limit = isset($_GET['limit']) ? max(1, (int)$_GET['limit']) : 10;
            $maquilaId = isset($_GET['maquila']) ? (int)$_GET['maquila'] : null;
            $areaId = isset($_GET['area']) ? (int)$_GET['area'] : null;
            $userId = isset($_GET['usuario']) ? (int)$_GET['usuario'] : null;
            $estado = isset($_GET['estado']) ? trim($_GET['estado']) : null;

            // --- Helpers para normalizar respuestas de controllers ---
            $normalizeControllerRows = function($res) {
                // acepta: array of rows OR ['success'=>true,'data'=>rows] OR ['success'=>false,'error'=>..]
                if ($res === null) return [];
                if (is_array($res) && isset($res['success'])) {
                    if ($res['success'] === true && isset($res['data']) && is_array($res['data'])) {
                        return $res['data'];
                    } else {
                        return []; // fallo o no hay datos
                    }
                }
                if (is_array($res)) {
                    // si es lista de filas (indexadas) devolvemos tal cual
                    $isAssoc = array_keys($res) !== range(0, count($res)-1);
                    if (!$isAssoc) return $res;
                    // si es asociativo y contiene filas dentro, fallback vacío
                    return [];
                }
                return [];
            };

            // --- Intentar cargar maquilas (varias estrategias) ---
            $maquilas = [];
            try {
                if (isset($MaquilaController) && is_object($MaquilaController)) {
                    if (method_exists($MaquilaController, 'obtenerTodos')) {
                        $mres = $MaquilaController->obtenerTodos();
                        $maquilas = $normalizeControllerRows($mres);
                    } elseif (method_exists($MaquilaController, 'obtenerPor')) {
                        // intento razonable: obtener filas con deleted_at = NULL no es soportado, intentar campo 'deleted_at' = ''
                        $mres = $MaquilaController->obtenerPor('deleted_at', '');
                        $maquilas = $normalizeControllerRows($mres);
                        // si no hay resultado, intentar obtener por 'Id_Maquila' = 0 (probablemente vacio)
                        if (empty($maquilas)) {
                            // do nothing, leave empty
                        }
                    }
                }
            } catch (\Throwable $e) {
                $maquilas = [];
            }

            // --- Intentar cargar usuarios (para el select) ---
            $usuarios = [];
            try {
                if (isset($UsuarioController) && is_object($UsuarioController)) {
                    if (method_exists($UsuarioController, 'obtenerTodos')) {
                        $ures = $UsuarioController->obtenerTodos();
                        $usuarios = $normalizeControllerRows($ures);
                    } elseif (method_exists($UsuarioController, 'obtenerPor')) {
                        // Intentar obtener usuarios activos por 'deleted_at' = ''
                        $ures = $UsuarioController->obtenerPor('deleted_at', '');
                        $usuarios = $normalizeControllerRows($ures);
                    }
                }
            } catch (\Throwable $e) {
                $usuarios = [];
            }

            // --- Intentar cargar áreas para la maquila seleccionada (si hay maquila) ---
            $areasForMaquila = [];
            try {
                if ($maquilaId && isset($MaquilaAreaController) && is_object($MaquilaAreaController) && method_exists($MaquilaAreaController, 'obtenerAreasPorMaquila')) {
                    $ares = $MaquilaAreaController->obtenerAreasPorMaquila($maquilaId);
                    $areasForMaquila = $normalizeControllerRows($ares);
                } elseif ($areaId && isset($AreaController) && is_object($AreaController) && method_exists($AreaController, 'obtenerPor')) {
                    // si se cargó un areaId pero no hay maquila, intentar traer solo esa area para mostrarla en select
                    $a = $AreaController->obtenerPor('Id_Area', $areaId);
                    $areasForMaquila = $normalizeControllerRows($a);
                }
            } catch (\Throwable $e) {
                $areasForMaquila = [];
            }

            // --- Determinar qué llamada al ReporteController hacemos según modo ---
            $result = null;
            try {
                if ($mode === 'maquila' && $maquilaId) {
                    // pasar $areaId si fue seleccionado (la versión del controller acepta areaId opcional)
                    // Si tu controller anterior no tenía areaId, la llamada seguirá funcionando sin él (depende de la implementación).
                    // Para compatibilidad, detectamos si el método acepta 3 parámetros.
                    $ref = new ReflectionMethod($ReporteController, 'getByMaquila');
                    $params = $ref->getNumberOfParameters();
                    if ($params >= 3) {
                        $result = $ReporteController->getByMaquila($maquilaId, $areaId ?: null, $limit);
                    } else {
                        // Llamada legacy: solo maquilaId y limit
                        $result = $ReporteController->getByMaquila($maquilaId, $limit);
                    }
                } elseif ($mode === 'usuario' && $userId) {
                    $result = $ReporteController->getByUsuario($userId, $limit);
                } elseif ($mode === 'estado' && $estado !== null) {
                    $result = $ReporteController->getByEstado($estado, $limit);
                } else {
                    // default últimos
                    $result = $ReporteController->getLatest($limit);
                }
            } catch (\Throwable $e) {
                $result = ['success' => false, 'error' => $e->getMessage()];
            }

            // Manejo de errores
            if (!isset($result['success']) || $result['success'] !== true) {
                $err = $result['error'] ?? 'Error desconocido al obtener reportes.';
                echo '<div class="alert alert-danger">Error: ' . htmlspecialchars($err) . '</div>';
                $rows = [];
            } else {
                $rows = $result['data'];
            }
            ?>

            <!-- FILTROS -->
            <form id="filterForm" method="get" style="margin-bottom:1rem;">
                <label>
                    Ver:
                    <select id="modeSelect" name="mode">
                        <option value="latest" <?php if($mode==='latest') echo 'selected'; ?>>Últimos</option>
                        <option value="maquila" <?php if($mode==='maquila') echo 'selected'; ?>>Por Maquila</option>
                        <option value="usuario" <?php if($mode==='usuario') echo 'selected'; ?>>Por Usuario</option>
                        <option value="estado" <?php if($mode==='estado') echo 'selected'; ?>>Por Estado</option>
                    </select>
                </label>

                <label style="margin-left:0.5rem;">
                    Limit:
                    <input type="number" name="limit" value="<?php echo htmlspecialchars($limit); ?>" min="1" max="500" style="width:70px;">
                </label>

                <!-- MAQUILA select (si no logramos obtener maquilas, se muestra input) -->
                <span id="maquilaContainer" style="margin-left:0.5rem;">
                    <?php if (!empty($maquilas)): ?>
                        <label>Maquila:
                            <select id="maquilaSelect" name="maquila">
                                <option value="">--Seleccionar--</option>
                                <?php foreach ($maquilas as $m): 
                                    // columnas comunes: Id_Maquila, Nombre_Maquila
                                    $mid = $m['Id_Maquila'] ?? $m['id'] ?? $m['Id'] ?? '';
                                    $mname = $m['Nombre_Maquila'] ?? $m['Nombre'] ?? $m['name'] ?? ('Maquila ' . $mid);
                                ?>
                                    <option value="<?php echo htmlspecialchars($mid); ?>" <?php if($maquilaId && $maquilaId == $mid) echo 'selected'; ?>><?php echo htmlspecialchars((string)$mname); ?></option>
                                <?php endforeach; ?>
                            </select>
                        </label>
                    <?php else: ?>
                        <label>Id Maquila:
                            <input type="number" name="maquila" value="<?php echo htmlspecialchars($maquilaId ?? ''); ?>" style="width:100px;">
                        </label>
                    <?php endif; ?>
                </span>

                <!-- AREA select (populado si hay maquila y areasForMaquila) -->
                <span id="areaContainer" style="margin-left:0.5rem;">
                    <?php if (!empty($areasForMaquila)): ?>
                        <label>Área:
                            <select id="areaSelect" name="area">
                                <option value="">--Todas--</option>
                                <?php foreach ($areasForMaquila as $a):
                                    $aid = $a['Id_Area'] ?? $a['id'] ?? $a['Id'] ?? '';
                                    $aname = $a['Nombre_Area'] ?? $a['Nombre'] ?? $a['name'] ?? ('Área ' . $aid);
                                ?>
                                    <option value="<?php echo htmlspecialchars($aid); ?>" <?php if($areaId && $areaId == $aid) echo 'selected'; ?>><?php echo htmlspecialchars((string)$aname); ?></option>
                                <?php endforeach; ?>
                            </select>
                        </label>
                    <?php else: ?>
                        <label>Área:
                            <input type="number" name="area" value="<?php echo htmlspecialchars($areaId ?? ''); ?>" style="width:100px;">
                        </label>
                    <?php endif; ?>
                </span>

                <!-- USUARIO select (si no disponible, mostrar input) -->
                <span id="usuarioContainer" style="margin-left:0.5rem;">
                    <?php if (!empty($usuarios)): ?>
                        <label>Usuario:
                            <select name="usuario">
                                <option value="">--Todos--</option>
                                <?php foreach ($usuarios as $u):
                                    $uid = $u['Id_Usuario'] ?? $u['id'] ?? $u['Id'] ?? '';
                                    $uname = $u['Nombre_Usuario'] ?? $u['Nombre'] ?? $u['name'] ?? ('Usuario ' . $uid);
                                ?>
                                    <option value="<?php echo htmlspecialchars($uid); ?>" <?php if($userId && $userId == $uid) echo 'selected'; ?>><?php echo htmlspecialchars((string)$uname); ?></option>
                                <?php endforeach; ?>
                            </select>
                        </label>
                    <?php else: ?>
                        <label>Id Usuario:
                            <input type="number" name="usuario" value="<?php echo htmlspecialchars($userId ?? ''); ?>" style="width:100px;">
                        </label>
                    <?php endif; ?>
                </span>

                <!-- ESTADO -->
                <span id="estadoContainer" style="margin-left:0.5rem;">
                    <label>Estado:
                        <input type="text" name="estado" value="<?php echo htmlspecialchars($estado ?? ''); ?>" style="width:140px;">
                    </label>
                </span>

                <button type="submit" style="margin-left:0.5rem;">Aplicar</button>
            </form>

            <?php if (!empty($rows)): ?>

            <table class="report-table" id="reportTable">
                <thead>
                    <tr>
                        <th>Id</th>
                        <th>Fecha Registro</th>
                        <th>Fecha Modificación</th>
                        <th>Total CAR</th>
                        <th>CAR Revisadas</th>
                        <th>Estado</th>
                        <th>Responsable Id</th>
                        <th>Responsable Nombre</th>
                        <th>JSON (detalle)</th>
                    </tr>
                </thead>
                <tbody>
                    <?php foreach ($rows as $row):
                        $id = $row['Id_Reporte'] ?? $row['id'] ?? '';
                        $fecha = $row['FechaRegistro_Reporte'] ?? '';
                        $fechaMod = $row['FechaModificacion_Reporte'] ?? '';
                        $totalCAR = $row['CARTotal_Reporte'] ?? '';
                        $revisadas = $row['CARRevisadas_Reporte'] ?? '';
                        $estadoR = $row['Estado_Reporte'] ?? '';
                        $respId = $row['Resp_Id_Usuario'] ?? $row['Id_Usuario'] ?? '';
                        $respName = $row['Resp_Nombre'] ?? $row['Nombre_Usuario'] ?? '';
                        $jsonRaw = $row['JSON_Reporte'] ?? '';
                        $jsonB64 = base64_encode((string)$jsonRaw);
                    ?>
                    <tr>
                        <td><?php echo htmlspecialchars((string)$id); ?></td>
                        <td><?php echo htmlspecialchars((string)$fecha); ?></td>
                        <td><?php echo htmlspecialchars((string)$fechaMod); ?></td>
                        <td><?php echo htmlspecialchars((string)$totalCAR); ?></td>
                        <td><?php echo htmlspecialchars((string)$revisadas); ?></td>
                        <td><?php echo htmlspecialchars((string)$estadoR); ?></td>
                        <td><?php echo htmlspecialchars((string)$respId); ?></td>
                        <td><?php echo htmlspecialchars((string)$respName); ?></td>
                        <td>
                            <button class="btn-small view-json-btn" data-report-id="<?php echo htmlspecialchars((string)$id); ?>" data-json="<?php echo $jsonB64; ?>">Ver respuestas</button>
                        </td>
                    </tr>
                    <?php endforeach; ?>
                </tbody>
            </table>

            <?php else: ?>
                <div class="alert alert-warning">No se encontraron reportes para estos criterios.</div>
            <?php endif; ?>

            <!-- Modal overlay (ya está en tu proyecto; si no, asegúrate de incluir rpOverlay markup) -->
            <div id="rpOverlay" class="rp-overlay" aria-hidden="true" style="display:none;">
                <div class="rp-modal" role="dialog" aria-modal="true" aria-labelledby="rpTitle">
                    <button id="rpClose" class="rp-close" title="Cerrar">&times;</button>
                    <h3 id="rpTitle">Detalle del reporte</h3>
                    <div id="rpContent" style="white-space:normal;"></div>
                </div>
            </div>

            <!-- Scripts: enviar el formulario automáticamente cuando cambie la maquila para poblar áreas -->
            <script>
            document.addEventListener('DOMContentLoaded', function(){
                var maquilaSelect = document.getElementById('maquilaSelect');
                if (maquilaSelect) {
                    maquilaSelect.addEventListener('change', function(){
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
                    modeSelect.addEventListener('change', function(){ document.getElementById('filterForm').submit(); });
                }
            });
            </script>

            <!-- incluir comportamiento del popout -->
            <script src="js/report_popout.js"></script>

        </div>


        
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
