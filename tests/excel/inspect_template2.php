<?php
require_once __DIR__ . '/../../vendor/autoload.php';

use PhpOffice\PhpSpreadsheet\IOFactory;

$templatePath = __DIR__ . '/../../services/excel/plantillaReportes.xlsx';
$spreadsheet = IOFactory::load($templatePath);

$out = '';
foreach ($spreadsheet->getAllSheets() as $sh) {
    $title = $sh->getTitle();
    $out .= "=== HOJA: $title ===\n";
    $hr = $sh->getHighestRow();
    $hc = $sh->getHighestColumn();
    $out .= "Dimensiones: {$hc}{$hr}\n";

    for ($r = 1; $r <= min($hr, 100); $r++) {
        $row = [];
        $colIndex = 'A';
        while ($colIndex <= 'P') {
            $v = $sh->getCell($colIndex . $r)->getValue();
            if ($v !== null && $v !== '') {
                // Truncar valores largos
                $vs = is_string($v) ? substr($v, 0, 60) : $v;
                $row[$colIndex . $r] = $vs;
            }
            if ($colIndex === 'P')
                break;
            $colIndex++;
        }
        if (!empty($row)) {
            $out .= "  R{$r}: " . json_encode($row, JSON_UNESCAPED_UNICODE) . "\n";
        }
    }
    $out .= "\n";
}

file_put_contents(__DIR__ . '/template_structure2.txt', $out);
echo "Guardado en tests/excel/template_structure2.txt\n";
echo "Hojas encontradas: " . $spreadsheet->getSheetCount() . "\n";
foreach ($spreadsheet->getAllSheets() as $i => $sh) {
    echo "  [$i] " . $sh->getTitle() . "\n";
}
