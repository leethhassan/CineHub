<?php

namespace Tests\Feature;

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Laravel\Sanctum\Sanctum;
use Tests\TestCase;

class AdminAuthTest extends TestCase
{
    use RefreshDatabase;

    public function test_regular_user_cannot_access_admin_routes()
    {
        $user = User::create([
            'name' => 'Regular User',
            'email' => 'regular@example.com',
            'password' => 'secret123',
            'role' => 'user',
        ]);

        Sanctum::actingAs($user);

        $response = $this->getJson('/api/admin/dashboard/stats');

        $response->assertStatus(403)
            ->assertJson([
                'success' => false,
                'message' => 'Forbidden. Admin privileges required.',
            ]);
    }

    public function test_admin_user_can_access_admin_routes()
    {
        $admin = User::create([
            'name' => 'Super Admin',
            'email' => 'admin@example.com',
            'password' => 'secret123',
            'role' => 'admin',
        ]);

        Sanctum::actingAs($admin);

        $response = $this->getJson('/api/admin/dashboard/stats');

        $response->assertStatus(200)
            ->assertJson([
                'success' => true,
                'data' => [
                    'users_count' => 1,
                    'movies_count' => 0,
                ]
            ]);
    }
}
