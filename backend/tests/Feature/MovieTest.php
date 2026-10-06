<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\Movie;
use App\Models\Genre;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Laravel\Sanctum\Sanctum;
use Tests\TestCase;

class MovieTest extends TestCase
{
    use RefreshDatabase;

    public function test_public_can_list_published_movies()
    {
        Movie::create([
            'title' => 'Published Movie',
            'description' => 'A great movie',
            'poster' => 'https://example.com/poster.jpg',
            'backdrop' => 'https://example.com/backdrop.jpg',
            'trailer_url' => 'https://example.com/trailer.mp4',
            'video_url' => 'https://example.com/video.mp4',
            'release_date' => '2024',
            'duration_minutes' => 120,
            'rating' => 0.0,
            'is_published' => true,
        ]);

        Movie::create([
            'title' => 'Unpublished Movie',
            'description' => 'Draft',
            'poster' => 'https://example.com/p2.jpg',
            'backdrop' => 'https://example.com/b2.jpg',
            'trailer_url' => 'https://example.com/t2.mp4',
            'video_url' => 'https://example.com/v2.mp4',
            'release_date' => '2024',
            'duration_minutes' => 90,
            'rating' => 0.0,
            'is_published' => false,
        ]);

        $response = $this->getJson('/api/movies');

        $response->assertStatus(200)
            ->assertJson([
                'success' => true,
            ]);

        $data = $response->json('data.data');
        $this->assertCount(1, $data);
        $this->assertEquals('Published Movie', $data[0]['title']);
    }

    public function test_admin_can_create_movie_with_zero_default_rating()
    {
        $admin = User::create([
            'name' => 'Admin User',
            'email' => 'admin@cinehub.test',
            'password' => 'secret123',
            'role' => 'admin',
        ]);
        Sanctum::actingAs($admin);

        $genre = Genre::create(['name' => 'Sci-Fi']);

        $response = $this->postJson('/api/admin/movies', [
            'title' => 'Inception Alpha',
            'description' => 'Mind bending dream worlds',
            'poster' => 'https://example.com/poster.jpg',
            'backdrop' => 'https://example.com/backdrop.jpg',
            'video_url' => 'https://example.com/movie.mp4',
            'trailer_url' => 'https://example.com/trailer.mp4',
            'release_date' => '2024',
            'duration_minutes' => 148,
            'genre_ids' => [$genre->id],
        ]);

        $response->assertStatus(201)
            ->assertJson([
                'success' => true,
                'data' => [
                    'title' => 'Inception Alpha',
                    'rating' => 0.0,
                    'is_trending' => false,
                    'is_featured' => false,
                    'views_count' => 0,
                ]
            ]);

        $this->assertDatabaseHas('movies', ['title' => 'Inception Alpha']);
        $this->assertDatabaseHas('movie_genre', ['genre_id' => $genre->id]);
    }

    public function test_admin_can_update_movie()
    {
        $admin = User::create([
            'name' => 'Admin User',
            'email' => 'admin@cinehub.test',
            'password' => 'secret123',
            'role' => 'admin',
        ]);
        Sanctum::actingAs($admin);

        $movie = Movie::create([
            'title' => 'Old Title',
            'description' => 'Old description',
            'poster' => 'https://example.com/poster.jpg',
            'backdrop' => 'https://example.com/backdrop.jpg',
            'trailer_url' => 'https://example.com/trailer.mp4',
            'video_url' => 'https://example.com/video.mp4',
            'release_date' => '2024',
            'duration_minutes' => 100,
            'rating' => 0.0,
            'is_published' => true,
        ]);

        $response = $this->putJson("/api/admin/movies/{$movie->id}", [
            'title' => 'Updated Title',
        ]);

        $response->assertStatus(200)
            ->assertJson([
                'success' => true,
                'data' => [
                    'title' => 'Updated Title',
                ]
            ]);

        $this->assertDatabaseHas('movies', ['title' => 'Updated Title']);
    }

    public function test_admin_can_delete_movie()
    {
        $admin = User::create([
            'name' => 'Admin User',
            'email' => 'admin@cinehub.test',
            'password' => 'secret123',
            'role' => 'admin',
        ]);
        Sanctum::actingAs($admin);

        $movie = Movie::create([
            'title' => 'To Be Deleted',
            'description' => 'Desc',
            'poster' => 'https://example.com/poster.jpg',
            'backdrop' => 'https://example.com/backdrop.jpg',
            'trailer_url' => 'https://example.com/trailer.mp4',
            'video_url' => 'https://example.com/video.mp4',
            'release_date' => '2024',
            'duration_minutes' => 90,
            'rating' => 0.0,
            'is_published' => true,
        ]);

        $response = $this->deleteJson("/api/admin/movies/{$movie->id}");

        $response->assertStatus(200)
            ->assertJson(['success' => true]);

        $this->assertDatabaseMissing('movies', ['id' => $movie->id]);
    }
}
