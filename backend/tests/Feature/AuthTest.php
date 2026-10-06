<?php

namespace Tests\Feature;

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AuthTest extends TestCase
{
    use RefreshDatabase;

    public function test_user_can_register()
    {
        $response = $this->postJson('/api/register', [
            'name' => 'John Doe',
            'email' => 'john@example.com',
            'password' => 'secret123',
            'password_confirmation' => 'secret123',
        ]);

        $response->assertStatus(201)
            ->assertJson([
                'success' => true,
                'data' => [
                    'user' => [
                        'name' => 'John Doe',
                        'email' => 'john@example.com',
                        'role' => 'user',
                    ]
                ]
            ]);

        $this->assertDatabaseHas('users', ['email' => 'john@example.com']);
    }

    public function test_user_can_login()
    {
        $user = User::create([
            'name' => 'Alice Smith',
            'email' => 'alice@example.com',
            'password' => 'secret123',
            'role' => 'user',
        ]);

        $response = $this->postJson('/api/login', [
            'email' => 'alice@example.com',
            'password' => 'secret123',
        ]);

        $response->assertStatus(200)
            ->assertJson([
                'success' => true,
                'data' => [
                    'user' => [
                        'email' => 'alice@example.com',
                    ]
                ]
            ]);

        $this->assertNotEmpty($response->json('data.token'));
    }

    public function test_user_cannot_login_with_invalid_password()
    {
        User::create([
            'name' => 'Bob Brown',
            'email' => 'bob@example.com',
            'password' => 'secret123',
            'role' => 'user',
        ]);

        $response = $this->postJson('/api/login', [
            'email' => 'bob@example.com',
            'password' => 'wrongpassword',
        ]);

        $response->assertStatus(401)
            ->assertJson(['success' => false]);
    }
}
