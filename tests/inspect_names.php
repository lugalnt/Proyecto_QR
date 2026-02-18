<?php
require 'vendor/autoload.php';

use PhpOffice\PhpSpreadsheet\IOFactory;

$inputFileName = 'utils/plantillaReportes.xlsx';

try {
    $spreadsheet = IOFactory::load($inputFileName);
    $definedNames = $spreadsheet->getDefinedNames();

    echo "Defined Names Count: " . count($definedNames) . "\n";
    foreach ($definedNames as $name) {
        echo "Name: " . $name->getName() . " -> " . $name->getValue() . "\n";
    }

} catch (Exception $e) {
    echo 'Error: ', $e->getMessage();
}
