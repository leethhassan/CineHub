<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\Movie;
use App\Models\Watchlist;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Laravel\Sanctum\Sanctum;
use Tests\TestCase;

class WatchlistTest extends TestCase
{
    use RefreshDatabase;

    public function test_user_can_add_to_watchlist_and_prevent_duplicates()
    {
        $user = User::create([
            'name' => 'Watchlist User',
            'email' => 'watchlist@example.com',
            'password' => 'secret123',
            'role' => 'user',
        ]);
        Sanctum::actingAs($user);

        $movie = Movie::create([
            'title' => 'Watchlist Movie',
            'description' => 'Great film',
            'poster' => 'https://example.com/p.jpg',
            'backdrop' => 'https://example.com/b.jpg',
            'trailer_url' => 'https://example.com/t.mp4',
            'video_url' => 'https://example.com/v.mp4',
            'release_date' => '2024',
            'duration_minutes' => 100,
            'rating' => 0.0,
            'is_published' => true,
        ]);

        // First add
        $res1 = $this->postJson('/api/watchlist', [
            'content_id' => $movie->id,
            'content_type' => 'movie',
        ]);
        $res1->assertStatus(201);
        $this->assertEquals(1, Watchlist::where('user_id', $user->id)->count());

        // Second add (duplicate attempt)
        $res2 = $this->postJson('/api/watchlist', [
            'content_id' => $movie->id,
            'content_type' => 'movie',
        ]);
        $res2->assertStatus(201);
        // Count should still be 1 (no duplicate entries)
        $this->assertEquals(1, Watchlist::where('user_id', $user->id)->count());

        // Get watchlist
        $listRes = $this->getJson('/api/watchlist');
        $listRes->assertStatus(200);
        $this->assertCount(1, $listRes->json('data'));

        // Delete from watchlist
        $delRes = $this->deleteJson("/api/watchlist/{$movie->id}");
        $delRes->assertStatus(200);
        $this->assertEquals(0, Watchlist::where('user_id', $user->id)->count());
    }
}
