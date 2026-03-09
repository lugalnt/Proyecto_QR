<?php
// config/db.php
namespace Config;

require_once __DIR__ . '/env.php';

class DB
{
    private static ?\PDO $pdo = null;

    private const CHARSET = 'utf8mb4';

    public static function getConnection(): \PDO
    {
        if (self::$pdo !== null)
            return self::$pdo;

        $host = getenv('DB_HOST') ?: 'localhost';
        $name = getenv('DB_NAME') ?: 'fcs';
        $user = getenv('DB_USER') ?: 'root';
        $pass = getenv('DB_PASS') ?: '';

        $dsn = "mysql:host={$host};dbname={$name};charset=" . self::CHARSET;
        $options = [
            \PDO::ATTR_ERRMODE => \PDO::ERRMODE_EXCEPTION,
            \PDO::ATTR_DEFAULT_FETCH_MODE => \PDO::FETCH_ASSOC,
            \PDO::ATTR_EMULATE_PREPARES => false,
        ];

        try {
            self::$pdo = new \PDO($dsn, $user, $pass, $options);
            return self::$pdo;
        } catch (\PDOException $e) {
            throw new \RuntimeException('DB connection error: ' . $e->getMessage());
        }
    }
}
