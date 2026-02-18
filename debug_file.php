<?php
$file = 'test_export_result.xlsx';
if (!file_exists($file)) {
    die("File not found\n");
}
$content = file_get_contents($file);
echo "File size: " . strlen($content) . "\n";
echo "First 500 bytes (hex):\n";
echo bin2hex(substr($content, 0, 500)) . "\n";
echo "\nFirst 500 bytes (raw):\n";
echo substr($content, 0, 500) . "\n";
