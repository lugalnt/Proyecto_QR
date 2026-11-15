<?php

declare(strict_types=1);

namespace Tests\Unit;

use PHPUnit\Framework\TestCase;

final class SistemaFcsTest extends TestCase
{
    public function testCifradoPasswordBcrypt(): void
    {
        $clavePlana = 'AdminFCS2026*';
        $hash = password_hash($clavePlana, PASSWORD_BCRYPT);

        $this->assertTrue(password_verify($clavePlana, $hash));
        $this->assertFalse(password_verify('ClaveIncorrecta', $hash));
    }

    public function testGeneracionTokenUnicoQr(): void
    {
        $tokenA = bin2hex(random_bytes(16));
        $tokenB = bin2hex(random_bytes(16));

        $this->assertSame(32, strlen($tokenA));
        $this->assertNotSame($tokenA, $tokenB);
    }
}
