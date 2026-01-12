<?php
require_once 'controllers/reporteController.php';
$rc = new ReporteController();
$result = $rc->getLatest(1);
if ($result['success'] && !empty($result['data'])) {
    $report = $result['data'][0];
    file_put_contents('debug_full_json.json', $report['JSON_Reporte']);
    echo "JSON written to debug_full_json.json\n";
} else {
    echo "No reports found.";
}
