<?php
// config/env.php
// Carga el archivo .env de la raíz del proyecto en $_ENV y getenv().

function loadEnv(string $path): void
{
    if (!file_exists($path)) {
        return; // Si no existe .env, no hace nada (usa vars ya definidas)
    }

    $lines = file($path, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    foreach ($lines as $line) {
        // Ignorar comentarios
        if (str_starts_with(trim($line), '#')) {
            continue;
        }

        if (!str_contains($line, '=')) {
            continue;
        }

        [$key, $value] = explode('=', $line, 2);
        $key = trim($key);
        $value = trim($value);

        // Quitar comillas si las hay
        if (
            (str_starts_with($value, '"') && str_ends_with($value, '"')) ||
            (str_starts_with($value, "'") && str_ends_with($value, "'"))
        ) {
            $value = substr($value, 1, -1);
        }

        if (!array_key_exists($key, $_ENV)) {
            $_ENV[$key] = $value;
            putenv("$key=$value");
        }
    }
}

// Ejecutar automáticamente al incluir este archivo
loadEnv(dirname(__DIR__) . '/.env');
