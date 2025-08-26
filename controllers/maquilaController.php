<?php
// controllers/maquilaController.php
// Controlador específico para entidad 'maquila' que usa BaseController.


require_once __DIR__ . '/baseController.php';

class MaquilaController {
    private BaseController $base;

    // Indica las columnas en el orden para CSV si quieres usar registrar con string
    private array $fields = ['Nombre_Maquila', 'Contraseña_Maquila'];

    public function __construct() {
        $this->base = new BaseController('maquila', 'Id_Maquila', $this->fields);
    }

    // Interfaz simplificada: registrar puede validar antes de llamar al base
    public function registrar($payload) {
        // ejemplo: validación mínima
        $data = is_string($payload) ? $payload : (array)$payload;

        $existe = $this->obtenerPor('Nombre_Maquila', $data['Nombre_Maquila']);
        if ($existe) {
            throw new \InvalidArgumentException('La maquila ya existe, elige otro nombre.');
        }
        // Si es array, comprobamos email y username
        if (is_array($data)) {
            if (empty($data['Nombre_Maquila'])) throw new \InvalidArgumentException('Nombre requerido');
            if (empty($data['Contraseña_Maquila'])) throw new \InvalidArgumentException('Contraseña requerido');
        }

        $data['Contraseña_Maquila'] = password_hash($data['Contraseña_Maquila'], PASSWORD_DEFAULT);

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
            if (empty($data['Nombre_Maquila'])) throw new \InvalidArgumentException('Nombre requerido');
            if (empty($data['Contraseña_Maquila'])) throw new \InvalidArgumentException('Contraseña requerido');

        $data['Contraseña_Maquila'] = password_hash($data['Contraseña_Maquila'], PASSWORD_DEFAULT);
        return $this->base->actualizar($id, $data);
    }

    public function eliminar($id) {
        return $this->base->eliminar($id);
    }

    public function login($NombreMaquila, $Contraseña){
        
        if (empty($NombreMaquila)) throw new \InvalidArgumentException('Nombre requerido');
        if (empty($Contraseña)) throw new \InvalidArgumentException('Contraseña requerido');

        $maquilas = $this->obtenerPor("Nombre_Maquila", $NombreMaquila);
        echo '<script>console.log('.json_encode($maquilas).')</script>'; //<---QUITAR!

        if ($maquilas && count($maquilas) > 0) 
        {
          $maquila = $maquilas[0];
          if (password_verify($Contraseña, $maquila['Contraseña_Maquila']))
          {
            return $maquila['Id_Maquila'];
          }
          else {throw new \InvalidArgumentException('Contraseña Incorrecta');}  
        } else {throw new \InvalidArgumentException('Nombre de maquila no registrado');}

    }
}
