<?php
require_once('conn.php');
session_start();

if($_SERVER['REQUEST_METHOD'] == 'POST') {
$nombre = $_POST['nombreHerramienta'];
$cantidad = $_POST['cantidadHerramienta'];
$cantidadActual = $_POST['cantidadActualHerramienta'];
$codigo = $_POST['modificar'];

$queryMH = $conn->prepare("UPDATE herramienta SET Nombre_Herramienta = ?, Cantidad_Herramienta = ?, CantidadActual_Herramienta = ? WHERE Codigo_Herramienta = ?");
$queryMH->bind_param("siis", $nombre, $cantidad, $cantidadActual, $codigo);

if ($queryMH->execute()) {
    echo "<script>alert('Herramienta modificada exitosamente');</script>";
    $_SESSION['divID'] = 2; 
    echo "<script>window.location.href = 'menuControl.php';</script>";
} else {
    echo "<script>alert('Error al modificar la herramienta');</script>";
}


}



?>