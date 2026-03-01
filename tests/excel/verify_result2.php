<?php
require_once __DIR__ . '/../../vendor/autoload.php';

use PhpOffice\PhpSpreadsheet\IOFactory;

$testFile = __DIR__ . '/Test_Result.xlsx';

if (!file_exists($testFile)) {
    echo "ERROR: El archivo Test_Result.xlsx no existe.\n";
    exit(1);
}

$spreadsheet = IOFactory::load($testFile);

echo "=== Hojas generadas (" . $spreadsheet->getSheetCount() . ") ===\n";
foreach ($spreadsheet->getAllSheets() as $i => $sh) {
    echo "  [$i] \"" . $sh->getTitle() . "\"\n";
}

echo "\n=== Contenido de cada hoja ===\n";
foreach ($spreadsheet->getAllSheets() as $sh) {
    echo "\n--- Hoja: " . $sh->getTitle() . " ---\n";
    $hr = $sh->getHighestRow();
    for ($r = 1; $r <= min($hr, 80); $r++) {
        $row = [];
        for ($c = 'A'; $c <= 'O'; $c++) {
            $v = $sh->getCell($c . $r)->getValue();
            if ($v !== null && $v !== '') {
                $vs = is_string($v) ? substr($v, 0, 50) : $v;
                $row[$c . $r] = $vs;
            }
            if ($c === 'O')
                break;
        }
        if (!empty($row)) {
            echo "  R{$r}: " . json_encode($row, JSON_UNESCAPED_UNICODE) . "\n";
        }
    }
}
