<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\Movie;
use App\Models\Rating;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Laravel\Sanctum\Sanctum;
use Tests\TestCase;

class RatingTest extends TestCase
{
    use RefreshDatabase;

    public function test_user_can_submit_rating_and_recalculate_average()
    {
        $movie = Movie::create([
            'title' => 'Interstellar Test',
            'description' => 'Space travel',
            'poster' => 'https://example.com/p.jpg',
            'backdrop' => 'https://example.com/b.jpg',
            'trailer_url' => 'https://example.com/t.mp4',
            'video_url' => 'https://example.com/v.mp4',
            'release_date' => '2024',
            'duration_minutes' => 160,
            'rating' => 0.0,
            'is_published' => true,
        ]);

        $user1 = User::create([
            'name' => 'User One',
            'email' => 'u1@example.com',
            'password' => 'secret123',
            'role' => 'user',
        ]);

        $user2 = User::create([
            'name' => 'User Two',
            'email' => 'u2@example.com',
            'password' => 'secret123',
            'role' => 'user',
        ]);

        // User 1 rates 5
        Sanctum::actingAs($user1);
        $res1 = $this->postJson('/api/ratings', [
            'content_id' => $movie->id,
            'content_type' => 'movie',
            'rating' => 5,
            'review' => 'Masterpiece!',
        ]);
        $res1->assertStatus(200);
        $this->assertEquals(5.0, (float) $movie->fresh()->rating);

        // User 2 rates 3
        Sanctum::actingAs($user2);
        $res2 = $this->postJson('/api/ratings', [
            'content_id' => $movie->id,
            'content_type' => 'movie',
            'rating' => 3,
            'review' => 'Average.',
        ]);
        $res2->assertStatus(200);

        // Average should now be (5+3)/2 = 4.0
        $this->assertEquals(4.0, (float) $movie->fresh()->rating);
    }
}
