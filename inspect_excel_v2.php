<?php
require 'vendor/autoload.php';

use PhpOffice\PhpSpreadsheet\IOFactory;

$inputFileName = $argv[1] ?? 'utils/plantillaReportes.xlsx';

try {
    $spreadsheet = IOFactory::load($inputFileName);
    $sheet = $spreadsheet->getActiveSheet();
    $highestRow = $sheet->getHighestRow();
    $highestColumn = $sheet->getHighestColumn();
    $highestColumnIndex = \PhpOffice\PhpSpreadsheet\Cell\Coordinate::columnIndexFromString($highestColumn);

    echo "Sheet Name: " . $sheet->getTitle() . "\n";
    echo "Dimensions: A1:" . $highestColumn . $highestRow . "\n";

    echo "--- Non-empty Cells ---\n";
    for ($row = 1; $row <= $highestRow; $row++) {
        for ($col = 1; $col <= $highestColumnIndex; $col++) {
            $colString = \PhpOffice\PhpSpreadsheet\Cell\Coordinate::stringFromColumnIndex($col);
            $cell = $sheet->getCell($colString . $row);
            $val = $cell->getValue();

            if (!empty($val) || $val === '0' || $val === 0) {
                // Check if it's part of a merged range
                $isMerged = false;
                foreach ($sheet->getMergeCells() as $range) {
                    if ($cell->isInRange($range)) {
                        // Only print the top-left cell of a merge range
                        $rangeParts = explode(':', $range);
                        if ($cell->getCoordinate() === $rangeParts[0]) {
                            // It's the main cell
                        } else {
                            // It's a hidden cell in merge
                            // $isMerged = true; 
                        }
                    }
                }

                echo "[$colString$row]: $val\n";
            }
        }
    }

} catch (Exception $e) {
    echo 'Error loading file: ', $e->getMessage();
}
