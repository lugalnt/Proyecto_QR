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
     * Exporta un reporte individual con sus detalles de CAR (desde JSON).
     * 
     * @param array $report Datos del reporte (fila única).
     * @param string $filename Nombre del archivo.
     */
    public static function exportSingleReport(array $report, string $filename = 'Detalle_Reporte.xlsx')
    {
        $spreadsheet = new Spreadsheet();
        $sheet = $spreadsheet->getActiveSheet();
        $sheet->setTitle('Detalle Reporte');

        // Estilos
        $labelStyle = ['font' => ['bold' => true], 'fill' => ['fillType' => Fill::FILL_SOLID, 'startColor' => ['rgb' => 'E9ECEF']]];
        $headerStyle = [
            'font' => ['bold' => true, 'color' => ['rgb' => 'FFFFFF']],
            'fill' => ['fillType' => Fill::FILL_SOLID, 'startColor' => ['rgb' => '4472C4']],
            'alignment' => ['horizontal' => Alignment::HORIZONTAL_CENTER],
            'borders' => ['allBorders' => ['borderStyle' => Border::BORDER_THIN]]
        ];

        // --- SECCIÓN 1: Información General ---
        $sheet->setCellValue('A1', 'INFORMACIÓN GENERAL DEL REPORTE');
        $sheet->mergeCells('A1:D1');
        $sheet->getStyle('A1')->getFont()->setBold(true)->setSize(14);

        $generalInfo = [
            ['ID Reporte:', $report['Id_Reporte'], 'Fecha:', $report['FechaRegistro_Reporte']],
            ['Maquila:', $report['Nombre_Maquila'] ?? 'N/A', 'Área:', $report['Nombre_Area'] ?? 'N/A'],
            ['Responsable:', $report['Resp_Nombre'] ?? 'N/A', 'Estado:', $report['Estado_Reporte']],
            ['CAR Totales:', $report['CARTotal_Reporte'], 'CAR Revisadas:', $report['CARRevisadas_Reporte']]
        ];

        $currRow = 3;
        foreach ($generalInfo as $infoRow) {
            $sheet->setCellValue('A' . $currRow, $infoRow[0]);
            $sheet->setCellValue('B' . $currRow, $infoRow[1]);
            $sheet->setCellValue('C' . $currRow, $infoRow[2]);
            $sheet->setCellValue('D' . $currRow, $infoRow[3]);
            $sheet->getStyle('A' . $currRow)->applyFromArray($labelStyle);
            $sheet->getStyle('C' . $currRow)->applyFromArray($labelStyle);
            $currRow++;
        }

        // --- SECCIÓN 2: Detalle de Inspección (Tablas por CAR) ---
        $currRow += 2;
        $sheet->setCellValue('A' . $currRow, 'DETALLE DE INSPECCIÓN POR C.A.R.');
        $sheet->mergeCells('A' . $currRow . ':D' . $currRow);
        $sheet->getStyle('A' . $currRow)->getFont()->setBold(true)->setSize(12);
        $currRow += 2;

        // Parsear JSON_Reporte
        $jsonStr = $report['JSON_Reporte'] ?? '';
        $jsonData = json_decode($jsonStr, true);

        $carReports = $jsonData['car_reports'] ?? $jsonData['cars'] ?? $jsonData['area_data']['cars'] ?? $jsonData['area']['cars'] ?? [];

        $hasContent = false;
        if (is_array($carReports)) {
            foreach ($carReports as $car) {
                // Nombre del CAR
                $carName = $car['car_name'] ?? $car['name'] ?? $car['Nombre_CAR'] ?? 'Sin Nombre';
                $observacionGeneral = $car['observacion'] ?? $car['obs'] ?? '';
                $responses = $car['responses'] ?? [];
                $props = $car['properties'] ?? $car['Propiedades'] ?? [];

                // Filtro: si no hay nada, saltar
                if (empty($responses) && empty($props) && empty($observacionGeneral)) {
                    continue;
                }
                $hasContent = true;

                // --- TABLA PARA ESTE CAR ---
                // Encabezado del CAR
                $sheet->setCellValue('A' . $currRow, "C.A.R: " . $carName);
                $sheet->mergeCells('A' . $currRow . ':D' . $currRow);
                $sheet->getStyle('A' . $currRow)->applyFromArray([
                    'font' => ['bold' => true, 'color' => ['rgb' => 'FFFFFF']],
                    'fill' => ['fillType' => Fill::FILL_SOLID, 'startColor' => ['rgb' => '2F5597']]
                ]);
                $currRow++;

                // Sub-encabezados de la tabla
                $sheet->setCellValue('A' . $currRow, 'Propiedad Evaluada');
                $sheet->setCellValue('B' . $currRow, 'Respuesta / Valor');
                $sheet->setCellValue('C' . $currRow, 'Observaciones');
                $sheet->mergeCells('C' . $currRow . ':D' . $currRow); // Combinar para observaciones más largas
                $sheet->getStyle('A' . $currRow . ':D' . $currRow)->applyFromArray($headerStyle);
                $tableStart = $currRow;
                $currRow++;

                // Datos del CAR
                if (!empty($responses) && is_array($responses)) {
                    foreach ($responses as $label => $value) {
                        if (is_bool($value))
                            $value = $value ? 'SÍ / OK' : 'NO / ERROR';
                        if ($value === "1")
                            $value = "SÍ / OK";
                        if ($value === "0")
                            $value = "NO / ERROR";

                        $sheet->setCellValue('A' . $currRow, $label);
                        $sheet->setCellValue('B' . $currRow, $value);
                        $sheet->setCellValue('C' . $currRow, $observacionGeneral);
                        $sheet->mergeCells('C' . $currRow . ':D' . $currRow);
                        $currRow++;
                    }
                } elseif (!empty($props) && is_array($props)) {
                    foreach ($props as $prop) {
                        $propLabel = $prop['label'] ?? $prop['Nombre_Propiedad'] ?? '-';
                        $propValue = $prop['value'] ?? $prop['Valor'] ?? '';
                        if (is_bool($propValue))
                            $propValue = $propValue ? 'SÍ / OK' : 'NO / ERROR';
                        if ($propValue === "1")
                            $propValue = "SÍ / OK";
                        if ($propValue === "0")
                            $propValue = "NO / ERROR";

                        $sheet->setCellValue('A' . $currRow, $propLabel);
                        $sheet->setCellValue('B' . $currRow, $propValue);
                        $sheet->setCellValue('C' . $currRow, $prop['obs'] ?? $observacionGeneral);
                        $sheet->mergeCells('C' . $currRow . ':D' . $currRow);
                        $currRow++;
                    }
                } else {
                    $sheet->setCellValue('A' . $currRow, '(Sin propiedades detalladas)');
                    $sheet->setCellValue('B' . $currRow, 'Reportado');
                    $sheet->setCellValue('C' . $currRow, $observacionGeneral);
                    $sheet->mergeCells('C' . $currRow . ':D' . $currRow);
                    $currRow++;
                }

                // Bordes para esta pequeña tabla
                $sheet->getStyle('A' . $tableStart . ':D' . ($currRow - 1))->getBorders()->getAllBorders()->setBorderStyle(Border::BORDER_THIN);

                $currRow += 2; // Espacio entre tablas de CAR
            }
        }

        if (!$hasContent) {
            $sheet->setCellValue('A' . $currRow, 'No hay inspecciones detalladas guardadas en este reporte.');
            $sheet->mergeCells('A' . $currRow . ':D' . $currRow);
            $currRow++;
        }

        // Estética final
        foreach (range('A', 'D') as $colID) {
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
}
