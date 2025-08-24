<?php
// controllers/UsuarioController.php
// Controlador específico para entidad 'usuarios' que usa BaseController.
// Aquí pones validaciones y lógica de negocio específicas de usuarios.

require_once __DIR__ . '/baseController.php';

class UsuarioController {
    private BaseController $base;

    // Indica las columnas en el orden para CSV si quieres usar registrar con string
    private array $fields = ['Nombre_Usuario', 'Telefono', 'Contra_Usuario'];

    public function __construct() {
        // tabla 'usuarios', pk 'id_usuario', campos para CSV
        $this->base = new BaseController('usuario', 'Id_Usuario', $this->fields);
    }

    // Interfaz simplificada: registrar puede validar antes de llamar al base
    public function registrar($payload) {
        // ejemplo: validación mínima
        $data = is_string($payload) ? $payload : (array)$payload;

        $existe = $this->obtenerPor('Nombre_Usuario', $data['Nombre_Usuario']);
        if ($existe) {
            throw new \InvalidArgumentException('El usuario ya existe, elige otro nombre.');
        }
        // Si es array, comprobamos email y username
        if (is_array($data)) {
            if (empty($data['Nombre_Usuario'])) throw new \InvalidArgumentException('Nombre requerido');
            if (empty($data['Telefono']) || !filter_var($data['Telefono'], FILTER_VALIDATE_INT)) {
                throw new \InvalidArgumentException('Numero inválido');
            }
        }

        $data['Contra_Usuario'] = password_hash($data['Contra_Usuario'], PASSWORD_DEFAULT);

        // Aquí podrías hashear contraseñas, normalizar datos, etc.
        return $this->base->registrar($data);
    }

    // Métodos que delegan en BaseController
    public function obtenerTodos(string $where = '', array $params = []) {
        // añadir por defecto: no mostrar borrados (si usas deleted_at)
        if ($where === '') $where = 'deleted_at IS NULL';
        else $where .= ' AND deleted_at IS NULL';
        return $this->base->obtenerTodos($where, $params);
    }

    public function obtenerPor(string $campo, $valor) {
        return $this->base->obtenerPor($campo, $valor);
    }

    public function actualizar($id, array $data) {
        // ejemplo: prohibir actualizar username a vacío
        if (isset($data['username']) && empty($data['username'])) {
            throw new \InvalidArgumentException('username no puede estar vacío');
        }
        return $this->base->actualizar($id, $data);
    }

    public function eliminar($id) {
        return $this->base->eliminar($id);
    }

    public function login($NombreUsuario, $Contraseña){
        //if(password_verify())

    }
}
