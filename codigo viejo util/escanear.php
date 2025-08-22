<?php
session_start();
require_once('conn.php');
if (!isset($_SESSION['Nombre_Usuario']) || !isset($_SESSION['Id_Usuario'])) {
    header("Location: login.php");
    exit();
}
?>
<!DOCTYPE html>
<html>
<head>
<title> Escanea el codigo - FCS</title>
<link rel="stylesheet" href="css/styleScanner.css">
</head>
<body>

<script src="js/html5-qrcode.min.js"></script>
<div id="reader"></div>
<div id="result"></div>
<br>
<br>
<div class="container mt-4">
    <h1> Herramientas escaneadas</h1>
    <ul>
    </ul>

    
</div>

<script>
function onScanSuccess(decodedText, decodedResult) {
  // handle the scanned code as you like, for example:
console.log(`Code matched = ${decodedText}`, decodedResult);
document.getElementById('result').innerHTML = `Code matched = ${decodedText}`;

fetch('buscar_herramienta.php', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `codigo=${encodeURIComponent(decodedText)}`,
    })
    .then(response => response.json())
    .then(data => {
    if (data.success) {
        const ul = document.querySelector('.container ul');
        const existingItem = Array.from(ul.children).find(li => li.textContent.includes(data.data.Nombre_Herramienta));
        if (!existingItem) {
        const li = document.createElement('li');
        li.textContent = `Código: ${data.data.Nombre_Herramienta}, Cantidad: ${data.data.Cantidad_Herramienta}`;
        ul.appendChild(li);
        }
    } else {
        document.getElementById('result').innerHTML = `Error: ${data.message}`;
    }
    })
    .catch(error => {
    console.error('Error:', error);
    document.getElementById('result').innerHTML = 'Error al buscar el código.';
    });

}

function onScanFailure(error) {
  // handle scan failure, usually better to ignore and keep scanning.
  // for example:
  console.warn(`Code scan error = ${error}`);
}

let html5QrcodeScanner = new Html5QrcodeScanner(
  "reader",
  { fps: 10, qrbox: {width: 500, height: 500} },
  /* verbose= */ false);
html5QrcodeScanner.render(onScanSuccess, onScanFailure);
</script>



</body>
</html>

<?php
?>

