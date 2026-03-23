<?php

require_once __DIR__ . '/config/env.php';
require_once __DIR__ . '/config/db.php';

try {
    $pdo = \Config\DB::getConnection();
    
    // Check if column exists in usuario
    $stmt = $pdo->query("SHOW COLUMNS FROM `usuario` LIKE 'Email_Usuario'");
    if ($stmt->rowCount() == 0) {
        $pdo->exec("ALTER TABLE `usuario` ADD `Email_Usuario` VARCHAR(255) NULL DEFAULT NULL AFTER `Puesto_Usuario`");
        echo "Column Email_Usuario added to usuario table.\n";
    } else {
        echo "Column Email_Usuario already exists.\n";
    }

    // Check if column exists in maquila
    $stmt = $pdo->query("SHOW COLUMNS FROM `maquila` LIKE 'Email_Maquila'");
    if ($stmt->rowCount() == 0) {
        $pdo->exec("ALTER TABLE `maquila` ADD `Email_Maquila` VARCHAR(255) NULL DEFAULT NULL AFTER `Contraseña_Maquila`");
        echo "Column Email_Maquila added to maquila table.\n";
    } else {
        echo "Column Email_Maquila already exists.\n";
    }

} catch (Exception $e) {
    echo "Error updating database: " . $e->getMessage() . "\n";
}
