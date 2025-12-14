<?php
// controllers/maquilaController.php
// Controlador específico para entidad 'maquila' que usa BaseController.

require_once __DIR__ . '/baseController.php';
require_once __DIR__ . '/../vendor/autoload.php';

use Endroid\QrCode\Color\Color;
use Endroid\QrCode\Encoding\Encoding;
use Endroid\QrCode\ErrorCorrectionLevel;
use Endroid\QrCode\QrCode;
use Endroid\QrCode\Label\Label;
use Endroid\QrCode\RoundBlockSizeMode;
use Endroid\QrCode\Writer\PngWriter;

class AreaController
{
    private BaseController $base;

    // Indica las columnas en el orden para CSV si quieres usar registrar con string
    private array $fields = ['Nombre_Area', 'Descripcion_Area', 'NumeroCAR_Area', 'Descripcion_Area', 'JSON_Area'];

    public function __construct()
    {
        $this->base = new BaseController('area', 'Id_Area', $this->fields);
    }

    public function registrar($payload)
    {

        $data = is_string($payload) ? $payload : (array) $payload;

        $existe = $this->obtenerPor('Nombre_Area', $data['Nombre_Area']);
        if ($existe) {
            throw new \InvalidArgumentException('El Area ya existe, elige otro nombre.');
        }

        do {
            $codigo = substr(bin2hex(random_bytes(8)), 0, 15);
            $existeCodigo = $this->obtenerPor("Codigo_Area", $codigo);
        } while ($existeCodigo);

        $data['Codigo_Area'] = $codigo;
        $textolabel = $data['Nombre_Area'] . ' ' . $data['Descripcion_Area'];
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

        $label = new Label(
            text: $textolabel,
            textColor: new Color(0, 0, 0)
        );

        $result = $writer->write($qrCode, null, $label);

        $fileName = __DIR__ . '/../qrcodes/' . $codigo . '.png';
        file_put_contents($fileName, $result->getString());

        return $this->base->registrar($data);

    }

    public function obtenerTodos(string $where = '', array $params = [])
    {
        if ($where === '')
            $where = 'deleted_at IS NULL OR deleted_at = ""';
        else
            $where .= ' AND (deleted_at IS NULL OR deleted_at = "")';
        return $this->base->obtenerTodos($where, $params);
    }

    public function obtenerPor(string $campo, $valor)
    {
        return $this->base->obtenerPor($campo, $valor);
    }


    public function editarArea($payload)
    {
        // Acepta string JSON o array/obj
        $data = is_string($payload) ? json_decode($payload, true) : (array) $payload;
        if ($data === null && is_string($payload)) {
            throw new \InvalidArgumentException('Payload JSON inválido: ' . json_last_error_msg());
        }

        // Validaciones mínimas
        if (empty($data['Id_Area'])) {
            throw new \InvalidArgumentException('Id_Area requerido para editar');
        }
        if (empty($data['Nombre_Area'])) {
            throw new \InvalidArgumentException('Nombre requerido');
        }

        $idArea = intval($data['Id_Area']);
        $nombre = trim($data['Nombre_Area']);
        $descripcion = trim($data['Descripcion_Area'] ?? '');

        // JSON_Area: puede venir como string JSON o como estructura ya decodificada
        $jsonAreaRaw = $data['JSON_Area'] ?? '[]';
        $decoded = is_string($jsonAreaRaw) ? json_decode($jsonAreaRaw, true) : (array) $jsonAreaRaw;
        if ($decoded === null && is_string($jsonAreaRaw)) {
            throw new \InvalidArgumentException('JSON_Area inválido: ' . json_last_error_msg());
        }

        // Normalizar: esperar { "cars": [...] } o una lista directa de cars
        if (!isset($decoded['cars'])) {
            if (array_values($decoded) === $decoded) {
                $decoded = ['cars' => $decoded];
            } else {
                $decoded = ['cars' => []];
            }
        }
        $numCarsReal = is_array($decoded['cars']) ? count($decoded['cars']) : 0;

        // Sincronizar NumeroCAR_Area con el JSON (ignoramos intentos de cambiar Id o Codigo)
        $numeroCAR = $numCarsReal;

        // Preparar JSON a guardar
        $jsonToStore = json_encode($decoded, JSON_UNESCAPED_UNICODE);

        // Construir datos para actualizar (NO incluimos Id_Area ni Codigo_Area)
        $updateData = [
            'Nombre_Area' => $nombre,
            'Descripcion_Area' => $descripcion,
            'JSON_Area' => $jsonToStore,
            'NumeroCAR_Area' => $numeroCAR
        ];

        // Usar el método actualizar() de BaseController
        // actualizar() devuelve bool; si necesitas la fila actualizada la pedimos con obtenerPor()
        $ok = $this->base->actualizar($idArea, $updateData);

        if (!$ok) {
            throw new \RuntimeException('No se pudo actualizar el área (actualizar devolvió false).');
        }

        // Recuperar y retornar el registro actualizado (opcional)
        $rows = $this->obtenerPor('Id_Area', $idArea);
        return !empty($rows) ? $rows[0] : true;
    }




}

?>