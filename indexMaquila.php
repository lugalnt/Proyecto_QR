<?php
session_start();
if (!isset($_SESSION['Id_Maquila'])) {
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

    //USUARIOS Y MAQUILAS ADMIN ELIMINADOS



    //MAQUILA//////////////////////////////////////////

    //AREA/////////////////////////////////////////////

    if (isset($_POST['Registrar_Area'])) {
        $permitidos = ["Nombre_Area", "Descripcion_Area", "NumeroCAR_Area", "JSON_Area"];
        $payload = array_intersect_key($_POST, array_flip($permitidos));
        try {
            $idArea = (int) $AreaController->registrar($payload);
            try {
                // Forzar uso de Maquila en Sesion
                $MaquilaAreaController->asignarMaquilaArea($_SESSION['Id_Maquila'], $idArea);
            } catch (Exception $e) {
                $mensaje = "❌ Error: " . $e->getMessage();
            }
            $_SESSION['mensaje'] = "✅ Area registrada con éxito";
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
            // Al editar, aseguramos que siga bajo la misma maquila (o se reasigna si se perdió)
            if (!empty($_SESSION['Id_Maquila']) && !empty($_POST['Id_Area'])) {
                try {
                    $MaquilaAreaController->asignarMaquilaArea((int) $_SESSION['Id_Maquila'], (int) $_POST['Id_Area']);
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
            <button class="action-btn" onclick="showDiv('div3')">📍 Area</button>
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

        <!-- USUARIOS y MAQUILAS Ocultos/Eliminados -->
        <div id="div1" class="content-panel" style="display:none;"></div>
        <div id="div2" class="content-panel" style="display:none;"></div>
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

                        <!-- Maquila fija por sesion -->
                        <input type="hidden" name="Id_Maquila" id="maquila_id" value="<?= $_SESSION['Id_Maquila'] ?>">
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
                                    <p class="muted">No hay propiedades agregadas (selecciona un tipo con los botones).
                                    </p>
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
                <br>
                <br>
                <!-- LISTADO AUTOMATICO DE AREAS DE LA MAQUILA -->
                <?php
                $areasPorMaquilaList = [];
                $laMaquilaBuscadaNombre = $_SESSION['Nombre_Maquila'] ?? 'Mi Maquila';

                try {
                    if (isset($MaquilaAreaController)) {
                        $areasPorMaquilaList = $MaquilaAreaController->obtenerAreasPorMaquila($_SESSION['Id_Maquila']);
                        if (isset($areasPorMaquilaList['success']))
                            $areasPorMaquilaList = $areasPorMaquilaList['data'] ?? [];
                    }
                } catch (\Throwable $e) {
                    echo "<div class='error'>Error al buscar áreas: " . $e->getMessage() . "</div>";
                }
                ?>

                <h3> Áreas Registradas </h3>

                <table class="report-table"> <!-- Reusamos estilo nuevo -->
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Descripción</th>
                            <th># CARs</th>
                            <th>Código QR</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php if (!empty($areasPorMaquilaList)): ?>
                            <?php foreach ($areasPorMaquilaList as $areaPorMaquila): ?>
                                <tr>
                                    <td><?= htmlspecialchars($areaPorMaquila['Id_Area']) ?></td>
                                    <td><?= htmlspecialchars($areaPorMaquila['Nombre_Area']) ?></td>
                                    <td><?= htmlspecialchars($areaPorMaquila['Descripcion_Area']) ?></td>
                                    <td>
                                        <?= htmlspecialchars($areaPorMaquila['NumeroCAR_Area']) ?>

                                        <?php
                                        // Decodificamos y re-encodeamos el JSON para asegurarlo
                                        $areaTmp = $areaPorMaquila['JSON_Area'] ?? '[]';
                                        $areaData = json_decode($areaTmp, true);
                                        if ($areaData === null)
                                            $areaData = [];
                                        // json_encode con flags
                                        $areaJsonEscaped = htmlspecialchars(json_encode($areaData, JSON_HEX_TAG | JSON_HEX_APOS | JSON_HEX_QUOT | JSON_HEX_AMP), ENT_QUOTES, 'UTF-8');
                                        ?>

                                        <!-- botón que abre el popup; data-area contiene el JSON escapado -->
                                        <button type="button" class="mostrarCarsBtn" data-area="<?= $areaJsonEscaped ?>">Mostrar
                                            CARS</button>

                                        <button type="button" class="editarAreaBtn" data-area="<?= $areaJsonEscaped ?>"
                                            data-id="<?= htmlspecialchars($areaPorMaquila['Id_Area']) ?>">Editar</button>

                                    </td>
                                    <td style="text-align:center;">
                                        <?php if (!empty($areaPorMaquila['Codigo_Area'])): ?>
                                            <a href="qrcodes/<?= htmlspecialchars($areaPorMaquila['Codigo_Area']) ?>.png"
                                                download="CodigoQR_Area<?= htmlspecialchars($areaPorMaquila['Nombre_Area']) ?>.png">
                                                <img style="max-width:80px; max-height: 80px"
                                                    src="qrcodes/<?= htmlspecialchars($areaPorMaquila['Codigo_Area']) ?>.png" />
                                            </a>
                                        <?php else: ?>
                                            <span style="color:gray;">Sin QR</span>
                                        <?php endif; ?>
                                    </td>
                                </tr>
                            <?php endforeach; ?>
                        <?php else: ?>
                            <tr>
                                <td colspan="5">No hay areas para mostrar o no has buscado.</td>
                            </tr>
                        <?php endif; ?>
                    </tbody>
                </table>



            </div>
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
                        const submit = document.getElementById('submitBtn'); if (submit) submit.textContent = 'Actualizar Area';
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

            // --- Parámetros de la UI / filtros ---
            $limit = isset($_GET['limit']) ? max(1, (int) $_GET['limit']) : 25;

            $filtros = [];
            // Forzar maquila de sesion
            $maquilaId = $_SESSION['Id_Maquila'];
            $areaId = isset($_GET['area']) ? (int) $_GET['area'] : null;
            $userId = isset($_GET['usuario']) ? (int) $_GET['usuario'] : null;
            $estado = isset($_GET['estado']) ? trim($_GET['estado']) : null;

            if ($maquilaId)
                $filtros['id_maquila'] = $maquilaId;
            if ($areaId)
                $filtros['id_area'] = $areaId;
            if ($userId)
                $filtros['id_usuario'] = $userId;
            if ($estado)
                $filtros['estado'] = $estado;


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
            <form id="filterForm" method="get" class="filters-container">

                <!-- Maquila Filter Removed for Maquila Dashboard -->


                <div class="filter-group">
                    <label>Área</label>
                    <select id="areaSelect" name="area">
                        <option value="">-- Todas --</option>
                        <?php if (!empty($areasForMaquila)): ?>
                            <?php foreach ($areasForMaquila as $a):
                                $aid = $a['Id_Area'] ?? $a['id'] ?? '';
                                $aname = $a['Nombre_Area'] ?? 'Area ' . $aid;
                                ?>
                                <option value="<?= htmlspecialchars($aid) ?>" <?= ($areaId == $aid) ? 'selected' : '' ?>>
                                    <?= htmlspecialchars($aname) ?>
                                </option>
                            <?php endforeach; ?>
                        <?php endif; ?>
                    </select>
                </div>

                <div class="filter-group">
                    <label>Usuario (Responsable)</label>
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

                <div class="filter-group">
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

                <div class="filter-group">
                    <label>Límite</label>
                    <input type="number" name="limit" value="<?= htmlspecialchars($limit) ?>" min="1" max="500"
                        style="width: 80px;">
                </div>

                <div class="filter-group" style="justify-content: flex-end;">
                    <button type="submit" class="filter-btn">Filtrar Reportes</button>
                </div>
            </form>

            <?php if (!empty($rows)): ?>

                <table class="report-table" id="reportTable">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Fecha</th>
                            <th>Maquila / Área</th>
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

            <!-- Scripts: enviar el formulario automáticamente cuando cambie la maquila para poblar áreas -->
            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    /* Maquila select removed
                    var maquilaSelect = document.getElementById('maquilaSelect');
                    if (maquilaSelect) {
                        maquilaSelect.addEventListener('change', function () {
                            document.getElementById('filterForm').submit();
                        });
                    }
                    */

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
            const buttons = document.querySelectorAll('.nav-menu .action-btn');
            buttons.forEach(btn => {
                if (btn.getAttribute('onclick').includes(divId)) {
                    btn.classList.add('active');
                }
            });

            try { localStorage.setItem('adminDiv', divId); } catch (e) { }
        }

        // Detectar si venimos de una busqueda de areas (GET)
        const params = new URLSearchParams(window.location.search);
        let initialDiv = 'div3';
        if (params.has('maquila_search_area') || params.get('view') === 'areas_search') {
            initialDiv = 'div3';
        } else if (params.has('maquila') || params.has('mode') || params.has('usuario')) {
            initialDiv = 'div4';
        }

        const saved = initialDiv !== 'div3' ? initialDiv : (localStorage.getItem('adminDiv') || 'div3');
        showDiv(saved);
    </script>
</body>

</html>