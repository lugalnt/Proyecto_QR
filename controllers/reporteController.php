<?php

require_once __DIR__ . '/baseController.php';

class ReporteController{
    private BaseController $base;

    private array $fields = ['FechaRegistro_Reporte, FechaModificacion_Reporte, CARTotal_Reporte, CARRevisadas_Reporte, Estado_Reporte, JSON_Reporte'];

    public function __construct() {
        $this->base = new BaseController('reporte', 'Id_Reporte', $this->fields);
    }

    public function registrar($payload){

        $data = is_string($payload) ? $payload : (array)$payload;
         
        return $this->base->registrar($data);

    }

}

?>