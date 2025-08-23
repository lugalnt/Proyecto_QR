<?php
// controllers/BaseController.php
// Controlador base que encapsula operaciones CRUD simples y seguras.
// Requiere: config/db.php (PDO singleton)

require_once __DIR__ . '/../config/db.php';
use Config\DB;

class BaseController {
    protected \PDO $pdo;
    protected string $table;        // nombre de la tabla
    protected string $primaryKey;   // nombre de la PK (por ejemplo 'id' o 'id_usuario')
    protected array $fields;        // (opcional) lista de columnas para mapear CSV u orden

    public function __construct(string $table, string $primaryKey = 'id', array $fields = []) {
        $this->pdo = DB::getConnection();
        $this->table = $table;
        $this->primaryKey = $primaryKey;
        $this->fields = $fields;
    }

    /**
     * registrar: inserta una fila.
     * - $payload puede ser array asociativo ['col'=>'valor', ...] o CSV string si se definieron $fields.
     * - Devuelve lastInsertId() en success.
     */
    public function registrar($payload) {
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

        // Evitar columnas vacías innecesarias:
        $data = array_filter($data, function($v) { return $v !== null; });

        $cols = array_keys($data);
        $placeholders = array_fill(0, count($cols), '?');

        $sql = "INSERT INTO `{$this->table}` (`" . implode('`,`', $cols) . "`) VALUES (" . implode(',', $placeholders) . ")";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute(array_values($data));

        return (int)$this->pdo->lastInsertId();
    }

    /**
     * obtenerTodos: selecciona filas. $where y $params son opcionales.
     */
    public function obtenerTodos(string $where = '', array $params = []): array {
        $sql = "SELECT * FROM `{$this->table}`";
        if ($where !== '') $sql .= " WHERE $where";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);
        return $stmt->fetchAll();
    }

    /**
     * obtenerPor: buscar por campo exacto
     * devuelve array de filas (puede ser vacío)
     */
    public function obtenerPor(string $campo, $valor): array {
        $sql = "SELECT * FROM `{$this->table}` WHERE `$campo` = ? LIMIT 1000";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([$valor]);
        return $stmt->fetchAll();
    }

    /**
     * actualizar: actualiza por id (pk)
     * $data = ['col'=>'valor', ...]
     */
    public function actualizar($id, array $data): bool {
        if (empty($data)) return false;
        $sets = [];
        foreach ($data as $col => $val) {
            $sets[] = "`$col` = ?";
        }
        $sql = "UPDATE `{$this->table}` SET " . implode(',', $sets) . " WHERE `{$this->primaryKey}` = ?";
        $params = array_values($data);
        $params[] = $id;
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    /**
     * eliminar: intenta soft delete (campo deleted_at), si no existe hace hard delete.
     */
    public function eliminar($id): bool {
        // Intento soft delete
        try {
            $sql = "UPDATE `{$this->table}` SET `deleted_at` = NOW() WHERE `{$this->primaryKey}` = ?";
            $stmt = $this->pdo->prepare($sql);
            $ok = $stmt->execute([$id]);
            return $ok;
        } catch (\PDOException $e) {
            // fallback: hard delete
            $sql = "DELETE FROM `{$this->table}` WHERE `{$this->primaryKey}` = ?";
            $stmt = $this->pdo->prepare($sql);
            return $stmt->execute([$id]);
        }
    }

    /**
     * ejecutarConsulta: método utilitario para consultas personalizadas
     */
    public function ejecutarConsulta(string $sql, array $params = []): array {
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);
        return $stmt->fetchAll();
    }
}
