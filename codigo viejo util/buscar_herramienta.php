<?php
require_once('conn.php');

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['codigo'])) {
    $codigo = $_POST['codigo'];

    // Consulta a la base de datos
    $query = $conn->prepare("SELECT * FROM herramienta WHERE Codigo_Herramienta = ?");
    $query->bind_param("s", $codigo);
    $query->execute();
    $result = $query->get_result();

    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        echo json_encode(['success' => true, 'data' => $row]);
    } else {
        echo json_encode(['success' => false, 'message' => 'Código no encontrado.']);
    }
} else {
    echo json_encode(['success' => false, 'message' => 'Solicitud inválida.']);
}
?>