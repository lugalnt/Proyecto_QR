<?php

namespace Utils;

require_once __DIR__ . '/../vendor/autoload.php';

use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;
use PhpOffice\PhpSpreadsheet\Style\Alignment;
use PhpOffice\PhpSpreadsheet\Style\Border;
use PhpOffice\PhpSpreadsheet\Style\Fill;

class ExcelExporter
{
    /**
     * Exporta datos de reportes a un archivo XLSX.
     * 
     * @param array $rows Datos de los reportes.
     * @param string $filename Nombre del archivo de salida.
     */
    public static function exportReports(array $rows, string $filename = 'Reporte_QR.xlsx')
    {
        $spreadsheet = new Spreadsheet();
        $sheet = $spreadsheet->getActiveSheet();
        $sheet->setTitle('Reportes');

        // Definir encabezados
        $headers = [
            'ID Reporte',
            'Fecha Registro',
            'Estado',
            'Area',
            'Maquila',
            'Responsable',
            'CAR Totales',
            'CAR Revisadas',
            '% Avance'
        ];

        // Estilo para encabezados
        $headerStyle = [
            'font' => [
                'bold' => true,
                'color' => ['rgb' => 'FFFFFF'],
            ],
            'alignment' => [
                'horizontal' => Alignment::HORIZONTAL_CENTER,
            ],
            'fill' => [
                'fillType' => Fill::FILL_SOLID,
                'startColor' => ['rgb' => '4472C4'],
            ],
            'borders' => [
                'allBorders' => [
                    'borderStyle' => Border::BORDER_THIN,
                ],
            ],
        ];

        // Escribir encabezados
        $col = 'A';
        foreach ($headers as $header) {
            $sheet->setCellValue($col . '1', $header);
            $col++;
        }
        $sheet->getStyle('A1:I1')->applyFromArray($headerStyle);

        // Escribir datos
        $rowNum = 2;
        foreach ($rows as $row) {
            $avance = ($row['CARTotal_Reporte'] > 0)
                ? round(($row['CARRevisadas_Reporte'] / $row['CARTotal_Reporte']) * 100, 2) . '%'
                : '0%';

            $sheet->setCellValue('A' . $rowNum, $row['Id_Reporte']);
            $sheet->setCellValue('B' . $rowNum, $row['FechaRegistro_Reporte']);
            $sheet->setCellValue('C' . $rowNum, $row['Estado_Reporte']);
            $sheet->setCellValue('D' . $rowNum, $row['Nombre_Area'] ?? 'N/A');
            $sheet->setCellValue('E' . $rowNum, $row['Nombre_Maquila'] ?? 'N/A');
            $sheet->setCellValue('F' . $rowNum, $row['Resp_Nombre'] ?? 'N/A');
            $sheet->setCellValue('G' . $rowNum, $row['CARTotal_Reporte']);
            $sheet->setCellValue('H' . $rowNum, $row['CARRevisadas_Reporte']);
            $sheet->setCellValue('I' . $rowNum, $avance);

            // Alineación de datos
            $sheet->getStyle('A' . $rowNum . ':I' . $rowNum)->getAlignment()->setHorizontal(Alignment::HORIZONTAL_LEFT);
            $rowNum++;
        }

        // Auto-ajustar columnas
        foreach (range('A', 'I') as $colID) {
            $sheet->getColumnDimension($colID)->setAutoSize(true);
        }

        // Configurar cabeceras HTTP para descarga
        header('Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
        header('Content-Disposition: attachment;filename="' . $filename . '"');
        header('Cache-Control: max-age=0');

        $writer = new Xlsx($spreadsheet);
        $writer->save('php://output');
        exit;
    }

    /**
     * Exporta un reporte individual con sus detalles de CAR (desde JSON) usando PLANTILLA.
     * 
     * @param array $report Datos del reporte (fila única).
     * @param string $filename Nombre del archivo.
     */
    public static function exportSingleReport(array $report, string $filename = 'Detalle_Reporte.xlsx')
    {
        // Limpiar cualquier salida previa
        if (ob_get_length())
            ob_end_clean();

        // Cargar plantilla
        $templatePath = __DIR__ . '/plantillaReportes.xlsx';
        if (!file_exists($templatePath)) {
            throw new \Exception("La plantilla no existe en: " . $templatePath);
        }

        $spreadsheet = \PhpOffice\PhpSpreadsheet\IOFactory::load($templatePath);

        // --- LIMPIEZA DE NOMBRES DEFINIDOS ---
        $definedNames = $spreadsheet->getDefinedNames();
        foreach ($definedNames as $name) {
            $spreadsheet->removeDefinedName($name->getName());
        }

        $sheet = $spreadsheet->getActiveSheet();

        // --- 1. Mapeo de Encabezados ---
        $sheet->setCellValue('C8', $report['Nombre_Maquila'] ?? '');
        $sheet->setCellValue('C9', ($report['Nombre_Area'] ?? '') . ' / ' . ($report['Resp_Nombre'] ?? ''));
        $sheet->setCellValue('C10', 'Inspección y Retrabajo (QR)');
        $sheet->setCellValue('Q9', $report['Resp_Nombre'] ?? '');
        $fecha = $report['FechaRegistro_Reporte'] ?? date('Y-m-d');
        $sheet->setCellValue('Q10', $fecha);

        // --- 2. Preparar Datos ---
        $jsonStr = $report['JSON_Reporte'] ?? '';
        $jsonData = json_decode($jsonStr, true);
        $carReports = $jsonData['car_reports'] ?? $jsonData['cars'] ?? $jsonData['area_data']['cars'] ?? $jsonData['area']['cars'] ?? [];

        $itemsToPrint = [];

        if (is_array($carReports)) {
            foreach ($carReports as $car) {
                $carName = $car['car_name'] ?? $car['name'] ?? $car['Nombre_CAR'] ?? 'Sin Nombre';
                $observacionGeneral = $car['observacion'] ?? $car['obs'] ?? '';
                $responses = $car['responses'] ?? [];
                $props = $car['properties'] ?? $car['Propiedades'] ?? [];

                $subItems = [];
                if (!empty($responses) && is_array($responses)) {
                    foreach ($responses as $label => $value) {
                        if (is_bool($value))
                            $value = $value ? 'SÍ / OK' : 'NO / ERROR';
                        if ($value === "1")
                            $value = "SÍ / OK";
                        if ($value === "0")
                            $value = "NO / ERROR";
                        $subItems[] = ['label' => $label, 'value' => $value, 'obs' => $observacionGeneral];
                    }
                } elseif (!empty($props) && is_array($props)) {
                    foreach ($props as $prop) {
                        $label = $prop['label'] ?? $prop['Nombre_Propiedad'] ?? '-';
                        $value = $prop['value'] ?? $prop['Valor'] ?? '';
                        if (is_bool($value))
                            $value = $value ? 'SÍ / OK' : 'NO / ERROR';
                        if ($value === "1")
                            $value = "SÍ / OK";
                        if ($value === "0")
                            $value = "NO / ERROR";
                        $subItems[] = ['label' => $label, 'value' => $value, 'obs' => $prop['obs'] ?? $observacionGeneral];
                    }
                } else {
                    $subItems[] = ['label' => '(General)', 'value' => 'Revisado', 'obs' => $observacionGeneral];
                }

                foreach ($subItems as $item) {
                    $itemsToPrint[] = [
                        'carName' => $carName,
                        'detalle' => $item['label'] . ": " . $item['value'],
                        'obs' => $item['obs'],
                        'fecha' => $fecha
                    ];
                }
            }
        }

        // --- 3. Inserción Dinámica de Filas ---
        $startRow = 14;

        // Buscar footer
        $highestRow = $sheet->getHighestRow();
        $footerRowIndex = 62;
        for ($r = 15; $r <= $highestRow; $r++) {
            $val = $sheet->getCell('B' . $r)->getValue();
            if ($val && stripos((string) $val, 'Observaciones') !== false) {
                $footerRowIndex = $r;
                break;
            }
        }

        $availableRows = $footerRowIndex - $startRow;
        $neededRows = count($itemsToPrint);

        if ($neededRows > $availableRows) {
            $rowsToAdd = $neededRows - $availableRows + 2;
            $sheet->insertNewRowBefore($footerRowIndex, $rowsToAdd);
        }

        // --- 4. Escribir Datos ---
        $currentRow = $startRow;

        foreach ($itemsToPrint as $item) {
            $sheet->setCellValue('B' . $currentRow, $item['carName']);
            $sheet->setCellValue('D' . $currentRow, $item['detalle']);
            $sheet->setCellValue('K' . $currentRow, $item['obs']);
            $sheet->setCellValue('R' . $currentRow, $item['fecha']);

            // Auto altura
            $sheet->getRowDimension($currentRow)->setRowHeight(-1);
            $currentRow++;
        }

        // --- Output ---
        header('Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
        header('Content-Disposition: attachment;filename="' . $filename . '"');
        header('Cache-Control: max-age=0');

        $writer = new \PhpOffice\PhpSpreadsheet\Writer\Xlsx($spreadsheet);
        $writer->save('php://output');
        exit;
    }
}
