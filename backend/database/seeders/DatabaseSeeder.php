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

        // 3. Seed Movies (Legal test streams)
        $m1 = Movie::firstOrCreate(
            ['title' => 'Tears of Steel: Cyber Horizon'],
            [
                'description' => 'In a dystopian cyberpunk future in Amsterdam, a team of freedom fighters and tech sorcerers struggle to rescue humanity from rogue cybernetic sentinels.',
                'poster' => 'https://images.unsplash.com/photo-1578632767115-351597cf2477?auto=format&fit=crop&w=600&q=80',
                'backdrop' => 'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=1200&q=80',
                'trailer_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',
                'video_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',
                'release_date' => '2024',
                'duration_minutes' => 118,
                'rating' => 4.9,
                'age_rating' => '16+',
                'language' => 'English / Dutch',
                'country' => 'Netherlands / USA',
                'is_published' => true,
                'is_trending' => true,
                'is_featured' => true,
                'views_count' => 12450,
            ]
        );
        $m1->genres()->sync([$genreModels['Sci-Fi']->id, $genreModels['Action']->id, $genreModels['Cyberpunk']->id]);

        $m2 = Movie::firstOrCreate(
            ['title' => 'Nebula Odyssey'],
            [
                'description' => 'A deep-space surveyor discovers an enigmatic ancient derelict station pulsating with alien quantum signatures on the edge of the galaxy.',
                'poster' => 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80',
                'backdrop' => 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1200&q=80',
                'trailer_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4',
                'video_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4',
                'release_date' => '2024',
                'duration_minutes' => 134,
                'rating' => 4.7,
                'age_rating' => '13+',
                'language' => 'English',
                'country' => 'USA',
                'is_published' => true,
                'is_trending' => true,
                'is_featured' => false,
                'views_count' => 9820,
            ]
        );
        $m2->genres()->sync([$genreModels['Sci-Fi']->id, $genreModels['Adventure']->id]);

        // 4. Seed Series, Seasons, Episodes
        $s1 = Series::firstOrCreate(
            ['title' => 'Chronicles of the Wasteland'],
            [
                'description' => 'A gritty post-apocalyptic saga following survivors navigating electromagnetic storms and warring factions to discover an underground sanctuary.',
                'poster' => 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80',
                'backdrop' => 'https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=1200&q=80',
                'trailer_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',
                'release_date' => '2024',
                'rating' => 4.9,
                'language' => 'English',
                'country' => 'USA',
                'is_published' => true,
                'is_trending' => true,
                'is_featured' => true,
                'views_count' => 21000,
            ]
        );
        $s1->genres()->sync([$genreModels['Sci-Fi']->id, $genreModels['Drama']->id]);

        $season1 = Season::firstOrCreate(
            ['series_id' => $s1->id, 'season_number' => 1],
            ['title' => 'Season 1: Fallout', 'release_date' => '2024']
        );

        Episode::firstOrCreate(
            ['season_id' => $season1->id, 'episode_number' => 1],
            [
                'series_id' => $s1->id,
                'title' => 'Episode 1: The Ash Awakening',
                'description' => 'Nolan awakens in an abandoned fallout bunker only to find strange electromagnetic signals calling from the north.',
                'duration_minutes' => 48,
                'thumbnail' => 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=500&q=80',
                'video_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',
                'release_date' => '2024-01-10',
            ]
        );

        // 5. Seed Broadcast Notification
        Notification::firstOrCreate(
            ['title' => 'Welcome to CineHub Cinema'],
            [
                'user_id' => 0,
                'message' => 'Experience high-definition movies and series with seamless video playback, watchlists, and offline synchronization.',
                'type' => 'system',
                'is_read' => false,
            ]
        );
    }
}
