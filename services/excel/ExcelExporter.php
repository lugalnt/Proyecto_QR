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

        // 1. Recolección y Organización de Datos (DEBUG)
        $dataByArea = self::collectAndGroupData($rows);

        // 2. Cargar Plantilla
        $templatePath = __DIR__ . '/plantillaReportes.xlsx';
        if (!file_exists($templatePath)) {
            throw new \Exception("La plantilla no existe en: " . $templatePath);
        }

        $spreadsheet = \PhpOffice\PhpSpreadsheet\IOFactory::load($templatePath);

        // Limpiar nombres definidos para evitar conflictos de impresión
        foreach ($spreadsheet->getDefinedNames() as $name) {
            $spreadsheet->removeDefinedName($name->getName());
        }

        $allSheetNames = $spreadsheet->getSheetNames();
        $usedAreas = array_keys($dataByArea);

        // 3. Procesar cada Área (Sistema)
        foreach ($dataByArea as $areaName => $areaData) {
            // Intentar encontrar la hoja exacta o una parecida
            $sheet = self::getBestMatchingSheet($spreadsheet, $areaName);

            if (!$sheet) {
                // Si no hay match, podríamos clonar una por defecto o saltar (aquí saltamos con log)
                error_log("ExcelExporter: No se encontró hoja para el sistema '$areaName'.");
                continue;
            }

            self::fillSystemSheet($sheet, $areaData);
        }

        // 4. Eliminar hojas no utilizadas (opcional, pero limpio)
        // Excepto "Aceptación y entrega" y las que acabamos de llenar
        // Para simplificar, las dejamos pero llenamos la de Aceptación al final.

        // 5. Llenar página de "Aceptación y entrega"
        $aceptacionSheet = $spreadsheet->getSheetByName('Aceptación y entrega.');
        if ($aceptacionSheet) {
            self::fillAcceptanceSheet($aceptacionSheet, $usedAreas);
        }

        // 6. Output
        header('Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
        header('Content-Disposition: attachment;filename="' . $filename . '"');
        header('Cache-Control: max-age=0');

        $writer = new \PhpOffice\PhpSpreadsheet\Writer\Xlsx($spreadsheet);
        $writer->save('php://output');
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

            // Parsear JSON de CARs
            $jsonStr = $row['JSON_Reporte'] ?? '';
            $jsonData = json_decode($jsonStr, true);
            $carReports = $jsonData['car_reports'] ?? $jsonData['cars'] ?? $jsonData['area_data']['cars'] ?? $jsonData['area']['cars'] ?? [];

            if (is_array($carReports)) {
                foreach ($carReports as $car) {
                    $grouped[$areaName]['cars'][] = self::normalizeCarData($car, $grouped[$areaName]['fecha']);
                }
            }
        }

        // Debug log opcional (PHP Error Log)
        error_log("ExcelExporter: Datos recolectados para " . count($grouped) . " áreas.");

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
        // 1. Match exacto
        $sheet = $spreadsheet->getSheetByName($name);
        if ($sheet)
            return $sheet;

        // 2. Match parcial (case insensitive, sin espacios raros)
        foreach ($spreadsheet->getAllSheets() as $sh) {
            if (stripos(trim($sh->getTitle()), trim($name)) !== false) {
                return $sh;
            }
        }

        // 3. Fallback: buscar una hoja que diga "Sistema X"
        return null;
    }

    private static function fillSystemSheet($sheet, $data)
    {
        // Maquila C7 - I7 (celdas combinadas usualmente)
        $sheet->setCellValue('C7', $data['maquila']);

        // El usuario dijo: b-f 16 CARs, i 16 no ok, j 16 ok, k 16 parametros, l-m-n 16 lecturas
        $currentRow = 16;
        $startRow = 16;

        // Buscar fila de observaciones para saber cuánto espacio tenemos
        $obsRowIndex = 62;
        foreach (range(16, 500) as $r) {
            $val = $sheet->getCell('B' . $r)->getCalculatedValue();
            if ($val && stripos((string) $val, 'Observaciones') !== false) {
                $obsRowIndex = $r;
                break;
            }
        }

        foreach ($data['cars'] as $car) {
            // Un CAR puede tener múltiples sub-items si queremos detallar responses, 
            // pero el usuario pidió "CAR" en B-F. Si el CAR es la unidad, ponemos el nombre ahí.

            // Si hay muchos sub-items, insertamos filas
            if ($currentRow >= $obsRowIndex - 1) {
                $sheet->insertNewRowBefore($obsRowIndex, 1);
                $obsRowIndex++;
            }

            $sheet->setCellValue('B' . $currentRow, $car['name']);

            // Lógica simple para OK/NO OK (J/I)
            // Asumiremos que si hay fallos en los detalles es NO OK
            $isOk = true;
            $readings = [];
            $params = [];

            foreach ($car['details'] as $detail) {
                $v = (string) $detail['value'];
                if (stripos($v, 'ERROR') !== false || stripos($v, 'NO') !== false || $v === "0" || $v === "false") {
                    $isOk = false;
                }

                // Si parece lectura (número)
                if (is_numeric($v)) {
                    $readings[] = $detail['label'] . ": " . $v;
                } else {
                    $params[] = $detail['label'] . ": " . $v;
                }
            }

            if ($isOk) {
                $sheet->setCellValue('J' . $currentRow, 'X');
            } else {
                $sheet->setCellValue('I' . $currentRow, 'X');
            }

            $sheet->setCellValue('K' . $currentRow, implode(", ", $params));
            $sheet->setCellValue('L' . $currentRow, implode(", ", $readings));

            // Observaciones (abajo, el usuario dijo "hay reglones de observaciones")
            // Usualmente se ponen al final de la tabla o en la fila de observaciones del footer

            $currentRow++;
        }

        // Poner la observación general del primer reporte en la fila de observaciones detectada
        if (!empty($data['cars'][0]['obs'])) {
            $sheet->setCellValue('B' . ($obsRowIndex + 1), $data['cars'][0]['obs']);
        }
    }

    private static function fillAcceptanceSheet($sheet, $areas)
    {
        // Listar las áreas al final
        $sheet->setCellValue('B10', "Sistemas inspeccionados:");
        $r = 11;
        foreach ($areas as $area) {
            $sheet->setCellValue('B' . $r, "- " . $area);
            $r++;
        }
    }

    /**
     * Exporta un reporte individual (mantenemos compatibilidad básica si se usa).
     */
    public static function exportSingleReport(array $report, string $filename = 'Detalle_Reporte.xlsx')
    {
        self::exportReports([$report], $filename);
    }
}
