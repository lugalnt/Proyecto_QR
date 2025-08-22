<?php
session_start();
if (!isset($_SESSION['Id_Usuario'])) {
    header("Location: login.php");
    exit();
}
if ($_SESSION['Id_Usuario'] != 1) {
    header("Location: escanear.php");
    exit();
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Menú Principal</title>
    <link rel="stylesheet" href="css/styleIndex.css">
</head>
<body>
    <nav class="header">
        <span class="nav-brand">Bienvenido: <?= htmlspecialchars($_SESSION['Nombre_Usuario']) ?></span>
        <a href="login.php" class="logout-button">Salir</a>
    </nav>

    <div class="main-container">
        <div class="button-grid">
            <button class="action-btn" onclick="showDiv('div1')">1</button>
            <button class="action-btn" onclick="showDiv('div2')">2</button>
            <button class="action-btn" onclick="showDiv('div3')">3</button>
            <button class="action-btn" onclick="showDiv('div4')">4</button>
        </div>

        <div id="div1" class="content-panel">
            <h1>1</h1>
        </div>
        <div id="div2" class="content-panel">
            <h1>2</h1>
        </div>
        <div id="div3" class="content-panel">
            <h1>3</h1>
        </div>
        <div id="div4" class="content-panel">
            <h1>4</h1>
        </div>
    </div>

    <script>
        function showDiv(divId) {
            // Ocultar todos los paneles
            document.querySelectorAll('.content-panel').forEach(panel => {
                panel.style.display = 'none';
            });
            // Mostrar el panel seleccionado
            document.getElementById(divId).style.display = 'block';
        }

        // Mostrar el panel guardado en sesión (o el primero por defecto)
        <?php if (isset($_SESSION['divID'])): ?>
            showDiv('div<?= $_SESSION['divID'] ?>');
        <?php else: ?>
            showDiv('div1');
        <?php endif; ?>
    </script>

    <?php
    require_once('conn.php');
    ?>
</body>
</html>
