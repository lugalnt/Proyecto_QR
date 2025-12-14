<?php
require_once __DIR__ . '/BaseController.php';

class MaquilaAreaController
{
    private BaseController $base;

    private array $fields = ['Id_Maquila', 'Id_Area'];

    public function __construct()
    {
        // PASAR PK compuesta como array
        $this->base = new BaseController('maquila_area', ['Id_Maquila', 'Id_Area'], $this->fields);
    }

    public function asignarMaquilaArea($idMaquila, $idArea)
    {
        $payload = [
            'Id_Maquila' => $idMaquila,
            'Id_Area' => $idArea
        ];
        return $this->base->registrar($payload);
    }

    // Devuelve solo las filas de maquila_area
    public function obtenerRelacionesPorMaquila($idMaquila)
    {
        return $this->base->obtenerPor('Id_Maquila', $idMaquila);
    }

    // Función recomendada: devolver las áreas (join con tabla area para mostrar nombre etc.)
    public function obtenerAreasPorMaquila($idMaquila)
    {
        $sql = "
            SELECT a.*
            FROM maquila_area ma
            JOIN area a ON ma.Id_Area = a.Id_Area
            WHERE ma.Id_Maquila = ?
            ORDER BY a.Nombre_Area 
        ";
        return $this->base->ejecutarConsulta($sql, [$idMaquila]);
    }

    public function eliminarRelacion($idMaquila, $idArea)
    {
        // Usamos array indexado o asociativo para el PK compuesta
        return $this->base->eliminar(['Id_Maquila' => $idMaquila, 'Id_Area' => $idArea]);
    }

    /**
     * Verifica si existe la relación Maquila <-> Area
     * Retorna true si existe, false si no.
     */
    public function verificarRelacion($idMaquila, $idArea): bool
    {
        // Opción A: Usar consulta SQL directa para contar (más eficiente)
        // Opción B: Usar obtenerPor si BaseController lo permite, pero como PK es compuesta, mejor SQL.

        $sql = "SELECT COUNT(*) as N FROM maquila_area WHERE Id_Maquila = ? AND Id_Area = ?";
        $res = $this->base->ejecutarConsulta($sql, [$idMaquila, $idArea]);

        if (!empty($res) && isset($res[0]['N'])) {
            return ((int) $res[0]['N']) > 0;
        }
        return false;
    }
}
