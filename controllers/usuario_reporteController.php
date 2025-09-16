<?php

require_once __DIR__ . '/baseController.php';

class Usuario_ReporteController{
    private BaseController $base;

    private array $fields = ['Id_Usuario','Id_Reporte','FechaRegistro_Reporte','FechaModificacion_Reporte'];

    public function __construct() {
        $this->base = new BaseController('usuario_reporte', ['Id_Usuario','Id_Reporte'], $this->fields);
    }

    public function registrar($payload){


        $data = is_string($payload) ? $payload : (array)$payload;
        $permitidos = ['Id_Usuario','Id_Reporte','FechaRegistro_Reporte','FechaModificacion_Reporte'];
        $data = array_intersect_key($data, array_flip($permitidos));
         
        return $this->base->registrar($data);

    }

}

?>