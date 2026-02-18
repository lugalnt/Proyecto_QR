<?php
require_once 'services/excel/ExcelExporter.php';

use Services\Excel\ExcelExporter;

// Mock data
$rows = [
    [
        'Nombre_Area' => 'Tanque Agua',
        'Nombre_Maquila' => 'Maquila Test 1',
        'Resp_Nombre' => 'Ing. Juan Perez',
        'FechaRegistro_Reporte' => '2026-02-18',
        'JSON_Reporte' => json_encode([
            'cars' => [
                [
                    'name' => 'Valvula de Succion',
                    'responses' => [
                        'Abierta' => 'SI',
                        'Estado' => 'OK'
                    ],
                    'obs' => 'Todo bien en el tanque.'
                ],
                [
                    'name' => 'Fugas',
                    'responses' => [
                        'Presenta fugas' => 'NO'
                    ]
                ]
            ]
        ])
    ],
    [
        'Nombre_Area' => 'Cuarto Bombas',
        'Nombre_Maquila' => 'Maquila Test 1',
        'Resp_Nombre' => 'Ing. Juan Perez',
        'FechaRegistro_Reporte' => '2026-02-18',
        'JSON_Reporte' => json_encode([
            'cars' => [
                [
                    'name' => 'Bomba 1',
                    'properties' => [
                        ['label' => 'Presion', 'value' => '150'],
                        ['label' => 'Vibracion', 'value' => 'Normal']
                    ],
                    'obs' => 'Bomba en operacion normal.'
                ]
            ]
        ])
    ]
];

echo "Iniciando prueba de exportación...\n";

try {
    // Para probar en CLI, redirigimos a un archivo físico en lugar de php://output
    // Tendremos que modificar ExcelExporter temporalmente o capturar el buffer.

    ob_start();
    ExcelExporter::exportReports($rows, 'Test_Result.xlsx');
    $content = ob_get_clean();

    $outputPath = 'tests/excel/Test_Result.xlsx';
    file_put_contents($outputPath, $content);

    echo "Exportación completada. Guardado en: $outputPath\n";

} catch (Exception $e) {
    echo "ERROR: " . $e->getMessage() . "\n";
}
