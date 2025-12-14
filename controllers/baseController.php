<?php
// controllers/BaseController.php
require_once __DIR__ . '/../config/db.php';
use Config\DB;

class BaseController
{
    protected \PDO $pdo;
    protected $table;        // string
    protected $primaryKey;   // string o array
    protected array $fields;

    public function __construct(string $table, $primaryKey = 'id', array $fields = [])
    {
        $this->pdo = DB::getConnection();
        $this->table = $table;
        // aceptar string o array para primaryKey
        $this->primaryKey = is_array($primaryKey) ? array_values($primaryKey) : $primaryKey;
        $this->fields = $fields;
    }

    /**
     * @param mixed $payload
     * @return mixed
     */
    public function registrar($payload)
    {
        if (is_string($payload)) {
            if (empty($this->fields)) {
                throw new \InvalidArgumentException("Para CSV debe definir fields en el constructor.");
            }
            $parts = array_map('trim', explode(',', $payload));
            $data = [];
            foreach ($this->fields as $i => $col) {
                $data[$col] = $parts[$i] ?? null;
            }
        } elseif (is_array($payload)) {
            $data = $payload;
        } else {
            throw new \InvalidArgumentException('Payload inválido: string CSV o array requerido.');
        }

        $data = array_filter($data, function ($v) {
            return $v !== null; });

        $cols = array_keys($data);
        $placeholders = array_fill(0, count($cols), '?');

        $sql = "INSERT INTO `{$this->table}` (`" . implode('`,`', $cols) . "`) VALUES (" . implode(',', $placeholders) . ")";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute(array_values($data));

        // Si PK es simple y hay auto_increment, devolver lastInsertId
        if (is_string($this->primaryKey)) {
            $id = (int) $this->pdo->lastInsertId();
            // si lastInsertId=0, intentar devolver la columna si estaba en $data
            if ($id === 0 && isset($data[$this->primaryKey])) {
                return $data[$this->primaryKey];
            }
            return $id;
        } else {
            // PK compuesta: intentar devolver los valores de PK que vienen en $data (si existen).
            $pkValues = [];
            foreach ($this->primaryKey as $pkCol) {
                $pkValues[$pkCol] = $data[$pkCol] ?? null;
            }
            return $pkValues; // devuelve un array asociativo con las cols PK (o null si no estaban presentes)
        }
    }

    public function obtenerTodos(string $where = '', array $params = []): array
    {
        $sql = "SELECT * FROM `{$this->table}`";
        if ($where !== '')
            $sql .= " WHERE $where";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);
        return $stmt->fetchAll();
    }

    public function obtenerPor(string $campo, $valor): array
    {
        $sql = "SELECT * FROM `{$this->table}` WHERE `$campo` = ? LIMIT 1000";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([$valor]);
        return $stmt->fetchAll();
    }

    // helper: construir where y params desde PK o par clave=>valor
    protected function buildWhereFromPk($id): array
    {
        // devuelve [ $whereSql, $paramsArray ]
        if (is_string($this->primaryKey)) {
            return ["`{$this->primaryKey}` = ?", [$id]];
        } else {
            // primaryKey es array
            if (is_array($id)) {
                // permitir asociativo ['Id_Maquila'=>1,'Id_Area'=>2] o array indexado [1,2] (mismo orden que primaryKey)
                $params = [];
                $parts = [];
                $isAssoc = array_keys($id) !== range(0, count($id) - 1);
                if ($isAssoc) {
                    foreach ($this->primaryKey as $pkCol) {
                        if (!array_key_exists($pkCol, $id)) {
                            throw new \InvalidArgumentException("Falto valor de PK compuesta: {$pkCol}");
                        }
                        $parts[] = "`$pkCol` = ?";
                        $params[] = $id[$pkCol];
                    }
                } else {
                    // indexado: mapear por orden
                    foreach ($this->primaryKey as $i => $pkCol) {
                        if (!isset($id[$i])) {
                            throw new \InvalidArgumentException("Falto valor de PK compuesta en la posición {$i}");
                        }
                        $parts[] = "`$pkCol` = ?";
                        $params[] = $id[$i];
                    }
                }
                return [implode(' AND ', $parts), $params];
            } else {
                throw new \InvalidArgumentException("Para PK compuesta se requiere array con los valores.");
            }
        }
    }

    public function actualizar($id, array $data): bool
    {
        if (empty($data))
            return false;
        $sets = [];
        foreach ($data as $col => $val) {
            $sets[] = "`$col` = ?";
        }
        list($where, $paramsPk) = $this->buildWhereFromPk($id);
        $sql = "UPDATE `{$this->table}` SET " . implode(',', $sets) . " WHERE $where";
        $params = array_values($data);
        $params = array_merge($params, $paramsPk);
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    public function eliminar($id): bool
    {
        list($where, $params) = $this->buildWhereFromPk($id);
        $sql = "DELETE FROM `{$this->table}` WHERE $where";
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    public function ejecutarConsulta(string $sql, array $params = []): array
    {
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);
        return $stmt->fetchAll();
    }
}
