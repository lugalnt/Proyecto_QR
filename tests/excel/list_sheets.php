<?php
require_once __DIR__ . '/vendor/autoload.php';
use PhpOffice\PhpSpreadsheet\IOFactory;

$templatePath = __DIR__ . '/services/excel/plantillaReportes.xlsx';
if (file_exists($templatePath)) {
    $spreadsheet = IOFactory::load($templatePath);
    echo "Sheets in template:\n";
    foreach ($spreadsheet->getSheetNames() as $name) {
        echo "- " . $name . "\n";
    }
} else {
    echo "Template not found at: $templatePath\n";
}
