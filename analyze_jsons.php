<?php
require_once 'controllers/reporteController.php';
$rc = new ReporteController();
$res = $rc->getLatest(20);
if ($res['success']) {
    foreach ($res['data'] as $r) {
        $d = json_decode($r['JSON_Reporte'], true);
        $id = $r['Id_Reporte'];
        $has_cr = isset($d['car_reports']) ? 'YES (' . count($d['car_reports']) . ')' : 'NO';
        $has_ac = isset($d['area']['cars']) ? 'YES (' . count($d['area']['cars']) . ')' : 'NO';
        $has_c = isset($d['cars']) ? 'YES (' . count($d['cars']) . ')' : 'NO';
        echo "ID $id: car_reports: $has_cr | area_cars: $has_ac | top_cars: $has_c\n";
    }
}
