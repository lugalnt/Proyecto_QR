<?php
// controllers/UsuarioController.php
// Controlador específico para entidad 'usuarios' que usa BaseController.
// Aquí pones validaciones y lógica de negocio específicas de usuarios.

require_once __DIR__ . '/baseController.php';

class UsuarioController {
    private BaseController $base;

    // Indica las columnas en el orden para CSV si quieres usar registrar con string
    private array $fields = ['Nombre_Usuario', 'Password_Usuario', 'Telefono_Usuario', 'Puesto_Usuario'];

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
            if (empty($data['Password_Usuario'])) throw new \InvalidArgumentException('Contraseña requerido');
            if (empty($data['Telefono_Usuario']) || !filter_var($data['Telefono_Usuario'], FILTER_VALIDATE_INT)) {
                throw new \InvalidArgumentException('Numero inválido');
            }
        }

        $data['Password_Usuario'] = password_hash($data['Password_Usuario'], PASSWORD_DEFAULT);

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
            if (empty($data['Nombre_Usuario'])) throw new \InvalidArgumentException('Nombre requerido');
            if (empty($data['Password_Usuario'])) throw new \InvalidArgumentException('Contrasena requerido');
            if (empty($data['Telefono_Usuario']) || !filter_var($data['Telefono_Usuario'], FILTER_VALIDATE_INT)) {
                throw new \InvalidArgumentException('Numero inválido');
            }

        $data['Password_Usuario'] = password_hash($data['Password_Usuario'], PASSWORD_DEFAULT);
        return $this->base->actualizar($id, $data);
    }

    public function eliminar($id) {
        return $this->base->eliminar($id);
    }

    public function login($NombreUsuario, $Contraseña){
        
        if (empty($NombreUsuario)) throw new \InvalidArgumentException('Nombre requerido');
        if (empty($Contraseña)) throw new \InvalidArgumentException('Contrasena requerido');

        $usuarios = $this->obtenerPor("Nombre_Usuario", $NombreUsuario);


        if ($usuarios && count($usuarios) > 0) 
        {
          $usuario = $usuarios[0];
          if (password_verify($Contraseña, $usuario['Password_Usuario']))
          {
            return $usuario['Id_Usuario'];
          }
          else {throw new \InvalidArgumentException('Contrasena Incorrecta');}  
        } else {throw new \InvalidArgumentException('Nombre de usuario no registrado');}

    }

    public function loginConToken(string $NombreUsuario, string $Contraseña): array{
            // Reutilizar el login existente (lanza excepciones si falla)
            $IdUsuario = $this->login($NombreUsuario, $Contraseña);

            // Obtener datos públicos del usuario (usar obtenerPor ya existente)
            $usuarios = $this->obtenerPor('Id_Usuario', $IdUsuario);
            if (!$usuarios || count($usuarios) === 0) {
                throw new \RuntimeException('Usuario no encontrado después de autenticar.');
            }
            $usuario = $usuarios[0];
        
            // Construir payload público (NO incluir Password_Usuario)
            $data = [
                'Id_Usuario' => (int)$usuario['Id_Usuario'],
                'Nombre_Usuario' => $usuario['Nombre_Usuario'],
                'Telefono_Usuario' => $usuario['Telefono_Usuario'] ?? null,
                'Puesto_Usuario' => $usuario['Puesto_Usuario'] ?? null,
            ];
        
            // Generar token seguro
            try {
                $token = bin2hex(random_bytes(24)); // 48 bytes hex
            } catch (\Exception $e) {
                $token = bin2hex(openssl_random_pseudo_bytes(24));
            }
        
            // Intentar guardar sesión en tabla usuario_session (opcional)
            // Si no existe la tabla, se captura la excepción y no se detiene el login.
            try {
                // Asegurarse de que la clase DB esté disponible (config/db.php)
                if (!class_exists('\Config\DB')) {
                    // si no existe, intentar requerir config si hace falta (ajusta ruta si necesario)
                    // require_once __DIR__ . '/../config/db.php';
                }
                $pdo = \Config\DB::getConnection();
                $expires = date('Y-m-d H:i:s', strtotime('+7 days'));
                $sql = "INSERT INTO usuario_session (Id_Usuario, token, created_at, expires_at) VALUES (:id, :token, NOW(), :expires)";
                $stmt = $pdo->prepare($sql);
                $stmt->execute([
                    ':id' => $IdUsuario,
                    ':token' => $token,
                    ':expires' => $expires
                ]);
            } catch (\Throwable $t) {
                // No fallamos el login si la tabla no existe o hay error al insertar.
                // Opcional: loggear el error en fichero o sistema de logs.
            }
        
            // Añadir token al payload final
            $data['token'] = $token;
        
            return [
                'success' => true,
                'message' => 'Login correcto',
                'data' => $data
            ];
    }
}
