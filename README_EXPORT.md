# Módulo de Exportación a Excel (Reportes)

Este módulo permite exportar los reportes filtrados directamente a un archivo de Excel nativo (.xlsx).

## Características
- **Exportación a XLSX**: Genera archivos compatibles con Excel modernos.
- **Formato Profesional**: Incluye encabezados estilizados, columnas auto-ajustables y porcentajes calculados.
- **Respeto de Filtros**: La exportación utiliza los mismos filtros que tienes aplicados en la tabla de reportes.
- **Exportación Individual**: Capacidad de exportar un reporte específico con el detalle de todas las respuestas contestadas por CAR.
- **Escalable**: Puede manejar miles de reportes eficientemente.

## Cómo Usar

### Exportación Masiva (Filtros)
1. Ve a la sección de **Reportes**.
2. Aplica los filtros deseados.
3. Haz clic en el botón verde superior **"Exportar a Excel"**.

### Exportación Individual (Detalle CAR)
1. En la tabla de reportes, localiza el reporte que deseas exportar.
2. Haz clic en el botón verde **"Excel"** que se encuentra en la columna "Detalle", junto al botón de "Ver Respuestas".
3. Se generará un archivo detallado con la información general y el listado de cada C.A.R. con su respuesta específica.

## Estructura de Datos
El archivo exportado contiene las siguientes columnas:
- **ID Reporte**: Identificador único.
- **Fecha Registro**: Cuándo se creó el reporte.
- **Estado**: Estado actual (Abierto, OK, NOK, etc.).
- **Area**: Nombre del área inspeccionada.
- **Maquila**: Maquila a la que pertenece el área.
- **Responsable**: Nombre del usuario que realizó la inspección.
- **CAR Totales**: Cantidad total de "Cosas A Revisar" definidas para el área.
- **CAR Revisadas**: Cuántas CARs fueron contestadas en este reporte.
- **% Avance**: Porcentaje de cumplimiento del reporte.

## Requisitos Técnicos
Para que este módulo funcione, el servidor PHP debe tener habilitada la extensión **zip**. 
- Si el botón de exportación te da un error de "ZipArchive not found", asegúrate de descomentar `extension=zip` en tu `php.ini` y reiniciar el servidor.

---

### Adaptación a Plantillas Futuras
El código está preparado en `utils/ExcelExporter.php`. Si en el futuro deseas usar una plantilla pre-diseñada (por ejemplo, para cargar datos en celdas específicas de un Excel ya hecho):
1. Sube tu plantilla (.xlsx) al servidor.
2. En `ExcelExporter.php`, usa `$spreadsheet = \PhpOffice\PhpSpreadsheet\IOFactory::load('tu_plantilla.xlsx');` en lugar de `new Spreadsheet();`.
3. Selecciona la celda deseada con `$sheet->setCellValue('C10', $dato);`.
