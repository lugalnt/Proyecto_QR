<?php
// controllers/maquilaController.php
// Controlador específico para entidad 'maquila' que usa BaseController.

require_once __DIR__ . '/baseController.php';
require_once __DIR__ . '/../vendor/autoload.php';

use Endroid\QrCode\Color\Color;
use Endroid\QrCode\Encoding\Encoding;
use Endroid\QrCode\ErrorCorrectionLevel;
use Endroid\QrCode\QrCode;
use Endroid\QrCode\RoundBlockSizeMode;
use Endroid\QrCode\Writer\PngWriter;

class AreaController {
    private BaseController $base;

    // Indica las columnas en el orden para CSV si quieres usar registrar con string
    private array $fields = ['Nombre_Area','Descripcion_Area','NumeroCAR_Area','Descripcion_Area','JSON_Area'];

    public function __construct() {
        $this->base = new BaseController('area', 'Id_Area', $this->fields);
    }

    public function registrar($payload){

        $data = is_string($payload) ? $payload : (array)$payload;
         
        $existe = $this->obtenerPor('Nombre_Area', $data['Nombre_Area']);
        if ($existe) {
            throw new \InvalidArgumentException('La maquila ya existe, elige otro nombre.');
        }

        do {
            $codigo = substr(bin2hex(random_bytes(8)), 0, 15);
            $existeCodigo = $this->obtenerPor("Codigo_Area", $codigo);
        } while ($existeCodigo);

        $data['Codigo_Area'] = $codigo;

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

        $fileName = __DIR__ . '/../qrcodes/' . $codigo . '.png';
        file_put_contents($fileName, $result->getString());

        return $this->base->registrar($data);

    }

    public function obtenerPor(string $campo, $valor) {
        return $this->base->obtenerPor($campo, $valor);
    }






}

?>