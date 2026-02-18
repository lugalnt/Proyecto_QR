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

// Capture output buffer to save to file instead of stream
ob_start();
ExcelExporter::exportSingleReport($mockReport, 'test_output.xlsx');
$content = ob_get_clean();

// Save manually
file_put_contents('test_export_result.xlsx', $content);

echo "Export generated: test_export_result.xlsx\n";
