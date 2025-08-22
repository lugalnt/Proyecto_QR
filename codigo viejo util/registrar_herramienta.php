<html>
<head>
<title> FCS - GENERANDO CODIGO QR</title>
<link rel="stylesheet" href="css/styleIndex.css">
</head>

<body>



<?php

session_start();
require_once("conn.php");

require("vendor/autoload.php");

use Endroid\QrCode\Color\Color;
use Endroid\QrCode\Encoding\Encoding;
use Endroid\QrCode\ErrorCorrectionLevel;
use Endroid\QrCode\QrCode;
use Endroid\QrCode\Label\Label;
use Endroid\QrCode\Logo\Logo;
use Endroid\QrCode\RoundBlockSizeMode;
use Endroid\QrCode\Writer\PngWriter;
use Endroid\QrCode\Writer\ValidationException;

if($_SERVER["REQUEST_METHOD"] == "POST")
{
    $nombre = $_POST['nombre'];
    $cantidad = $_POST['cantidad'];

    $queryCE = $conn->prepare("SELECT Nombre_Herramienta FROM herramienta WHERE Nombre_Herramienta = ?");
    $queryCE->bind_param("s", $nombre);
    $queryCE->execute();
    $resultCE = $queryCE->get_result();
    if ($resultCE->num_rows > 0) {
        echo "<script>alert('La herramienta ya existe.');</script>";
        echo "<script>window.location.href = 'index.php';</script>";
        exit;
    }

    do {
        $codigo = substr(bin2hex(random_bytes(8)), 0, 15); 

        $queryGC = $conn->prepare("SELECT Codigo_Herramienta FROM herramienta WHERE Codigo_Herramienta = ?");
        $queryGC->bind_param("s", $codigo);
        $queryGC->execute();
        $resultGC = $queryGC->get_result();
    } while ($resultGC->num_rows > 0);

    $queryIH = $conn->prepare("INSERT INTO herramienta (Nombre_Herramienta, Cantidad_Herramienta, CantidadActual_Herramienta, Codigo_Herramienta) VALUES (?, ?, ?, ?)");
    $queryIH->bind_param("siis", $nombre, $cantidad, $cantidad, $codigo);
    if ($queryIH->execute()) {

        $writer = new PngWriter();
        $qrCode = new QrCode(
            data: $codigo,
            encoding: new Encoding('UTF-8'),
            errorCorrectionLevel: ErrorCorrectionLevel::Low,
            size: 300,
            margin: 10,
            roundBlockSizeMode: RoundBlockSizeMode::Margin,
            foregroundColor: new Color(0, 0, 0),
            backgroundColor: new Color(255, 255, 255)
        );


        $result = $writer->write($qrCode);

        $fileName = 'qrcodes/' . $codigo . '.png';
        file_put_contents($fileName, $result->getString());

        echo "<script>alert('Herramienta registrada exitosamente. Código QR generado.');</script>";

        echo '
        <div class="container mt-4 text-center">
            <center>
            <h1>Herramienta Registrada</h1>
            <p class="mt-3">Favor de guardar el siguiente código QR:</p>
            <img src="' . $fileName . '" alt="Código QR" style="width: 300px; height: 300px;" class="mt-3">
            <div class="mt-4">
            <a href="' . $fileName . '" download class="btn btn-primary">Descargar QR</a>
            <a href="menuControl.php" class="btn btn-secondary">Regresar al menú</a>
            </div>
            </center>
        </div>
        ';

        $_SESSION['divID'] = 4; 



    } else {
        echo "<script>alert('Error al registrar la herramienta.');</script>";
    }

}

?>