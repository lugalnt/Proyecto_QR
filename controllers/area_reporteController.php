<?php

require_once __DIR__ . '/baseController.php';

class Area_ReporteController
{
    private BaseController $base;

    private array $fields = ['Id_Reporte', 'Id_Area', 'NumeroCAR_Area', 'FechaRegistro_Reporte', 'FechaModificacion_Reporte'];

    public function __construct()
    {
        $this->base = new BaseController('area_reporte', ['Id_Reporte', 'Id_Area'], $this->fields);
    }

    /**
     * @param mixed $payload
     * @return mixed
     */
    public function registrar($payload)
    {
        $data = is_string($payload) ? $payload : (array) $payload;
        $permitidos = ['Id_Reporte', 'Id_Area', 'NumeroCAR_Area', 'FechaRegistro_Reporte', 'FechaModificacion_Reporte'];
        $data = array_intersect_key($data, array_flip($permitidos));

        $res = $this->base->registrar($data);
        return $res;
    }

}

?>