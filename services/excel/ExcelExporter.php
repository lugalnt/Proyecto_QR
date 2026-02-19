<?php

namespace Services\Excel;

require_once __DIR__ . '/../../vendor/autoload.php';

use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;
use PhpOffice\PhpSpreadsheet\Style\Alignment;
use PhpOffice\PhpSpreadsheet\Style\Border;
use PhpOffice\PhpSpreadsheet\Style\Fill;

class ExcelExporter
{
    /**
     * Exporta datos de reportes a un archivo XLSX usando la lógica de sistemas (áreas) y plantilla.
     * 
     * @param array $rows Datos de los reportes.
     * @param string $filename Nombre del archivo de salida.
     */
    public static function exportReports(array $rows, string $filename = 'Reporte_QR.xlsx')
    {
        // Limpiar cualquier salida previa
        if (ob_get_length())
            ob_end_clean();

        // 1. Recolección y Organización de Datos
        $dataByArea = self::collectAndGroupData($rows);

        // 2. Cargar Plantilla
        $templatePath = __DIR__ . '/plantillaReportes.xlsx';
        if (!file_exists($templatePath)) {
            throw new \Exception("La plantilla no existe en: " . $templatePath);
        }

        $spreadsheet = \PhpOffice\PhpSpreadsheet\IOFactory::load($templatePath);

        // Limpiar nombres definidos para evitar conflictos
        foreach ($spreadsheet->getDefinedNames() as $name) {
            $spreadsheet->removeDefinedName($name->getName());
        }

        $usedAreas = [];

        // 3. Procesar cada Área (Sistema)
        foreach ($dataByArea as $areaName => $areaData) {
            $sheet = self::getBestMatchingSheet($spreadsheet, $areaName);

            if (!$sheet) {
                error_log("ExcelExporter: No se encontró hoja para el sistema '$areaName'.");
                continue;
            }

            self::fillSystemSheet($sheet, $areaData, $areaName);
            $usedAreas[] = $areaName;
        }

        // 4. Llenar página de "Aceptación y entrega."
        $aceptacionSheet = $spreadsheet->getSheetByName('Aceptación y entrega.');
        if ($aceptacionSheet) {
            self::fillAcceptanceSheet($aceptacionSheet, $usedAreas);
        }

        // 5. Output
        if (strpos($filename, 'php://') === 0 || strpos($filename, 'tests/') === 0) {
            // Si es una ruta de prueba o stream directo
            $writer = new \PhpOffice\PhpSpreadsheet\Writer\Xlsx($spreadsheet);
            $writer->save($filename === 'Test_Result.xlsx' ? 'php://output' : $filename);
        } else {
            header('Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
            header('Content-Disposition: attachment;filename="' . $filename . '"');
            header('Cache-Control: max-age=0');

            $writer = new \PhpOffice\PhpSpreadsheet\Writer\Xlsx($spreadsheet);
            $writer->save('php://output');
        }
        exit;
    }

    private static function collectAndGroupData(array $rows)
    {
        $grouped = [];
        foreach ($rows as $row) {
            $areaName = $row['Nombre_Area'] ?? 'Sin Area';
            if (!isset($grouped[$areaName])) {
                $grouped[$areaName] = [
                    'maquila' => $row['Nombre_Maquila'] ?? '',
                    'responsable' => $row['Resp_Nombre'] ?? '',
                    'fecha' => $row['FechaRegistro_Reporte'] ?? date('Y-m-d'),
                    'cars' => []
                ];
            }

            $jsonStr = $row['JSON_Reporte'] ?? '';
            $jsonData = json_decode($jsonStr, true);
            $carReports = $jsonData['car_reports'] ?? $jsonData['cars'] ?? $jsonData['area_data']['cars'] ?? $jsonData['area']['cars'] ?? [];

            if (is_array($carReports)) {
                foreach ($carReports as $car) {
                    $grouped[$areaName]['cars'][] = self::normalizeCarData($car, $grouped[$areaName]['fecha']);
                }
            }
        }
        return $grouped;
    }

    private static function normalizeCarData($car, $defaultFecha)
    {
        $carName = $car['car_name'] ?? $car['name'] ?? $car['Nombre_CAR'] ?? 'Sin Nombre';
        $observacion = $car['observacion'] ?? $car['obs'] ?? '';
        $responses = $car['responses'] ?? [];
        $props = $car['properties'] ?? $car['Propiedades'] ?? [];

        $details = [];
        if (!empty($responses) && is_array($responses)) {
            foreach ($responses as $label => $value) {
                $details[] = ['label' => $label, 'value' => $value];
            }
        } elseif (!empty($props) && is_array($props)) {
            foreach ($props as $prop) {
                $details[] = ['label' => $prop['label'] ?? $prop['Nombre_Propiedad'] ?? '-', 'value' => $prop['value'] ?? $prop['Valor'] ?? ''];
            }
        }

        return [
            'name' => $carName,
            'details' => $details,
            'obs' => $observacion,
            'fecha' => $defaultFecha
        ];
    }

    private static function getBestMatchingSheet($spreadsheet, $name)
    {
        $sheet = $spreadsheet->getSheetByName($name);
        if ($sheet)
            return $sheet;

        foreach ($spreadsheet->getAllSheets() as $sh) {
            $title = trim($sh->getTitle());
            if (stripos($title, trim($name)) !== false) {
                return $sh;
            }
        }
        return null;
    }

    private static function fillSystemSheet($sheet, $data, $areaName)
    {
        // Header: Maquila en C7, Area en B15 (según inspección previa)
        $sheet->setCellValue('C7', $data['maquila']);

        // Título de la tabla usualmente está en B15
        $sheet->setCellValue('B15', "LISTA DE REVISION: " . strtoupper($areaName));

        $currentRow = 16;
        $obsRowIndex = 62;

        // Buscar dinámicamente la fila de Observaciones
        for ($r = 16; $r < 200; $r++) {
            $val = $sheet->getCell('B' . $r)->getCalculatedValue();
            if ($val && (stripos((string) $val, 'Observaciones') !== false || stripos((string) $val, 'Comentarios') !== false)) {
                $obsRowIndex = $r;
                break;
            }
        }

        foreach ($data['cars'] as $car) {
            if ($currentRow >= $obsRowIndex) {
                $sheet->insertNewRowBefore($obsRowIndex, 1);
                $obsRowIndex++;
            }

            $sheet->setCellValue('B' . $currentRow, $car['name']);

            $isOk = true;
            $readings = [];
            $params = [];

            foreach ($car['details'] as $detail) {
                $v = (string) $detail['value'];
                $lowV = strtolower($v);
                if ($lowV === 'no' || $lowV === 'error' || $lowV === '0' || $lowV === 'false' || $lowV === 'malo') {
                    $isOk = false;
                }

                if (is_numeric($v)) {
                    $readings[] = $detail['label'] . ": " . $v;
                } else {
                    $params[] = $detail['label'] . ": " . $v;
                }
            }

            if ($isOk) {
                $sheet->setCellValue('J' . $currentRow, 'X');
                $sheet->setCellValue('I' . $currentRow, '');
            } else {
                $sheet->setCellValue('I' . $currentRow, 'X');
                $sheet->setCellValue('J' . $currentRow, '');
            }

            $sheet->setCellValue('K' . $currentRow, implode(", ", $params));

            // Lecturas en L, M, N (repartir si hay varias o poner todas en L)
            if (count($readings) > 0) {
                $sheet->setCellValue('L' . $currentRow, $readings[0]);
                if (isset($readings[1]))
                    $sheet->setCellValue('M' . $currentRow, $readings[1]);
                if (isset($readings[2]))
                    $sheet->setCellValue('N' . $currentRow, $readings[2]);
            }

            $currentRow++;
        }

        // Observaciones generales del sistema
        $allObs = [];
        foreach ($data['cars'] as $car) {
            if (!empty($car['obs']))
                $allObs[] = $car['name'] . ": " . $car['obs'];
        }

        if (!empty($allObs)) {
            $sheet->setCellValue('B' . ($obsRowIndex + 1), implode(" | ", $allObs));
        }
    }

    private static function fillAcceptanceSheet($sheet, $areas)
    {
        // En la plantilla actual, parece que las áreas se listan a partir de la fila 11
        $r = 11;
        foreach ($areas as $area) {
            $sheet->setCellValue('B' . $r, $area);
            $sheet->setCellValue('C' . $r, "COMPLETO");
            $r++;
            if ($r > 30)
                break; // Limite de seguridad
        }
    }

    public static function exportSingleReport(array $report, string $filename = 'Detalle_Reporte.xlsx')
    {
        self::exportReports([$report], $filename);
    }
}
