<?php
require_once 'vendor/autoload.php';

use PhpOffice\PhpSpreadsheet\IOFactory;

$filePath = 'tests/excel/Test_Result.xlsx';

if (!file_exists($filePath)) {
    echo "Archivo de resultado no encontrado: $filePath\n";
    exit(1);
}

try {
    $spreadsheet = IOFactory::load($filePath);
    echo "Hojas generadas: " . implode(', ', $spreadsheet->getSheetNames()) . "\n\n";

    $checks = ['Tanque Agua', 'Cuarto Bombas', 'Aceptación y entrega.'];

    foreach ($checks as $sheetName) {
        $sheet = $spreadsheet->getSheetByName($sheetName);
        if (!$sheet) {
            echo "--- Hoja '$sheetName' NO encontrada ---\n";
            continue;
        }

        echo "=== Verificando Hoja: $sheetName ===\n";
        echo "  [C7] Maquila: " . $sheet->getCell('C7')->getValue() . "\n";

        // Revisar filas 16 y 17
        echo "  [B16] CAR: " . $sheet->getCell('B16')->getValue() . "\n";
        echo "  [I16] NO OK: " . $sheet->getCell('I16')->getValue() . "\n";
        echo "  [J16] OK: " . $sheet->getCell('J16')->getValue() . "\n";
        echo "  [K16] Parametros: " . $sheet->getCell('K16')->getValue() . "\n";
        echo "  [L16] Lecturas: " . $sheet->getCell('L16')->getValue() . "\n";

        if ($sheetName === 'Aceptación y entrega.') {
            echo "  [B10]: " . $sheet->getCell('B10')->getValue() . "\n";
            echo "  [B11]: " . $sheet->getCell('B11')->getValue() . "\n";
            echo "  [B12]: " . $sheet->getCell('B12')->getValue() . "\n";
        }
        echo "\n";
    }
} catch (Exception $e) {
    echo "Error al verificar: " . $e->getMessage() . "\n";
}
