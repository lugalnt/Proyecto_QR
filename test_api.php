<?php
// test_api.php
require_once 'controllers/maquilaController.php';
require_once 'controllers/areaController.php';
require_once 'controllers/maquilaareaController.php';

$maquilaCtrl = new MaquilaController();
$areaCtrl = new AreaController();
$maAreaCtrl = new MaquilaAreaController();

echo "--- Iniciando Pruebas de Lógica de Seguridad ---\n";

// 1. Obtener una maquila y sus areas
$maquilas = $maquilaCtrl->obtenerTodos();
if (empty($maquilas)) {
    die("No hay maquilas para probar.\n");
}

$maquila = $maquilas[0];
$idMaquila = $maquila['Id_Maquila'];
echo "Maquila de prueba: ID {$idMaquila} ({$maquila['Nombre_Maquila']})\n";

// Obtener areas de esa maquila
$areas = $maAreaCtrl->obtenerAreasPorMaquila($idMaquila);
if (empty($areas)) {
    die("La maquila no tiene areas asociadas para probar relacion positiva.\n");
}

$areaPropia = $areas[0];
$idAreaPropia = $areaPropia['Id_Area'];
echo "Area propia: ID {$idAreaPropia} ({$areaPropia['Nombre_Area']})\n";

// 2. Probar Relación Positiva
$check1 = $maAreaCtrl->verificarRelacion($idMaquila, $idAreaPropia);
echo "TEST 1: verificarRelacion($idMaquila, $idAreaPropia) [Esperado: TRUE] -> Resultado: " . ($check1 ? "TRUE" : "FALSE") . "\n";

// 3. Probar Relación Negativa
// Buscamos un area que NO este en $areas
$todasLasAreas = $areaCtrl->obtenerTodos();
$idAreaAjena = null;
$idsPropios = array_column($areas, 'Id_Area');

foreach ($todasLasAreas as $a) {
    if (!in_array($a['Id_Area'], $idsPropios)) {
        $idAreaAjena = $a['Id_Area'];
        echo "Area ajena encontrada: ID {$idAreaAjena} ({$a['Nombre_Area']})\n";
        break;
    }
}

if ($idAreaAjena) {
    $check2 = $maAreaCtrl->verificarRelacion($idMaquila, $idAreaAjena);
    echo "TEST 2: verificarRelacion($idMaquila, $idAreaAjena) [Esperado: FALSE] -> Resultado: " . ($check2 ? "TRUE" : "FALSE") . "\n";
} else {
    echo "TEST 2: No se encontro area ajena para probar (puedes crear una dummy).\n";
}

// 4. Probar la API simulada
// Simulamos la lógica de get_reports_by_area.php
echo "\n--- Simulacion API ---\n";

function simularApi($idMaquilaInput, $idAreaInput, $maAreaCtrl)
{
    if ($idMaquilaInput) {
        $esValido = $maAreaCtrl->verificarRelacion($idMaquilaInput, $idAreaInput);
        if (!$esValido) {
            return "Acceso denegado";
        }
    }
    return "Acceso permitido (Query normal)";
}

// Caso A: Petición sin id_maquila (Legacy / Admin)
echo "Caso API Legacy (sin id_maquila): " . simularApi(null, $idAreaPropia, $maAreaCtrl) . "\n";

// Caso B: Petición con id_maquila CORRECTO
echo "Caso API Segura (id_maquila correcto): " . simularApi($idMaquila, $idAreaPropia, $maAreaCtrl) . "\n";

// Caso C: Petición con id_maquila INCORRECTO
if ($idAreaAjena) {
    echo "Caso API Segura (id_maquila incorrecto): " . simularApi($idMaquila, $idAreaAjena, $maAreaCtrl) . "\n";
}

echo "--- Fin de Pruebas ---\n";
