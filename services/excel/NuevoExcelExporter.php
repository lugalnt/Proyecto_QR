<?php

namespace Services\Excel;

require_once __DIR__ . '/../../vendor/autoload.php';

use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;

class NuevoExcelExporter
{
    /**
     * Exporta datos de reportes a un archivo XLSX utilizando la nueva plantilla basada en marcadores.
     *
     * @param array  $rows     Datos de los reportes.
     * @param string $filename Nombre del archivo de salida.
     */
    public static function exportReports(array $rows, string $filename = 'Nuevo_Reporte_QR.xlsx')
    {
        if (ob_get_length())
            ob_end_clean();

        // 1. Recolectar y agrupar datos por área → lista de CARs
        $dataByArea = self::collectAndGroupData($rows);

        // 2. Cargar plantilla
        $templatePath = __DIR__ . '/nuevaPlantillaMarcadores.xlsx';
        if (!file_exists($templatePath)) {
            throw new \Exception("La plantilla no existe en: " . $templatePath);
        }
        $spreadsheet = \PhpOffice\PhpSpreadsheet\IOFactory::load($templatePath);

        // Limpiar nombres definidos para evitar conflictos
        foreach ($spreadsheet->getDefinedNames() as $name) {
            $spreadsheet->removeDefinedName($name->getName());
        }

        // 3. Obtener la hoja "SISTEMA" como plantilla base
        $templateSheet = null;
        foreach ($spreadsheet->getAllSheets() as $sh) {
            if (strtoupper($sh->getTitle()) === 'SISTEMA') {
                $templateSheet = $sh;
                break;
            }
        }
        if (!$templateSheet) {
            throw new \Exception("No se encontró la hoja 'SISTEMA' en la plantilla.");
        }

        // Recolectar info global (maquila, responsable, fecha) del primer registro
        $globalInfo = self::extractGlobalInfo($rows);

        // 4. Generar una hoja por cada C.A.R
        $allCars = [];        
        $insertIndex = 0;     

        foreach ($dataByArea as $areaName => $areaData) {
            foreach ($areaData['cars'] as $car) {
                // Clonar la hoja plantilla
                $cloned = clone $templateSheet;
                $sheetTitle = self::makeSheetTitle($areaName, $car['name']);
                $cloned->setTitle($sheetTitle);

                // Insertar la hoja clonada
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

        // 5. Eliminar la hoja "SISTEMA" (ya no se necesita)
        $spreadsheet->removeSheetByIndex($spreadsheet->getIndex($templateSheet));

        // 6. Rellenar hoja de "ACEPTACIÓN Y ENTREGA"
        $aceptacionSheet = null;
        foreach ($spreadsheet->getAllSheets() as $sh) {
            if (stripos($sh->getTitle(), 'ACEPTAC') !== false) {
                $aceptacionSheet = $sh;
                break;
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
                $spreadsheet->removeSheetByIndex($currentIdx);
                $spreadsheet->addSheet($aceptacionSheet);
            }
        }

        // 8. Output
        $isTest = (strpos($filename, 'php://') === 0 || strpos($filename, 'tests/') === 0 || $filename === 'Test_Result.xlsx');
        if ($isTest) {
            $writer = new Xlsx($spreadsheet);
            $writer->save($filename === 'Test_Result.xlsx' ? 'php://output' : $filename);
        } else {
            header('Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
            header('Content-Disposition: attachment;filename="' . $filename . '"');
            header('Cache-Control: max-age=0');
            $writer = new Xlsx($spreadsheet);
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
            if (!$jsonData) continue;

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
        
        $incidencia = $car['incidencia'] ?? $car['incidencias'] ?? '';
        if (!empty($incidencia)) {
            $obs_part = empty($observacion) ? '' : $observacion . " | ";
            $observacion = $obs_part . "Incidencia: " . $incidencia;
        }

        $responses = $car['responses'] ?? [];
        $props = $car['properties'] ?? $car['Propiedades'] ?? [];

        $details = [];
        if (!empty($responses) && is_array($responses)) {
            foreach ($responses as $label => $value) {
                if (substr((string)$label, -5) === '_stts') continue;
                $valStr = is_bool($value) ? ($value ? 'si' : 'no') : (string)$value;
                
                $estado = '';
                if (isset($responses[$label . '_stts'])) {
                    $estado = $responses[$label . '_stts'];
                }
                
                $details[] = ['label' => $label, 'value' => $valStr, 'estado' => $estado];
            }
        } elseif (!empty($props) && is_array($props)) {
            foreach ($props as $prop) {
                $val = $prop['value'] ?? $prop['Valor'] ?? '';
                $valStr = is_bool($val) ? ($val ? 'si' : 'no') : (string)$val;
                $details[] = [
                    'label' => $prop['label'] ?? $prop['Nombre_Propiedad'] ?? '-',
                    'value' => $valStr
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

    private static function makeSheetTitle(string $area, string $carName): string
    {
        $forbidden = ['\\', '/', '?', '*', '[', ']', ':'];
        $area = str_replace($forbidden, '-', $area);
        $carName = str_replace($forbidden, '-', $carName);

        $title = $area . ' - ' . $carName;
        if (strlen($title) > 31) {
            $available = 31 - 3; // 3 = " - "
            $half = (int) floor($available / 2);
            $title = substr($area, 0, $half) . ' - ' . substr($carName, 0, $available - $half);
        }
        return $title;
    }

    // -------------------------------------------------------------------------
    // HELPERS DE MARCADORES
    // -------------------------------------------------------------------------

    private static function getMarkersMap($sheet) {
        $markersMap = [];
        foreach ($sheet->getRowIterator() as $row) {
            foreach ($row->getCellIterator() as $cell) {
                $val = $cell->getValue();
                if (is_string($val) && strpos($val, '{{') !== false) {
                    preg_match_all('/\{\{.*?\}\}|\{\{.*?\)\)/', $val, $matches);
                    foreach ($matches[0] as $match) {
                        $markersMap[$match][] = $cell->getCoordinate();
                    }
                }
            }
        }
        return $markersMap;
    }

    private static function consumeMarker($sheet, &$markersMap, $marker, $value) {
        // Normalizar caso de typing error (solicitado en el prompt/hallazgo)
        if ($marker === '{{ESTADO_NOOK}}' && empty($markersMap['{{ESTADO_NOOK}}']) && !empty($markersMap['{{ESTADO_NOOK))'])) {
            $marker = '{{ESTADO_NOOK))';
        }

        if (!empty($markersMap[$marker])) {
            $coord = array_shift($markersMap[$marker]);
            $val = $sheet->getCell($coord)->getValue();
            $sheet->setCellValue($coord, str_replace($marker, $value, $val));
            return true;
        }
        return false;
    }
    
    private static function replaceAllMarker($sheet, &$markersMap, $marker, $value) {
        if ($marker === '{{ESTADO_NOOK}}' && empty($markersMap['{{ESTADO_NOOK}}']) && !empty($markersMap['{{ESTADO_NOOK))'])) {
            $marker = '{{ESTADO_NOOK))';
        }

        if (!empty($markersMap[$marker])) {
            foreach ($markersMap[$marker] as $coord) {
                 $val = $sheet->getCell($coord)->getValue();
                 $sheet->setCellValue($coord, str_replace($marker, $value, $val));
            }
            // Vaciar arreglo de este marcador ya que han sido reemplazados todos
            $markersMap[$marker] = [];
        }
    }

    private static function clearRemainingMarkers($sheet, $markersMap) {
        foreach ($markersMap as $marker => $coords) {
            foreach ($coords as $coord) {
                $val = $sheet->getCell($coord)->getValue();
                // Si el valor ya no contiene el marcador (por manipulación cruzada), ignorar
                if (is_string($val)) {
                    $sheet->setCellValue($coord, str_replace($marker, '', $val));
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // LLENADO DE HOJA POR CAR
    // -------------------------------------------------------------------------

    private static function fillCarSheet($sheet, array $car, string $areaName, string $maquila, string $responsable, string $fecha)
    {
        $markersMap = self::getMarkersMap($sheet);

        // Reemplazar marcadores globales en esta hoja
        self::replaceAllMarker($sheet, $markersMap, '{{NOMBRE_MAQ}}', $maquila);
        self::replaceAllMarker($sheet, $markersMap, '{{NOMBRE_REP}}', $responsable);
        self::replaceAllMarker($sheet, $markersMap, '{{NOMBRE_AREA}}', $areaName);
        self::replaceAllMarker($sheet, $markersMap, '{{FECHA}}', $fecha);
        self::replaceAllMarker($sheet, $markersMap, '{{NOMBRE_CAR}}', $car['name']);

        // Iterar propiedades del array details
        foreach ($car['details'] as $detail) {
            $label = (string) $detail['label'];
            $value = (string) $detail['value'];
            $lowV = strtolower(trim($value));

            $estadoStr = $detail['estado'] ?? '';
            if ($estadoStr === 'Satisfactoria') {
                $isBad = false;
                $isNA = false;
            } elseif ($estadoStr === 'No Satisfactoria') {
                $isBad = true;
                $isNA = false;
            } elseif ($estadoStr === 'N/A') {
                $isNA = true;
                $isBad = false;
            } else {
                // Fallback a comportamiento antiguo si no hay estado definido
                $isBad = in_array($lowV, ['no', 'error', '0', 'false', 'malo', 'x', 'n']);
                $isNA = in_array($lowV, ['n/a', 'na', 'no aplica', 'no_aplica', 'n a']);
            }

            $estOk = '';
            $estNa = '';
            $estNook = '';
            $param = '';
            $num = '';

            if ($isNA) {
                $estNa = 'N/A';
            } elseif ($isBad) {
                $estNook = 'N';
            } else {
                $estOk = 'S';
            }

            if (is_numeric($value)) {
                $num = $value;
            } elseif (!$isBad && !$isNA && !in_array($lowV, ['ok', 'si', 'sí', 'yes', 's'])) {
                // Rango o texto corto extra
                $param = $value;
            }

            // Consumir el siguiente espacio de marcador disponible uno por uno
            self::consumeMarker($sheet, $markersMap, '{{NOMBRE_PROP}}', $label);
            self::consumeMarker($sheet, $markersMap, '{{ESTADO_OK}}', $estOk);
            self::consumeMarker($sheet, $markersMap, '{{ESTADO_NA}}', $estNa);
            self::consumeMarker($sheet, $markersMap, '{{ESTADO_NOOK}}', $estNook);
            self::consumeMarker($sheet, $markersMap, '{{PARAMETROS}}', $param);
            self::consumeMarker($sheet, $markersMap, '{{NUM}}', $num);
        }

        // Consumir marcador de observación para esta hoja CAR
        if (!empty($car['obs'])) {
            self::consumeMarker($sheet, $markersMap, '{{OBS_INC}}', $car['obs']);
        }

        // Limpiar los marcadores de propiedades u observaciones que sobraron
        self::clearRemainingMarkers($sheet, $markersMap);
    }

    // -------------------------------------------------------------------------
    // LLENADO DE HOJA DE ACEPTACIÓN
    // -------------------------------------------------------------------------

    private static function fillAcceptanceSheet($sheet, array $allCars, array $globalInfo)
    {
        $markersMap = self::getMarkersMap($sheet);

        // Reemplazar información global
        self::replaceAllMarker($sheet, $markersMap, '{{NOMBRE_MAQ}}', $globalInfo['maquila']);
        self::replaceAllMarker($sheet, $markersMap, '{{NOMBRE_REP}}', $globalInfo['responsable']);
        self::replaceAllMarker($sheet, $markersMap, '{{FECHA}}', $globalInfo['fecha']);

        foreach ($allCars as $entry) {
            $car = $entry['car'];
            $area = $entry['area'];

            $irregularidades = [];
            $recomendaciones = [];

            foreach ($car['details'] as $detail) {
                $lowV = strtolower(trim((string) $detail['value']));
                
                $estadoStr = $detail['estado'] ?? '';
                if ($estadoStr === 'Satisfactoria' || $estadoStr === 'N/A') {
                    $isBad = false;
                } elseif ($estadoStr === 'No Satisfactoria') {
                    $isBad = true;
                } else {
                    $isBad = in_array($lowV, ['no', 'error', '0', 'false', 'malo', 'x', 'n']);
                }
                
                if ($isBad) {
                    $irregularidades[] = $detail['label'];
                    $recomendaciones[] = 'Revisión y corrección de: ' . $detail['label'];
                }
            }

            $obsCompleta = [];
            if (!empty($irregularidades)) {
                $obsCompleta[] = "Irregularidades: " . implode('; ', $irregularidades);
                $obsCompleta[] = "Recomendaciones: " . implode('; ', $recomendaciones);
            } else {
                $obsCompleta[] = "Sin irregularidades. Continuar con mantenimiento preventivo.";
            }

            if (!empty($car['obs'])) {
                $obsCompleta[] = "Obs: " . $car['obs'];
            }
            
            $obsText = implode(' | ', $obsCompleta);
            $nombreCarStr = $area . ' - ' . $car['name'];

            // Consumir los marcadores para la fila de este C.A.R.
            self::consumeMarker($sheet, $markersMap, '{{NOMBRE_CAR}}', $nombreCarStr);
            self::consumeMarker($sheet, $markersMap, '{{OBS_INC}}', $obsText);
        }

        // Limpiar filas sobrantes de C.A.R en la tabla de Aceptación
        self::clearRemainingMarkers($sheet, $markersMap);
    }

    // -------------------------------------------------------------------------
    // ENTRADA PÚBLICA ALTERNATIVA
    // -------------------------------------------------------------------------

    public static function exportSingleReport(array $report, string $filename = 'Nuevo_Detalle_Reporte.xlsx')
    {
        self::exportReports([$report], $filename);
    }
}
