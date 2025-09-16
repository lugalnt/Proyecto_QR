<?php

require_once __DIR__ . '/baseController.php';
require_once __DIR__ . '/usuario_reporteController.php';
require_once __DIR__ . '/area_reporteController.php';


class ReporteController{
    private BaseController $base;

    private array $fields = ['FechaRegistro_Reporte, FechaModificacion_Reporte, CARTotal_Reporte, CARRevisadas_Reporte, Estado_Reporte, JSON_Reporte'];

    public function __construct() {
        $this->base = new BaseController('reporte', 'Id_Reporte', $this->fields);
    }

    public function registrar($payload) {
        try {
            // Normalizar payload a array (si viene como objeto o string JSON ya decodificado)
            $original = $payload;
            if (is_string($payload)) {
                // si por alguna razón llega JSON string, intentar decodificar
                $decoded = json_decode($payload, true);
                if (json_last_error() === JSON_ERROR_NONE && is_array($decoded)) {
                    $original = $decoded;
                }
            }
            $origArr = is_array($original) ? $original : (array)$original;

            // Helper interno: busca varias claves posibles dentro de un array/map (root o data)
            $findKey = function(array $arr, array $candidates) {
                foreach ($candidates as $k) {
                    if (array_key_exists($k, $arr) && $arr[$k] !== null && $arr[$k] !== '') {
                        return $arr[$k];
                    }
                }
                return null;
            };

            // Primero intenta en la raíz
            $idArea = $findKey($origArr, ['Id_Area','IdArea','id_area','idArea','IdArea']);
            $idUsuario = $findKey($origArr, ['Id_Usuario','IdUsuario','id_usuario','idUsuario','IdUsuario','IdUser','Id_User','user_id','usuario_id']);

            // Si no están en la raíz, intentar en "data" si existe
            if (($idArea === null || $idUsuario === null) && isset($origArr['data']) && is_array($origArr['data'])) {
                $dataPart = (array)$origArr['data'];
                if ($idArea === null) $idArea = $findKey($dataPart, ['Id_Area','IdArea','id_area','idArea','id']);
                if ($idUsuario === null) $idUsuario = $findKey($dataPart, ['Id_Usuario','IdUsuario','id_usuario','idUsuario','user_id','Id_User']);
            }

            // Si aún no están, intentar encontrar dentro de JSON_Area (string) o json anidado
            if ($idArea === null && isset($origArr['JSON_Area'])) {
                $inner = @json_decode($origArr['JSON_Area'], true);
                if (is_array($inner)) {
                    $idArea = $findKey($inner, ['Id_Area','IdArea','id_area','id']);
                    // también puede existir como maquila_id pero no es lo mismo; lo omitimos
                }
            }

            // Normalizar a enteros cuando sea posible
            $idArea = ($idArea !== null && $idArea !== '') ? (int)$idArea : null;
            $idUsuario = ($idUsuario !== null && $idUsuario !== '') ? (int)$idUsuario : null;

            if ($idArea === null || $idUsuario === null) {
                return ['success' => false, 'error' => 'Faltan Id_Area o Id_Usuario en el payload'];
            }

            // corregir permitidos: debe ser array de strings
            $permitidos = [
                'FechaRegistro_Reporte',
                'FechaModificacion_Reporte',
                'CARTotal_Reporte',
                'CARRevisadas_Reporte',
                'Estado_Reporte',
                'JSON_Reporte'
            ];

            // quedarse solo con campos permitidos para insertar en reporte
            $data = array_intersect_key($origArr, array_flip($permitidos));

            // insertar en reporte
            $exitoR_Reporte = $this->base->registrar($data);

            // obtener Id_Reporte (soporta retorno array o id simple)
            if (is_array($exitoR_Reporte)) {
                $idReporte = isset($exitoR_Reporte['Id_Reporte']) ? (int)$exitoR_Reporte['Id_Reporte'] : null;
            } else {
                $idReporte = is_numeric($exitoR_Reporte) ? (int)$exitoR_Reporte : null;

                if ($idReporte === 0 || $idReporte === null) {
                    if (isset($data['Id_Reporte']) && is_numeric($data['Id_Reporte'])) {
                        $idReporte = (int)$data['Id_Reporte'];
                    } else {
                        return ['success' => false, 'error' => 'No se pudo obtener Id_Reporte tras la inserción'];
                    }
                }
            }

            // preparar payloads para los otros controllers (sin mutar $data original)
            $payloadArea = array_merge($data, [
                'Id_Reporte' => $idReporte,
                'Id_Area'    => $idArea
            ]);

            $payloadUsuario = array_merge($data, [
                'Id_Reporte' => $idReporte,
                'Id_Usuario' => $idUsuario
            ]);

            // instanciar y llamar a los controllers
            $usuario_ReporteController = new Usuario_ReporteController();
            $area_ReporteController    = new Area_ReporteController();

            $okArea    = $area_ReporteController->registrar($payloadArea);
            $okUsuario = $usuario_ReporteController->registrar($payloadUsuario);

            // considerar éxito si no devolvieron false o null
            $okAreaSuccess    = ($okArea !== false && $okArea !== null);
            $okUsuarioSuccess = ($okUsuario !== false && $okUsuario !== null);

            $success = ($okAreaSuccess && $okUsuarioSuccess);

            return [
                'success'     => (bool)$success,
                'Id_Reporte'  => $idReporte,
                'areaResult'  => $okArea,
                'userResult'  => $okUsuario
            ];

        } catch (\Throwable $e) {
            return [
                'success' => false,
                'error'   => $e->getMessage()
            ];
        }
    }


}

?>