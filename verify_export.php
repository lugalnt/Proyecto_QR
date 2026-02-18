<?php
require 'vendor/autoload.php';

use PhpOffice\PhpSpreadsheet\IOFactory;

$inputFileName = 'test_export_result.xlsx';

try {
    $spreadsheet = IOFactory::load($inputFileName);
    $sheet = $spreadsheet->getActiveSheet();
    $highestRow = $sheet->getHighestRow();

    echo "Sheet Name: " . $sheet->getTitle() . "\n";
    echo "Dimensions: " . $sheet->calculateWorksheetDimension() . "\n";

    // Verify Headers
    $headers = [
        'C8' => 'Maquila',
        'C9' => 'Area / Resp',
        'Q9' => 'Inspector',
        'Q10' => 'Fecha'
    ];

    echo "\n--- Headers ---\n";
    foreach ($headers as $cell => $label) {
        echo "$label ($cell): " . $sheet->getCell($cell)->getValue() . "\n";
    }

    // Verify Data Table (Rows 14+)
    echo "\n--- Data Table (Rows 14-25) ---\n";
    for ($row = 14; $row <= 25; $row++) {
        $b = $sheet->getCell('B' . $row)->getValue();
        $d = $sheet->getCell('D' . $row)->getValue();
        $k = $sheet->getCell('K' . $row)->getValue();

        if (!empty($b) || !empty($d)) {
            echo "Row $row: [B] '$b' | [D] '$d' | [K] '$k'\n";
        }
    }

    // Verify Footer Position
    echo "\n--- Footer Check (Row 62) ---\n";
    $b62 = $sheet->getCell('B62')->getValue();
    echo "B62: $b62\n";

} catch (Exception $e) {
    echo 'Error: ', $e->getMessage();
}
