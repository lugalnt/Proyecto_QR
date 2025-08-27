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

$MaquilaController = new MaquilaController();
$UsuarioController = new UsuarioController();
$AreaController = new AreaController();
$MaquilaAreaController = new MaquilaAreaController();
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

//AREA/////////////////////////////////////////////



}
//AGARRES/////////////////////////////////////////
$usuarios = $UsuarioController->obtenerTodos();
$maquilas = $MaquilaController->obtenerTodos();
if(!empty($_SESSION['areasPorMaquila'])){$areasPorMaquila = $_SESSION['areasPorMaquila'];}
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
                                <button type="button" class="mostrarCarsBtn" data-area="<?= $areaJsonEscaped ?>">
                                    Mostrar CARS
                                </button>
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
