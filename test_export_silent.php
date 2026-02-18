<?php
// Mock del script de exportación
require 'utils/ExcelExporter.php';

use Utils\ExcelExporter;

// Mock data
$mockReport = [
    'Id_Reporte' => 12345,
    'Nombre_Maquila' => 'Maquila Test S.A.',
    'Nombre_Area' => 'Area de Corte',
    'Resp_Nombre' => 'Juan Perez',
    'FechaRegistro_Reporte' => '2023-10-27',
    'JSON_Reporte' => json_encode([
        'car_reports' => [
            [
                'car_name' => 'CAR-001 Frontal',
                'responses' => [
                    'Pintura' => 'OK',
                    'Abolladuras' => 'NO'
                ],
                'observacion' => 'Todo bien en general'
            ],
            [
                'car_name' => 'CAR-002 Trasero',
                'properties' => [
                    ['label' => 'Luces', 'value' => 'OK', 'obs' => 'Funcionando'],
                    ['label' => 'Frenos', 'value' => 'Revisar', 'obs' => 'Desgaste leve']
                ]
            ]
        ]
    ])
];

// Suppress output of errors to stdout
ini_set('display_errors', 0);
ini_set('log_errors', 1);

// Register shutdown function to capture output before exit
register_shutdown_function(function () {
    $content = ob_get_contents();
    ob_end_clean();
    if (strlen($content) > 0) {
        file_put_contents('test_export_result.xlsx', $content);
    }
});

// Start buffering
ob_start();

// Call function - this will output binary and exit
ExcelExporter::exportSingleReport($mockReport, 'ignored_filename.xlsx');
