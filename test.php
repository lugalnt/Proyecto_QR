<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

echo "<h1>Diagnóstico de Entorno (Hostinger Subdominio)</h1>";

// 1. PHP Version
echo "<h2>1. Versión de PHP: " . PHP_VERSION . "</h2>";
if (version_compare(PHP_VERSION, '7.4.0') >= 0) {
    echo "<p style='color:green'>✅ Versión compatible (>= 7.4)</p>";
} else {
    echo "<p style='color:red'>❌ Versión NO compatible. Se requiere PHP 7.4+ por las Typed Properties.</p>";
}

// 2. Rutas y Archivos
echo "<h2>2. Rutas de Archivos</h2>";
echo "<ul>";
echo "<li>Document Root: " . $_SERVER['DOCUMENT_ROOT'] . "</li>";
echo "<li>__DIR__: " . __DIR__ . "</li>";
echo "<li>.env existe: " . (file_exists(__DIR__ . '/.env') ? '✅ SI' : '❌ NO') . "</li>";
echo "<li>vendor/autoload existe: " . (file_exists(__DIR__ . '/vendor/autoload.php') ? '✅ SI' : '❌ NO') . "</li>";
echo "</ul>";

// 3. Extensiones Críticas
echo "<h2>3. Extensiones Requeridas</h2>";
$extensions = ['gd', 'mbstring', 'zip', 'xml', 'pdo_mysql', 'openssl'];
echo "<ul>";
foreach ($extensions as $ext) {
    $status = extension_loaded($ext) ? "<span style='color:green'>✅ Cargada</span>" : "<span style='color:red'>❌ NO ENCONTRADA</span>";
    echo "<li>$ext: $status</li>";
}
echo "</ul>";

// 4. Test de Sesión
echo "<h2>4. Sistema de Sesiones</h2>";
$sessPath = session_save_path();
echo "<p>Session save path: " . ($sessPath ?: '(vacío)') . "</p>";
if (@session_start()) {
    echo "<p style='color:green'>✅ Session iniciada correctamente</p>";
} else {
    echo "<p style='color:red'>❌ Error al iniciar sesión</p>";
}

// 5. Test de Base de Datos (.env)
echo "<h2>5. Conexión a Base de Datos</h2>";
require_once __DIR__ . '/config/env.php';
$host = getenv('DB_HOST');
$name = getenv('DB_NAME');
$user = getenv('DB_USER');

if (!$host) {
    echo "<p style='color:red'>❌ Error: .env no cargó variables de entorno.</p>";
} else {
    echo "<ul>";
    echo "<li>Host: $host</li>";
    echo "<li>Nombre BD: $name</li>";
    echo "<li>Usuario BD: $user</li>";
    echo "</ul>";

    try {
        require_once __DIR__ . '/config/db.php';
        $pdo = \Config\DB::getConnection();
        echo "<h3 style='color:green'>✅ Conexión a Base de Datos: EXITOSA</h3>";
    } catch (\Throwable $e) {
        echo "<h3 style='color:red'>❌ Error de Conexión: " . $e->getMessage() . "</h3>";
    }
}

echo "<hr><p style='color:orange'>⚠️ Recuerda borrar este archivo (test.php) después de usarlo.</p>";
?>