# Sistema de Inspección mediante Códigos QR

Este proyecto es un sistema web profesional diseñado para la gestión y seguimiento de inspecciones industriales utilizando códigos QR. Permite a los administradores y usuarios de maquilas supervisar áreas específicas mediante puntos de control configurables (C.A.R. - Cosas A Revisar).

## 🚀 Características Principales

- **Gestión de Usuarios**: Control de acceso para Administradores y Personal de Maquila.
- **Configuración de Áreas**: Creación de áreas dinámicas con descripción y parámetros de inspección personalizados.
- **C.A.R. Dinámicos**: Define "Cosas A Revisar" por área, admitiendo diversos tipos de datos:
  - Ok / No Ok (Booleano)
  - Rangos numéricos
  - Valores numéricos
  - Descripciones de texto
  - Fechas
- **Generación de QR**: Generación automática de códigos QR únicos para cada área registrada.
- **Dashboard de Reportes**: Visualización en tiempo real de los resultados de las inspecciones, con filtros avanzados por área, maquila, usuario y estado.
- **Diseño Responsivo**: Interfaz moderna y adaptable para su uso en computadoras y dispositivos móviles.
- **Exportación**: Funcionalidad para exportar reportes detallados.

## 🛠️ Stack Tecnológico

- **Backend**: PHP 7.4+
- **Base de Datos**: MySQL / MariaDB
- **Frontend**: HTML5, CSS3 (Vanilla), JavaScript (ES6+)
- **Dependencias**: Composer para la gestión de librerías (Generación de QR, etc.)

## 📦 Instalación

1. Clona el repositorio en tu servidor local (ej. `XAMPP/htdocs`).
2. Importa la base de datos (si se proporciona un archivo `.sql`).
3. Configura la conexión en `conn.php` dentro del directorio `config` o raíz.
4. Ejecuta `composer install` para instalar las dependencias necesarias.
5. Accede a `login.php` a través de tu navegador.

## 📱 Uso en Móviles

El sistema está optimizado para que los inspectores escaneen los códigos QR desde una aplicación móvil o navegador móvil, permitiendo registrar hallazgos directamente en el sitio de inspección.

## 📄 Licencia

Este proyecto es de uso privado/interno.
