<?php
require_once 'vendor/autoload.php';

use PhpOffice\PhpSpreadsheet\IOFactory;

$templatePath = 'services/excel/plantillaReportes.xlsx';
$outputPath = 'tests/excel/template_structure.txt';

$log = function ($msg) use ($outputPath) {
    echo $msg;
    file_put_contents($outputPath, $msg, FILE_APPEND);
};

if (file_exists($outputPath))
    unlink($outputPath);

if (!file_exists($templatePath)) {
    $log("Plantilla no encontrada: $templatePath\n");
    exit(1);
}

try {
    $spreadsheet = IOFactory::load($templatePath);
    $log("Plantilla cargada correctamente.\n");

    foreach ($spreadsheet->getAllSheets() as $sheet) {
        $title = $sheet->getTitle();
        $log("=== Hoja: $title ===\n");

        $row15 = [];
        $row16 = [];
        foreach (range('A', 'P') as $col) {
            $row15[$col] = $sheet->getCell($col . '15')->getCalculatedValue();
            $row16[$col] = $sheet->getCell($col . '16')->getCalculatedValue();
        }
        $log("  Row 15: " . json_encode($row15) . "\n");
        $log("  Row 16: " . json_encode($row16) . "\n");

        $c7 = $sheet->getCell('C7')->getCalculatedValue();
        $log("  [C7] => " . ($c7 === null ? '[EMPTY]' : $c7) . "\n");

        // Buscar fila de observaciones
        $highestRow = $sheet->getHighestRow();
        $obsRow = -1;
        for ($r = 16; $r <= $highestRow; $r++) {
            $val = $sheet->getCell('B' . $r)->getCalculatedValue();
            if ($val && stripos((string) $val, 'Observaciones') !== false) {
                $obsRow = $r;
                break;
            }
        }
        $log("  Fila 'Observaciones' detectada en: " . ($obsRow > 0 ? "B$obsRow" : "No encontrada") . "\n");
        $log("\n");
    }
} catch (Exception $e) {
    $log("Error: " . $e->getMessage() . "\n");
}
