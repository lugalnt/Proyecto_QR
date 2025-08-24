<?php
// config/DB.php
namespace Config;

class DB {
    private static ?\PDO $pdo = null;

    private const HOST = 'localhost';
    private const NAME = 'fcs';
    private const USER = 'root';
    private const PASS = '';
    private const CHARSET = 'utf8mb4';

    public static function getConnection(): \PDO {
        if (self::$pdo !== null) return self::$pdo;

        $dsn = "mysql:host=" . self::HOST . ";dbname=" . self::NAME . ";charset=" . self::CHARSET;
        $options = [
            \PDO::ATTR_ERRMODE => \PDO::ERRMODE_EXCEPTION,
            \PDO::ATTR_DEFAULT_FETCH_MODE => \PDO::FETCH_ASSOC,
            \PDO::ATTR_EMULATE_PREPARES => false,
        ];

        try {
            self::$pdo = new \PDO($dsn, self::USER, self::PASS, $options);
            return self::$pdo;
        } catch (\PDOException $e) {
            throw new \RuntimeException('DB connection error: ' . $e->getMessage());
        }
    }
}
