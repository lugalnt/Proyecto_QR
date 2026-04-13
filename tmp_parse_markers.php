<?php
require 'vendor/autoload.php';

$templatePath = __DIR__ . '/services/excel/nuevaPlantillaMarcadores.xlsx';
$spreadsheet = \PhpOffice\PhpSpreadsheet\IOFactory::load($templatePath);

echo "Hojas:\n";
foreach ($spreadsheet->getAllSheets() as $sheet) {
    echo "- " . $sheet->getTitle() . "\n";
    $markers = [];
    foreach ($sheet->getRowIterator() as $row) {
        foreach ($row->getCellIterator() as $cell) {
            $val = $cell->getValue();
            if (is_string($val) && strpos($val, '{{') !== false) {
                // Find all markers in the cell using regex
                preg_match_all('/\{\{.*?\}\}|\{\{.*?\)\)/', $val, $matches);
                foreach ($matches[0] as $match) {
                    $coord = $cell->getCoordinate();
                    $markers[$match][] = $coord;
                }
            }
        }
    }
    foreach ($markers as $marker => $coords) {
        $count = count($coords);
        echo "  $marker : $count occurrences (e.g. " . implode(', ', array_slice($coords, 0, 5)) . ")\n";
    }
}
