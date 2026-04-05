<?php

require_once __DIR__ . '/../../services/excel/ExcelExporter.php';
use Services\Excel\ExcelExporter;

$outDir = __DIR__ . '/results';
if (!is_dir($outDir)) {
    mkdir($outDir, 0777, true);
}

// Helper para crear estructura de reporte
function createBaseReport($carReportsArray) {
    return [
        'Nombre_Area' => 'Test Area',
        'Nombre_Maquila' => 'Test Maquila',
        'Resp_Nombre' => 'Ing_Pruebas',
        'FechaRegistro_Reporte' => date('Y-m-d'),
        'JSON_Reporte' => json_encode(['car_reports' => $carReportsArray])
    ];
}

echo "========================================\n";
echo "Corriendo tests del exportador Excel...\n";
echo "========================================\n\n";

try {
    // TEST 1: Booleanos
    $carBoolean = [
        'car_name' => 'Sistema Valvulas',
        'responses' => [
            'Valvula 1 (Debe ser SI -> OK)' => true,
            'Valvula 2 (Debe ser NO -> Tache)' => false
        ]
    ];
    ExcelExporter::exportReports([createBaseReport([$carBoolean])], $outDir . '/test_1_booleanos.xlsx');
    echo "[OK] test_1_booleanos.xlsx generado.\n";

} catch (\Exception $e) {
    echo "[ERROR] Test 1 falló: " . $e->getMessage() . "\n";
}

try {
    // TEST 2: Incidencias
    $carIncidents = [
        'car_name' => 'Bomba Contra Incendios',
        'responses' => [
            'Presion' => '120 PSI',
            'Nivel Aceite' => 'Correcto'
        ],
        'observacion' => 'Mantenimiento mensual regular.',
        'incidencia' => 'Fuga severa en junta principal.'
    ];
    ExcelExporter::exportReports([createBaseReport([$carIncidents])], $outDir . '/test_2_incidencias.xlsx');
    echo "[OK] test_2_incidencias.xlsx generado.\n";

} catch (\Exception $e) {
    echo "[ERROR] Test 2 falló: " . $e->getMessage() . "\n";
}

try {
    // TEST 3: Masivo (Más de 100 respuestas en 1 CAR)
    $massiveResponses = [];
    for ($i = 1; $i <= 100; $i++) {
        $massiveResponses["Comprobante iteracion $i"] = "OK";
    }
    // Provocamos un false para probar iteración con errores
    $massiveResponses["Comprobante iteracion 101 (Fallo)"] = false;

    $carMassive = [
        'car_name' => 'Sistema Extenso',
        'responses' => $massiveResponses
    ];
    ExcelExporter::exportReports([createBaseReport([$carMassive])], $outDir . '/test_3_masivo.xlsx');
    echo "[OK] test_3_masivo.xlsx generado.\n";

} catch (\Exception $e) {
    echo "[ERROR] Test 3 falló: " . $e->getMessage() . "\n";
}

try {
    // TEST 4: Acceptance Overflow (65 CARs para romper el límite de 60)
    $overflowCars = [];
    for ($i = 1; $i <= 65; $i++) {
        $overflowCars[] = [
            'car_name' => "Extintor #$i",
            'responses' => [
                'Presión' => 'malo' // Forzamos falla para que se registre como irregularidad
            ],
            'obs' => "Revisar valvula del extintor $i"
        ];
    }
    ExcelExporter::exportReports([createBaseReport($overflowCars)], $outDir . '/test_4_aceptacion_extensa.xlsx');
    echo "[OK] test_4_aceptacion_extensa.xlsx generado.\n";

} catch (\Exception $e) {
    echo "[ERROR] Test 4 falló: " . $e->getMessage() . "\n";
}

echo "\nTodos los scripts finalizados. Revisa la carpeta tests/excel/results/\n";
