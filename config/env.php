<?php
// config/env.php
// Carga el archivo .env de la raíz del proyecto en $_ENV y getenv().
// Compatible con PHP 7.2+

function loadEnv(string $path): void
{
    if (!file_exists($path)) {
        return; // Si no existe .env, no hace nada
    }

    $lines = file($path, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    foreach ($lines as $line) {
        $trimmed = trim($line);

        // Ignorar comentarios y líneas vacías
        if ($trimmed === '' || $trimmed[0] === '#') {
            continue;
        }

        if (strpos($trimmed, '=') === false) {
            continue;
        }

        [$key, $value] = explode('=', $trimmed, 2);
        $key = trim($key);
        $value = trim($value);

        // Quitar comillas si las hay
        $len = strlen($value);
        if ($len >= 2) {
            $first = $value[0];
            $last = $value[$len - 1];
            if (($first === '"' && $last === '"') || ($first === "'" && $last === "'")) {
                $value = substr($value, 1, -1);
            }
        }

        if (!array_key_exists($key, $_ENV)) {
            $_ENV[$key] = $value;
            putenv("$key=$value");
        }
    }
}

// Ejecutar automáticamente al incluir este archivo
loadEnv(dirname(__DIR__) . '/.env');
