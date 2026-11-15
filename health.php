<?php

declare(strict_types=1);

header('Content-Type: application/json; charset=utf-8');
http_response_code(200);

echo json_encode([
    'status' => 'healthy',
    'service' => 'Panel de Control y Reportes F.C.S.',
    'php_version' => PHP_VERSION,
    'timestamp' => date('c'),
]);
