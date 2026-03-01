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
     * Exporta datos de reportes a un archivo XLSX.
     * Por cada objeto C.A.R se crea una hoja (clonada de "Sistema EJEMPLO").
     * Al final se genera un resumen en "Aceptaciòn y entrega.".
     *
     * @param array  $rows     Datos de los reportes.
     * @param string $filename Nombre del archivo de salida.
     */
    public static function exportReports(array $rows, string $filename = 'Reporte_QR.xlsx')
    {
        if (ob_get_length())
            ob_end_clean();

        // 1. Recolectar y agrupar datos por área → lista de CARs
        $dataByArea = self::collectAndGroupData($rows);

        // 2. Cargar plantilla
        $templatePath = __DIR__ . '/plantillaReportes.xlsx';
        if (!file_exists($templatePath)) {
            throw new \Exception("La plantilla no existe en: " . $templatePath);
        }
        $spreadsheet = \PhpOffice\PhpSpreadsheet\IOFactory::load($templatePath);

        // Limpiar nombres definidos para evitar conflictos
        foreach ($spreadsheet->getDefinedNames() as $name) {
            $spreadsheet->removeDefinedName($name->getName());
        }

        // 3. Obtener la hoja "Sistema EJEMPLO" como plantilla base
        $templateSheet = $spreadsheet->getSheetByName('Sistema EJEMPLO');
        if (!$templateSheet) {
            throw new \Exception("No se encontró la hoja 'Sistema EJEMPLO' en la plantilla.");
        }
        $templateIndex = $spreadsheet->getIndex($templateSheet);

        // Recolectar info global (maquila, responsable, fecha) del primer registro
        $globalInfo = self::extractGlobalInfo($rows);

        // 4. Generar una hoja por cada C.A.R
        $allCars = [];        // Lista plana de todos los CARs para la hoja de aceptación
        $insertIndex = 0;     // Insertar las hojas de CARs antes de "Aceptaciòn y entrega."

        foreach ($dataByArea as $areaName => $areaData) {
            foreach ($areaData['cars'] as $car) {
                // Clonar la hoja plantilla
                $cloned = clone $templateSheet;
                $sheetTitle = self::makeSheetTitle($areaName, $car['name']);
                $cloned->setTitle($sheetTitle);

                // Insertar la hoja clonada antes de "Aceptaciòn y entrega."
                $spreadsheet->addSheet($cloned, $insertIndex);
                $insertIndex++;

                // Rellenar la hoja con los datos del CAR
                self::fillCarSheet($cloned, $car, $areaName, $areaData['maquila'], $areaData['responsable'], $areaData['fecha']);

                $allCars[] = [
                    'area' => $areaName,
                    'car' => $car,
                    'maquila' => $areaData['maquila'],
                    'responsable' => $areaData['responsable'],
                    'fecha' => $areaData['fecha'],
                ];
            }
        }

        // 5. Eliminar la hoja "Sistema EJEMPLO" (ya no se necesita)
        $spreadsheet->removeSheetByIndex($spreadsheet->getIndex($templateSheet));

        // 6. Rellenar hoja de "Aceptaciòn y entrega."
        $aceptacionSheet = $spreadsheet->getSheetByName('Aceptaciòn y entrega.');
        if (!$aceptacionSheet) {
            // Nombre alternativo por si acaso hay variación de acento
            foreach ($spreadsheet->getAllSheets() as $sh) {
                if (stripos($sh->getTitle(), 'Aceptac') !== false) {
                    $aceptacionSheet = $sh;
                    break;
                }
            }
        }
        if ($aceptacionSheet) {
            self::fillAcceptanceSheet($aceptacionSheet, $allCars, $globalInfo);
        }

        // 7. Mover la hoja de aceptación al final
        if ($aceptacionSheet) {
            $lastIdx = $spreadsheet->getSheetCount() - 1;
            $currentIdx = $spreadsheet->getIndex($aceptacionSheet);
            if ($currentIdx !== $lastIdx) {
                $spreadsheet->setActiveSheetIndex($currentIdx);
                // Reposicionar al final
                $spreadsheet->removeSheetByIndex($currentIdx);
                $spreadsheet->addSheet($aceptacionSheet);
            }
        }

        // 8. Output
        $isTest = (strpos($filename, 'php://') === 0 || strpos($filename, 'tests/') === 0 || $filename === 'Test_Result.xlsx');
        if ($isTest) {
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

    // -------------------------------------------------------------------------
    // HELPERS DE DATOS
    // -------------------------------------------------------------------------

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
            $carReports = $jsonData['car_reports']
                ?? $jsonData['cars']
                ?? $jsonData['area_data']['cars']
                ?? $jsonData['area']['cars']
                ?? [];

            if (is_array($carReports)) {
                foreach ($carReports as $car) {
                    $grouped[$areaName]['cars'][] = self::normalizeCarData($car, $grouped[$areaName]['fecha']);
                }
            }
        }
        return $grouped;
    }

    private static function extractGlobalInfo(array $rows)
    {
        $first = $rows[0] ?? [];
        return [
            'maquila' => $first['Nombre_Maquila'] ?? '',
            'responsable' => $first['Resp_Nombre'] ?? '',
            'fecha' => $first['FechaRegistro_Reporte'] ?? date('Y-m-d'),
        ];
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
                $details[] = [
                    'label' => $prop['label'] ?? $prop['Nombre_Propiedad'] ?? '-',
                    'value' => $prop['value'] ?? $prop['Valor'] ?? ''
                ];
            }
        }

        return [
            'name' => $carName,
            'details' => $details,
            'obs' => $observacion,
            'fecha' => $defaultFecha
        ];
    }

    /**
     * Genera un título de hoja válido (máx. 31 caracteres, sin caracteres prohibidos).
     */
    private static function makeSheetTitle(string $area, string $carName): string
    {
        $forbidden = ['\\', '/', '?', '*', '[', ']', ':'];
        $area = str_replace($forbidden, '-', $area);
        $carName = str_replace($forbidden, '-', $carName);

        $title = $area . ' - ' . $carName;
        if (strlen($title) > 31) {
            // Acortar área y nombre para que quepan
            $available = 31 - 3; // 3 = " - "
            $half = (int) floor($available / 2);
            $title = substr($area, 0, $half) . ' - ' . substr($carName, 0, $available - $half);
        }
        return $title;
    }

    // -------------------------------------------------------------------------
    // LLENADO DE HOJA POR CAR
    // -------------------------------------------------------------------------

    /**
     * Rellena una hoja clonada de "Sistema EJEMPLO" con los datos de un C.A.R.
     *
     * Estructura de la plantilla (Sistema EJEMPLO):
     *   C7  → Cliente/Maquila
     *   C11 → Nombre del sistema/CAR
     *   B15 → "SISTEMAS  EJEMPLO" (encabezado de tabla, se conserva el formato)
     *   R16 en adelante → filas de datos
     *     B = Nombre del ítem
     *     G = Frecuencia (se deja vacío o "M")
     *     H = S (Satisfactorio)
     *     I = N/A
     *     J = N (No satisfactorio)
     *     K = Parámetros
     *     L = Lectura 1
     *     M = Lectura 2
     *     N = Lectura 3
     *     O = Comentarios
     *   B75 → "OBSERVACIONES :"
     */
    private static function fillCarSheet($sheet, array $car, string $areaName, string $maquila, string $responsable, string $fecha)
    {
        // Encabezado
        $sheet->setCellValue('C7', $maquila);
        $sheet->setCellValue('C8', $responsable);
        $sheet->setCellValue('C11', $car['name']);

        // Título de la sección
        $sheet->setCellValue('B15', 'LISTA DE REVISIÓN: ' . strtoupper($areaName) . ' - ' . strtoupper($car['name']));

        // Buscar dinámicamente la fila donde empieza el cuerpo (fila 16 por diseño de plantilla)
        $dataStartRow = 16;

        // Buscar fila de Observaciones
        $obsRowIndex = 75; // Valor por defecto de la plantilla
        $highestRow = $sheet->getHighestRow();
        for ($r = $dataStartRow; $r <= min($highestRow, 200); $r++) {
            $val = $sheet->getCell('B' . $r)->getCalculatedValue();
            if ($val && (stripos((string) $val, 'Observaciones') !== false || stripos((string) $val, 'Comentarios') !== false)) {
                $obsRowIndex = $r;
                break;
            }
        }

        $currentRow = $dataStartRow;

        foreach ($car['details'] as $detail) {
            if ($currentRow >= $obsRowIndex) {
                $sheet->insertNewRowBefore($obsRowIndex, 1);
                $obsRowIndex++;
            }

            $label = (string) $detail['label'];
            $value = (string) $detail['value'];
            $lowV = strtolower(trim($value));

            // Determinar estado del ítem
            $isBad = in_array($lowV, ['no', 'error', '0', 'false', 'malo', 'x', 'n']);
            $isNA = in_array($lowV, ['n/a', 'na', 'no aplica', 'no_aplica']);

            // Nombre del ítem en columna B
            $sheet->setCellValue('B' . $currentRow, $label);

            if ($isNA) {
                $sheet->setCellValue('I' . $currentRow, 'X');
            } elseif ($isBad) {
                $sheet->setCellValue('J' . $currentRow, 'X');
            } else {
                $sheet->setCellValue('H' . $currentRow, 'X');
            }

            // Si el valor es numérico → Lecturas (L/M/N), sino → Parámetros (K) o Comentarios (O)
            if (is_numeric($value)) {
                $sheet->setCellValue('L' . $currentRow, $value);
            } elseif (!$isBad && !$isNA && $lowV !== 'ok' && $lowV !== 'si' && $lowV !== 'sí' && $lowV !== 'yes') {
                // Valor descriptivo que no es simplemente OK/SI → va en parámetros
                $sheet->setCellValue('K' . $currentRow, $value);
            }

            $currentRow++;
        }

        // Observación del CAR
        if (!empty($car['obs'])) {
            $sheet->setCellValue('B' . ($obsRowIndex + 1), $car['obs']);
        }

        // Fecha
        $sheet->setCellValue('L9', $fecha);
    }

    // -------------------------------------------------------------------------
    // LLENADO DE HOJA DE ACEPTACIÓN
    // -------------------------------------------------------------------------

    /**
     * Rellena la hoja "Aceptaciòn y entrega." con un resumen de todos los CARs.
     *
     * Estructura detectada en la plantilla:
     *   B8  → "CLIENTE : "  C8 → valor
     *   B9  → "ATENCION :"  C9 → valor
     *   B10 → "SERVICIO :"  C10 → valor   O10 → fecha
     *   B13 → REFERENCIA    D13 → IRREGULARIDAD    K13 → RECOMENDACION
     *   R14 en adelante → filas de resumen
     *   B62 → Observaciones generales
     */
    private static function fillAcceptanceSheet($sheet, array $allCars, array $globalInfo)
    {
        // Encabezado
        $sheet->setCellValue('C8', $globalInfo['maquila']);
        $sheet->setCellValue('C9', $globalInfo['responsable']);
        $sheet->setCellValue('C10', 'Reporte mensual de sistemas contra incendio');
        $sheet->setCellValue('O10', $globalInfo['fecha']);

        $r = 14; // Primera fila de datos

        foreach ($allCars as $entry) {
            $car = $entry['car'];
            $area = $entry['area'];

            // Recopilar irregularidades (ítems marcados como "no satisfactorio")
            $irregularidades = [];
            $recomendaciones = [];

            foreach ($car['details'] as $detail) {
                $lowV = strtolower(trim((string) $detail['value']));
                $isBad = in_array($lowV, ['no', 'error', '0', 'false', 'malo', 'x', 'n']);

                if ($isBad) {
                    $irregularidades[] = $detail['label'];
                    $recomendaciones[] = 'Revisión y corrección de: ' . $detail['label'];
                }
            }

            // Referencia: Area - Nombre del CAR
            $referencia = $area . ' - ' . $car['name'];

            if (!empty($irregularidades)) {
                $sheet->setCellValue('B' . $r, $referencia);
                $sheet->setCellValue('D' . $r, implode('; ', $irregularidades));
                $sheet->setCellValue('K' . $r, implode('; ', $recomendaciones));
            } else {
                // Sin irregularidades: marcar como satisfactorio
                $sheet->setCellValue('B' . $r, $referencia);
                $sheet->setCellValue('D' . $r, 'Sin irregularidades');
                $sheet->setCellValue('K' . $r, 'Continuar con mantenimiento preventivo');
            }

            // Si hay observación, agregarla como nota en la misma fila
            if (!empty($car['obs'])) {
                $existing = $sheet->getCell('K' . $r)->getValue();
                $sheet->setCellValue('K' . $r, $existing . ' | Obs: ' . $car['obs']);
            }

            $r++;
            if ($r > 60)
                break; // Límite de seguridad (fila 62 es Observaciones en plantilla)
        }

        // Observaciones generales: consolidar todas las obs de todos los CARs
        $obsGenerales = [];
        foreach ($allCars as $entry) {
            if (!empty($entry['car']['obs'])) {
                $obsGenerales[] = $entry['area'] . ' - ' . $entry['car']['name'] . ': ' . $entry['car']['obs'];
            }
        }
        if (!empty($obsGenerales)) {
            $sheet->setCellValue('B63', implode("\n", $obsGenerales));
        }
    }

    // -------------------------------------------------------------------------
    // ENTRADA PÚBLICA ALTERNATIVA
    // -------------------------------------------------------------------------

    public static function exportSingleReport(array $report, string $filename = 'Detalle_Reporte.xlsx')
    {
        self::exportReports([$report], $filename);
    }
}
