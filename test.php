<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <form method="POST" action="">
        <input type="text" placeholder="nombre" name="nombre">
        <button type="submit">crear</button>
    </form>
</body>
</html>

<?php

require("conn.php");

if($_SERVER["REQUEST_METHOD"] == "POST")
{
$nombreTabla = $_POST['nombre'];

if (!preg_match('/^[a-zA-Z0-9_]+$/', $nombreTabla)) {
    die("Nombre de tabla inválido");
}

$sql = "CREATE TABLE `$nombreTabla` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
)";

if ($conn->query($sql) === TRUE) {
    echo '<h1>Tabla creada correctamente</h1>';
} else {
    echo "Error al crear la tabla: " . $conn->error;
}

}
?>