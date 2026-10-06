<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use App\Models\User;
use App\Models\Genre;

class DatabaseSeeder extends Seeder
{
    public function run(): void
    {
        // 1. Seed Core Film & TV Genres
        $genres = [
            'Action',
            'Adventure',
            'Animation',
            'Comedy',
            'Crime',
            'Documentary',
            'Drama',
            'Fantasy',
            'Horror',
            'Romance',
            'Sci-Fi',
            'Thriller',
        ];

        foreach ($genres as $name) {
            Genre::firstOrCreate(['name' => $name]);
        }

        // 2. Seed open/legal movies
        $this->call(MovieSeeder::class);

        // 3. Development-Only Initial Admin (Created only if no users exist in database)
        if (User::count() === 0) {
            User::create([
                'name' => 'Development Admin (DEV ONLY)',
                'email' => env('INITIAL_ADMIN_EMAIL', 'admin@cinehub.local'),
                'password' => Hash::make(env('INITIAL_ADMIN_PASSWORD', 'CineHubDev2024!Secure')),
                'role' => 'admin',
                'avatar_url' => 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80',
            ]);
        }
    }
}
