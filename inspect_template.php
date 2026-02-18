<?php
require_once 'vendor/autoload.php';

use PhpOffice\PhpSpreadsheet\IOFactory;

$templatePath = 'utils/plantillaReportes.xlsx';

if (!file_exists($templatePath)) {
    echo "Plantilla no encontrada: $templatePath\n";
    exit(1);
}

try {
    $spreadsheet = IOFactory::load($templatePath);
    echo "Plantilla cargada correctamente.\n";

    $sheetNames = $spreadsheet->getSheetNames();
    echo "Hojas encontradas: " . implode(', ', $sheetNames) . "\n\n";

    foreach ($spreadsheet->getAllSheets() as $sheet) {
        $title = $sheet->getTitle();
        echo "=== Hoja: $title ===\n";

        $cells = ['C7', 'C8', 'C9', 'I7', 'B16', 'F16', 'I16', 'J16', 'K16', 'L16', 'M16', 'N16'];
        foreach ($cells as $cell) {
            $value = $sheet->getCell($cell)->getValue();
            echo "  [$cell] => " . ($value === null ? '[EMPTY]' : $value) . "\n";
        }

        // Buscar fila de observaciones
        $highestRow = $sheet->getHighestRow();
        $obsRow = -1;
        for ($r = 16; $r <= $highestRow; $r++) {
            $val = $sheet->getCell('B' . $r)->getValue();
            if ($val && stripos((string) $val, 'Observaciones') !== false) {
                $obsRow = $r;
                break;
            }
        }
        echo "  Fila 'Observaciones' detectada en: " . ($obsRow > 0 ? "B$obsRow" : "No encontrada") . "\n";
        echo "\n";
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage() . "\n";
}
