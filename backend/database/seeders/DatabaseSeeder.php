<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use App\Models\User;
use App\Models\Genre;
use App\Models\Movie;
use App\Models\Series;
use App\Models\Season;
use App\Models\Episode;
use App\Models\Notification;

class DatabaseSeeder extends Seeder
{
    public function run(): void
    {
        // 1. Seed Users (Admin & Demo User)
        $admin = User::firstOrCreate(
            ['email' => 'admin@cinehub.com'],
            [
                'name' => 'Admin Master',
                'password' => Hash::make('admin123'),
                'role' => 'admin',
                'avatar_url' => 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80',
            ]
        );

        $user = User::firstOrCreate(
            ['email' => 'user@cinehub.com'],
            [
                'name' => 'Alex Vance',
                'password' => Hash::make('user123'),
                'role' => 'user',
                'avatar_url' => 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80',
            ]
        );

        // 2. Seed Genres
        $genres = ['Sci-Fi', 'Action', 'Drama', 'Adventure', 'Animation', 'Cyberpunk', 'Thriller', 'Fantasy', 'Documentary'];
        $genreModels = [];
        foreach ($genres as $name) {
            $genreModels[$name] = Genre::firstOrCreate(['name' => $name]);
        }

        // 3. Seed Open Movies
        $m1 = Movie::firstOrCreate(
            ['title' => 'Big Buck Bunny'],
            [
                'description' => 'A giant rabbit named Big Buck Bunny enjoys a peaceful day in the forest until three mischievous rodents disturb the peace.',
                'poster' => 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?auto=format&fit=crop&w=600&q=80',
                'backdrop' => 'https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=1200&q=80',
                'trailer_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
                'video_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
                'release_date' => '2008',
                'duration_minutes' => 10,
                'rating' => 4.8,
                'age_rating' => 'ALL',
                'language' => 'English',
                'country' => 'Netherlands',
                'is_published' => true,
                'is_trending' => true,
                'is_featured' => true,
                'views_count' => 0,
            ]
        );
        $m1->genres()->sync([
            $genreModels['Animation']->id,
            $genreModels['Adventure']->id
        ]);

        $m2 = Movie::firstOrCreate(
            ['title' => 'Sintel'],
            [
                'description' => 'A young warrior named Sintel begins a dangerous journey to find a dragon she has grown attached to.',
                'poster' => 'https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=600&q=80',
                'backdrop' => 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=1200&q=80',
                'trailer_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4',
                'video_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4',
                'release_date' => '2010',
                'duration_minutes' => 15,
                'rating' => 4.9,
                'age_rating' => 'PG',
                'language' => 'English',
                'country' => 'Netherlands',
                'is_published' => true,
                'is_trending' => true,
                'is_featured' => true,
                'views_count' => 0,
            ]
        );
        $m2->genres()->sync([
            $genreModels['Animation']->id,
            $genreModels['Fantasy']->id,
            $genreModels['Adventure']->id
        ]);

        $m3 = Movie::firstOrCreate(
            ['title' => 'Tears of Steel'],
            [
                'description' => 'A group of warriors and scientists reunite in Amsterdam to save the world from destructive robots from the future.',
                'poster' => 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80',
                'backdrop' => 'https://images.unsplash.com/photo-1519608487953-e999c86e7455?auto=format&fit=crop&w=1200&q=80',
                'trailer_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',
                'video_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',
                'release_date' => '2012',
                'duration_minutes' => 12,
                'rating' => 4.7,
                'age_rating' => 'PG-13',
                'language' => 'English',
                'country' => 'Netherlands',
                'is_published' => true,
                'is_trending' => true,
                'is_featured' => false,
                'views_count' => 0,
            ]
        );
        $m3->genres()->sync([
            $genreModels['Sci-Fi']->id,
            $genreModels['Action']->id
        ]);

        $m4 = Movie::firstOrCreate(
            ['title' => 'Elephants Dream'],
            [
                'description' => 'Two characters explore a strange surreal world filled with mysterious machines and unexpected discoveries.',
                'poster' => 'https://images.unsplash.com/photo-1549366021-9f761d450615?auto=format&fit=crop&w=600&q=80',
                'backdrop' => 'https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=1200&q=80',
                'trailer_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
                'video_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
                'release_date' => '2006',
                'duration_minutes' => 11,
                'rating' => 4.6,
                'age_rating' => 'PG',
                'language' => 'English',
                'country' => 'Netherlands',
                'is_published' => true,
                'is_trending' => false,
                'is_featured' => false,
                'views_count' => 0,
            ]
        );
        $m4->genres()->sync([
            $genreModels['Animation']->id,
            $genreModels['Sci-Fi']->id
        ]);

        // No fake series/seasons/episodes are seeded.
        // Series content can be added later from verified legal sources.

        // 5. Seed Broadcast Notification
        Notification::firstOrCreate(
            [
                'user_id' => $user->id,
                'title' => 'Welcome to CineHub',
            ],
            [
                'message' => 'Welcome to CineHub. Explore openly licensed movies and discover new content.',
                'type' => 'system',
                'is_read' => false,
                'action_content_id' => null,
                'action_content_type' => null,
            ]
        );
    }
}
