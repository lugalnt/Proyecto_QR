<?php
require 'vendor/autoload.php';

use PhpOffice\PhpSpreadsheet\IOFactory;

$inputFileName = 'utils/plantillaReportes.xlsx';

try {
    $spreadsheet = IOFactory::load($inputFileName);
    $sheet = $spreadsheet->getActiveSheet();
    $highestRow = $sheet->getHighestRow();
    $highestColumn = $sheet->getHighestColumn();
    $highestColumnIndex = \PhpOffice\PhpSpreadsheet\Cell\Coordinate::columnIndexFromString($highestColumn);

    echo "Sheet Name: " . $sheet->getTitle() . "\n";
    echo "Dimensions: A1:" . $highestColumn . $highestRow . "\n";

    // Inspect specifically around the keys we found
    $ranges = [
        [1, 20], // Header area
        [60, 70] // Footer area
    ];

    foreach ($ranges as $range) {
        echo "\n--- Rows " . $range[0] . " to " . $range[1] . " ---\n";
        for ($row = $range[0]; $row <= $range[1]; $row++) {
            $line = sprintf("%3d | ", $row);
            for ($col = 1; $col <= 20; $col++) { // distinct columns A to T
                $colString = \PhpOffice\PhpSpreadsheet\Cell\Coordinate::stringFromColumnIndex($col);
                $val = $sheet->getCell($colString . $row)->getValue();
                $val = substr((string) $val, 0, 10); // truncate for display
                $line .= str_pad($val, 12, " ");
            }
            echo $line . "\n";
        }
    }

} catch (Exception $e) {
    echo 'Error loading file: ', $e->getMessage();
}
