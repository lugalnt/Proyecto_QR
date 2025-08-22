<?php
session_start();
require_once('conn.php');

if($_SERVER['REQUEST_METHOD'] == 'POST') {
    $codigo = $_POST['eliminar'];
    
    // Eliminar la herramienta de la base de datos
    $queryBH = $conn->prepare("DELETE FROM herramienta WHERE Codigo_Herramienta = ?");
    $queryBH->bind_param("s", $codigo);
    
    if ($queryBH->execute()) {
        echo "<script>alert('Herramienta eliminada exitosamente');</script>";
        $_SESSION['divID'] = 2; 
        echo "<script>window.location.href = 'menuControl.php';</script>";
    } else {
        echo "<script>alert('Error al eliminar la herramienta');</script>";
    }
}

?>