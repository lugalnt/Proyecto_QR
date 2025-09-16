<?php

require_once __DIR__ . '/baseController.php';
require_once __DIR__ . '/usuario_reporteController.php';
require_once __DIR__ . '/area_reporteController.php';


class ReporteController{
    private BaseController $base;

    private array $fields = [
        'FechaRegistro_Reporte',
        'FechaModificacion_Reporte',
        'CARTotal_Reporte',
        'CARRevisadas_Reporte',
        'Estado_Reporte',
        'JSON_Reporte'
    ];

    public function __construct() {
        $this->base = new BaseController('reporte', 'Id_Reporte', $this->fields);
    }

    public function registrar($payload) {
        try {
            // -------------------------------
            // Normalizar payload de forma segura
            // -------------------------------
            // Función recursiva para convertir stdClass->array y arrays; NO decodifica JSON_Reporte.
            $normalize = function($v) use (&$normalize) {
                if ($v instanceof \stdClass) {
                    $v = (array)$v;
                }
                if (is_array($v)) {
                    $res = [];
                    foreach ($v as $k => $item) {
                        $res[$k] = $normalize($item);
                    }
                    return $res;
                }
                // Si es string que parece JSON: intentamos decodificar **solo** en contextos que usemos después.
                // Aquí devolvemos tal cual; parseo específico se hace más abajo cuando convenga.
                return $v;
            };

            $orig = $payload;
            if (is_string($orig)) {
                // si viene JSON crudo en string, intentar decodificar a array
                $decoded = json_decode($orig, true);
                if (json_last_error() === JSON_ERROR_NONE && is_array($decoded)) {
                    $orig = $decoded;
                }
            }
            if (is_object($orig)) {
                $orig = (array)$orig;
            }
            $origArr = is_array($orig) ? $normalize($orig) : (array)$orig;

            // Si 'data' viene como string JSON, parsearlo a array
            if (isset($origArr['data']) && is_string($origArr['data'])) {
                $maybe = json_decode($origArr['data'], true);
                if (json_last_error() === JSON_ERROR_NONE && is_array($maybe)) {
                    $origArr['data'] = $maybe;
                }
            }

            // Si JSON_Area viene como string JSON, parsearlo (puede contener Id_Area)
            if (isset($origArr['JSON_Area']) && is_string($origArr['JSON_Area'])) {
                $tmp = json_decode($origArr['JSON_Area'], true);
                if (json_last_error() === JSON_ERROR_NONE && is_array($tmp)) {
                    $origArr['JSON_AREA_PARSED'] = $tmp; // lo guardamos en otra clave para no sobrescribir el string
                }
            }

            // -------------------------------
            // Helper para buscar claves
            // -------------------------------
            $findKey = function(array $arr, array $candidates) {
                foreach ($candidates as $k) {
                    if (array_key_exists($k, $arr) && $arr[$k] !== null && $arr[$k] !== '') {
                        return $arr[$k];
                    }
                }
                return null;
            };

            // -------------------------------
            // Extraer Id_Area y Id_Usuario
            // -------------------------------
            $idArea = $findKey($origArr, ['Id_Area','IdArea','id_area','idArea','id']);
            $idUsuario = $findKey($origArr, ['Id_Usuario','IdUsuario','id_usuario','idUsuario','IdUser','Id_User','user_id','usuario_id']);

            // Buscar en 'data' si falta alguno
            if ($idArea === null || $idUsuario === null) {
                // respuesta diagnóstica mínima para saber qué llegó realmente.
                // received_keys: lista de claves raíz recibidas
                // received_sample: solo algunas claves útiles (evita devolver todo JSON_Reporte completo)
                $receivedKeys = array_keys($origArr);
                $sample = [];
                foreach (['Id_Area','Id_Usuario','JSON_Reporte','data'] as $k) {
                    if (array_key_exists($k, $origArr)) {
                        $sample[$k] = $origArr[$k];
                    }
                }

                return [
                    'success' => false,
                    'error' => 'Faltan Id_Area o Id_Usuario en el payload',
                    'received_keys' => $receivedKeys,
                    'received_sample' => $sample
                ];
            }

            // Buscar en JSON_AREA_PARSED si existe
            if ($idArea === null && isset($origArr['JSON_AREA_PARSED']) && is_array($origArr['JSON_AREA_PARSED'])) {
                $idArea = $findKey($origArr['JSON_AREA_PARSED'], ['Id_Area','IdArea','id_area','id','maquila_id']);
            }

            // Normalizar a enteros (si viene vacio -> null)
            $idArea = ($idArea !== null && $idArea !== '') ? (int)$idArea : null;
            $idUsuario = ($idUsuario !== null && $idUsuario !== '') ? (int)$idUsuario : null;

            if ($idArea === null || $idUsuario === null) {
                return ['success' => false, 'error' => 'Faltan Id_Area o Id_Usuario en el payload'];
            }

            // -------------------------------
            // Preparar $data que se insertará en 'reporte'
            // -------------------------------
            $permitidos = [
                'FechaRegistro_Reporte',
                'FechaModificacion_Reporte',
                'CARTotal_Reporte',
                'CARRevisadas_Reporte',
                'Estado_Reporte',
                'JSON_Reporte'
            ];

            // Nos quedamos sólo con las claves permitidas tal cual están en la raíz de $origArr.
            // IMPORTANTE: si JSON_Reporte fue enviado como array (no debería), lo convertimos a string JSON
            $data = array_intersect_key($origArr, array_flip($permitidos));
            if (isset($data['JSON_Reporte']) && is_array($data['JSON_Reporte'])) {
                // re-encode to string to store in DB
                $data['JSON_Reporte'] = json_encode($data['JSON_Reporte'], JSON_UNESCAPED_UNICODE);
            }

            // -------------------------------
            // Insertar en la tabla reporte
            // -------------------------------
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

            // -------------------------------
            // Preparar payloads y llamar controllers relacionados
            // -------------------------------
            $payloadArea = array_merge($data, [
                'Id_Reporte' => $idReporte,
                'Id_Area'    => $idArea
            ]);

            $payloadUsuario = array_merge($data, [
                'Id_Reporte' => $idReporte,
                'Id_Usuario' => $idUsuario
            ]);

            $usuario_ReporteController = new Usuario_ReporteController();
            $area_ReporteController    = new Area_ReporteController();

            $okArea    = $area_ReporteController->registrar($payloadArea);
            $okUsuario = $usuario_ReporteController->registrar($payloadUsuario);

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

        // ---------- Métodos basados en BaseController::ejecutarConsulta ----------

    /**
     * Devuelve los últimos $limit reportes (ordenados por FechaRegistro_Reporte desc).
        * @param int $limit
        * @return array
        */
    public function getLatest(int $limit = 10): array {
        try {
            $limit = max(1, (int)$limit);

            $sql = "SELECT r.*,
                        resp.Id_Usuario AS Resp_Id_Usuario,
                        u.Nombre_Usuario AS Resp_Nombre
                    FROM reporte r
                    LEFT JOIN (
                        -- usuario_reporte con la fecha mínima por reporte (primer usuario vinculado)
                        SELECT ur1.Id_Reporte, ur1.Id_Usuario
                        FROM usuario_reporte ur1
                        JOIN (
                            SELECT Id_Reporte, MIN(FechaRegistro_Reporte) AS minf
                            FROM usuario_reporte
                            WHERE (deleted_at IS NULL OR deleted_at = '')
                            GROUP BY Id_Reporte
                        ) urmin
                        ON ur1.Id_Reporte = urmin.Id_Reporte AND ur1.FechaRegistro_Reporte = urmin.minf
                        WHERE (ur1.deleted_at IS NULL OR ur1.deleted_at = '')
                    ) resp ON resp.Id_Reporte = r.Id_Reporte
                    LEFT JOIN usuario u ON u.Id_Usuario = resp.Id_Usuario AND (u.deleted_at IS NULL OR u.deleted_at = '')
                    WHERE (r.deleted_at IS NULL OR r.deleted_at = '')
                    ORDER BY r.FechaRegistro_Reporte DESC
                    LIMIT {$limit}";

            $rows = $this->base->ejecutarConsulta($sql, []);
            return ['success' => true, 'data' => $rows];
        } catch (\Throwable $e) {
            return ['success' => false, 'error' => $e->getMessage()];
        }
    }

    /**
     * Devuelve reportes relacionados con las áreas de una maquila (maquila_area -> area_reporte -> reporte).
     * Añade Resp_Id_Usuario y Resp_Nombre.
     */
    public function getByMaquila(int $idMaquila, int $limit = 100): array {
        try {
            $idMaquila = (int)$idMaquila;
            $limit = max(1, (int)$limit);

            $sql = "SELECT DISTINCT r.*,
                        resp.Id_Usuario AS Resp_Id_Usuario,
                        u.Nombre_Usuario AS Resp_Nombre
                    FROM reporte r
                    INNER JOIN area_reporte ar ON ar.Id_Reporte = r.Id_Reporte AND (ar.deleted_at IS NULL OR ar.deleted_at = '')
                    INNER JOIN maquila_area ma ON ma.Id_Area = ar.Id_Area AND (ma.deleted_at IS NULL OR ma.deleted_at = '')
                    LEFT JOIN (
                        SELECT ur1.Id_Reporte, ur1.Id_Usuario
                        FROM usuario_reporte ur1
                        JOIN (
                            SELECT Id_Reporte, MIN(FechaRegistro_Reporte) AS minf
                            FROM usuario_reporte
                            WHERE (deleted_at IS NULL OR deleted_at = '')
                            GROUP BY Id_Reporte
                        ) urmin
                        ON ur1.Id_Reporte = urmin.Id_Reporte AND ur1.FechaRegistro_Reporte = urmin.minf
                        WHERE (ur1.deleted_at IS NULL OR ur1.deleted_at = '')
                    ) resp ON resp.Id_Reporte = r.Id_Reporte
                    LEFT JOIN usuario u ON u.Id_Usuario = resp.Id_Usuario AND (u.deleted_at IS NULL OR u.deleted_at = '')
                    WHERE ma.Id_Maquila = ?
                    AND (r.deleted_at IS NULL OR r.deleted_at = '')
                    ORDER BY r.FechaRegistro_Reporte DESC
                    LIMIT {$limit}";

            $rows = $this->base->ejecutarConsulta($sql, [$idMaquila]);
            return ['success' => true, 'data' => $rows];
        } catch (\Throwable $e) {
            return ['success' => false, 'error' => $e->getMessage()];
        }
    }

    /**
     * Devuelve reportes asociados a un usuario (vía usuario_reporte).
     * Incluye Resp_Id_Usuario y Resp_Nombre (responsable; puede ser el mismo u otro).
     */
    public function getByUsuario(int $idUsuario, int $limit = 100): array {
        try {
            $idUsuario = (int)$idUsuario;
            $limit = max(1, (int)$limit);

            $sql = "SELECT r.*,
                        resp.Id_Usuario AS Resp_Id_Usuario,
                        u.Nombre_Usuario AS Resp_Nombre
                    FROM reporte r
                    INNER JOIN usuario_reporte urfilter ON urfilter.Id_Reporte = r.Id_Reporte AND (urfilter.deleted_at IS NULL OR urfilter.deleted_at = '')
                    LEFT JOIN (
                        SELECT ur1.Id_Reporte, ur1.Id_Usuario
                        FROM usuario_reporte ur1
                        JOIN (
                            SELECT Id_Reporte, MIN(FechaRegistro_Reporte) AS minf
                            FROM usuario_reporte
                            WHERE (deleted_at IS NULL OR deleted_at = '')
                            GROUP BY Id_Reporte
                        ) urmin
                        ON ur1.Id_Reporte = urmin.Id_Reporte AND ur1.FechaRegistro_Reporte = urmin.minf
                        WHERE (ur1.deleted_at IS NULL OR ur1.deleted_at = '')
                    ) resp ON resp.Id_Reporte = r.Id_Reporte
                    LEFT JOIN usuario u ON u.Id_Usuario = resp.Id_Usuario AND (u.deleted_at IS NULL OR u.deleted_at = '')
                    WHERE urfilter.Id_Usuario = ?
                    AND (r.deleted_at IS NULL OR r.deleted_at = '')
                    ORDER BY r.FechaRegistro_Reporte DESC
                    LIMIT {$limit}";

            $rows = $this->base->ejecutarConsulta($sql, [$idUsuario]);
            return ['success' => true, 'data' => $rows];
        } catch (\Throwable $e) {
            return ['success' => false, 'error' => $e->getMessage()];
        }
    }

    /**
     * Devuelve reportes por estado exacto. Añade Resp_Id_Usuario y Resp_Nombre.
     */
    public function getByEstado(string $estado, int $limit = 100): array {
        try {
            $estado = (string)$estado;
            $limit = max(1, (int)$limit);

            $sql = "SELECT r.*,
                        resp.Id_Usuario AS Resp_Id_Usuario,
                        u.Nombre_Usuario AS Resp_Nombre
                    FROM reporte r
                    LEFT JOIN (
                        SELECT ur1.Id_Reporte, ur1.Id_Usuario
                        FROM usuario_reporte ur1
                        JOIN (
                            SELECT Id_Reporte, MIN(FechaRegistro_Reporte) AS minf
                            FROM usuario_reporte
                            WHERE (deleted_at IS NULL OR deleted_at = '')
                            GROUP BY Id_Reporte
                        ) urmin
                        ON ur1.Id_Reporte = urmin.Id_Reporte AND ur1.FechaRegistro_Reporte = urmin.minf
                        WHERE (ur1.deleted_at IS NULL OR ur1.deleted_at = '')
                    ) resp ON resp.Id_Reporte = r.Id_Reporte
                    LEFT JOIN usuario u ON u.Id_Usuario = resp.Id_Usuario AND (u.deleted_at IS NULL OR u.deleted_at = '')
                    WHERE r.Estado_Reporte = ?
                    AND (r.deleted_at IS NULL OR r.deleted_at = '')
                    ORDER BY r.FechaRegistro_Reporte DESC
                    LIMIT {$limit}";

            $rows = $this->base->ejecutarConsulta($sql, [$estado]);
            return ['success' => true, 'data' => $rows];
        } catch (\Throwable $e) {
            return ['success' => false, 'error' => $e->getMessage()];
        }
    }




}

?>