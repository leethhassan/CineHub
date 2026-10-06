<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Movie;
use App\Models\Genre;

class MovieSeeder extends Seeder
{
    public function run(): void
    {
        $movies = [
            [
                'title' => 'Big Buck Bunny',
                'description' => 'An open animated short film created by the Blender Foundation.',
                'poster' => 'https://commons.wikimedia.org/wiki/Special:Redirect/file/Big_buck_bunny_poster_big.jpg',
                'backdrop' => 'https://commons.wikimedia.org/wiki/Special:Redirect/file/BBB-Bunny.png',
                'trailer_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
                'video_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
                'release_date' => '2008-04-10',
                'duration_minutes' => 10,
                'rating' => 0.0,
                'age_rating' => 'PG',
                'language' => 'English',
                'country' => 'Netherlands',
                'is_published' => true,
                'is_trending' => false,
                'is_featured' => false,
                'views_count' => 0,
                'genres' => ['Animation', 'Comedy'],
            ],
            [
                'title' => 'Sintel',
                'description' => 'An open animated short film created by the Blender Foundation.',
                'poster' => 'https://commons.wikimedia.org/wiki/Special:Redirect/file/Sintel_poster.jpg',
                'backdrop' => 'https://commons.wikimedia.org/wiki/Special:Redirect/file/Sintel_1920x1080.png',
                'trailer_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4',
                'video_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4',
                'release_date' => '2010-09-30',
                'duration_minutes' => 15,
                'rating' => 0.0,
                'age_rating' => 'PG',
                'language' => 'English',
                'country' => 'Netherlands',
                'is_published' => true,
                'is_trending' => false,
                'is_featured' => false,
                'views_count' => 0,
                'genres' => ['Animation', 'Adventure', 'Fantasy'],
            ],
            [
                'title' => 'Tears of Steel',
                'description' => 'An open science-fiction short film produced by the Blender Foundation.',
                'poster' => 'https://commons.wikimedia.org/wiki/Special:Redirect/file/Tos-poster.png',
                'backdrop' => 'https://commons.wikimedia.org/wiki/Special:Redirect/file/Tears_of_Steel_frame_03_2e.jpg',
                'trailer_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',
                'video_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',
                'release_date' => '2012-09-26',
                'duration_minutes' => 12,
                'rating' => 0.0,
                'age_rating' => 'PG-13',
                'language' => 'English',
                'country' => 'Netherlands',
                'is_published' => true,
                'is_trending' => false,
                'is_featured' => false,
                'views_count' => 0,
                'genres' => ['Sci-Fi', 'Action', 'Drama'],
            ],
            [
                'title' => 'Elephants Dream',
                'description' => 'An open animated short film and one of the Blender Foundation open movie projects.',
                'poster' => 'https://commons.wikimedia.org/wiki/Special:Redirect/file/ElephantsDreamPoster.jpg',
                'backdrop' => 'https://commons.wikimedia.org/wiki/Special:Redirect/file/Elephants_Dream_-_Emo_and_Proog.png',
                'trailer_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
                'video_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
                'release_date' => '2006-03-24',
                'duration_minutes' => 11,
                'rating' => 0.0,
                'age_rating' => 'PG',
                'language' => 'English',
                'country' => 'Netherlands',
                'is_published' => true,
                'is_trending' => false,
                'is_featured' => false,
                'views_count' => 0,
                'genres' => ['Animation', 'Fantasy'],
            ],
        ];

        foreach ($movies as $data) {
            $genreNames = $data['genres'];
            unset($data['genres']);

            $movie = Movie::updateOrCreate(
                ['title' => $data['title']],
                $data
            );

            $genreIds = Genre::whereIn('name', $genreNames)->pluck('id');
            $movie->genres()->sync($genreIds);
        }
    }
}
