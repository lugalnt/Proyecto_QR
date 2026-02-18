<?php
// Script to generate a large SQL query for testing

// Generar 100 CARs de prueba
$cars = [];
for ($i = 1; $i <= 100; $i++) {
    $cars[] = [
        'car_name' => "CAR-" . str_pad($i, 3, '0', STR_PAD_LEFT) . " - Componente " . chr(65 + ($i % 26)),
        'responses' => [
            'Inspección Visual' => 'OK',
            'Funcionamiento' => ($i % 5 === 0) ? 'NO' : 'OK', // Algunos con error
            'Medida (mm)' => rand(10, 50)
        ],
        'observacion' => ($i % 10 === 0) ? "Observación crítica en CAR-$i" : "Sin observaciones relevantes."
    ];
}

$jsonData = [
    'car_reports' => $cars,
    'meta' => [
        'generated_by' => 'TestScript',
        'items_count' => count($cars)
    ]
];

$jsonString = json_encode($jsonData, JSON_UNESCAPED_UNICODE);
// Escapar comillas simples para SQL
$jsonSQL = str_replace("'", "''", $jsonString);

$sql = "
START TRANSACTION;

-- Insertar en la tabla reporte
INSERT INTO `reporte` (`FechaRegistro_Reporte`, `CARTotal_Reporte`, `CARRevisadas_Reporte`, `Estado_Reporte`, `JSON_Reporte`)
VALUES (NOW(), 100, 100, 'Finalizado', '$jsonSQL');

-- Obtener el ID generado
SET @new_report_id = LAST_INSERT_ID();

-- Insertar en area_reporte (Asumiendo Id_Area=1, que pertenece a Maquila 2)
-- Si Id_Area=1 no existe, ajustar según la base de datos (según fcs_not_clean.sql existe AreaPrueba con ID 1)
INSERT INTO `area_reporte` (`Id_Reporte`, `Id_Area`, `Nombre_Area`, `NumeroCAR_Area`, `FechaRegistro_Reporte`)
VALUES (@new_report_id, 1, 'AreaPrueba', 100, NOW());

-- Insertar en usuario_reporte (Asumiendo Id_Usuario=1 'Yoz')
INSERT INTO `usuario_reporte` (`Id_Usuario`, `Id_Reporte`, `FechaRegistro_Reporte`)
VALUES (1, @new_report_id, NOW());

COMMIT;

SELECT @new_report_id as 'Reporte Creado con ID';
";

file_put_contents('test_data_insert.sql', $sql);
echo "Archivo SQL generado: test_data_insert.sql\n";
?>