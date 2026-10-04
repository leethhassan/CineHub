<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        // 1. Users Table
        Schema::create('users', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('email')->unique();
            $table->string('password');
            $table->string('avatar_url')->nullable();
            $table->enum('role', ['user', 'admin'])->default('user');
            $table->rememberToken();
            $table->timestamps();
        });

        // 2. Genres Table
        Schema::create('genres', function (Blueprint $table) {
            $table->id();
            $table->string('name')->unique();
            $table->timestamps();
        });

        // 3. Movies Table
        Schema::create('movies', function (Blueprint $table) {
            $table->id();
            $table->string('title');
            $table->text('description');
            $table->string('poster');
            $table->string('backdrop');
            $table->string('trailer_url');
            $table->string('video_url');
            $table->string('release_date');
            $table->integer('duration_minutes');
            $table->decimal('rating', 3, 1)->default(0.0);
            $table->string('age_rating')->default('16+');
            $table->string('language')->default('English');
            $table->string('country')->default('USA');
            $table->boolean('is_published')->default(true);
            $table->boolean('is_trending')->default(false);
            $table->boolean('is_featured')->default(false);
            $table->unsignedBigInteger('views_count')->default(0);
            $table->timestamps();
        });

        // 4. Series Table
        Schema::create('series', function (Blueprint $table) {
            $table->id();
            $table->string('title');
            $table->text('description');
            $table->string('poster');
            $table->string('backdrop');
            $table->string('trailer_url');
            $table->string('release_date');
            $table->decimal('rating', 3, 1)->default(0.0);
            $table->string('language')->default('English');
            $table->string('country')->default('USA');
            $table->boolean('is_published')->default(true);
            $table->boolean('is_trending')->default(false);
            $table->boolean('is_featured')->default(false);
            $table->unsignedBigInteger('views_count')->default(0);
            $table->timestamps();
        });

        // 5. Seasons Table
        Schema::create('seasons', function (Blueprint $table) {
            $table->id();
            $table->foreignId('series_id')->constrained('series')->onDelete('cascade');
            $table->integer('season_number');
            $table->string('title');
            $table->string('release_date');
            $table->timestamps();
        });

        // 6. Episodes Table
        Schema::create('episodes', function (Blueprint $table) {
            $table->id();
            $table->foreignId('season_id')->constrained('seasons')->onDelete('cascade');
            $table->foreignId('series_id')->constrained('series')->onDelete('cascade');
            $table->integer('episode_number');
            $table->string('title');
            $table->text('description');
            $table->integer('duration_minutes');
            $table->string('thumbnail');
            $table->string('video_url');
            $table->string('release_date');
            $table->timestamps();
        });

        // 7. Pivot Tables for Genres
        Schema::create('movie_genre', function (Blueprint $table) {
            $table->foreignId('movie_id')->constrained('movies')->onDelete('cascade');
            $table->foreignId('genre_id')->constrained('genres')->onDelete('cascade');
            $table->primary(['movie_id', 'genre_id']);
        });

        Schema::create('series_genre', function (Blueprint $table) {
            $table->foreignId('series_id')->constrained('series')->onDelete('cascade');
            $table->foreignId('genre_id')->constrained('genres')->onDelete('cascade');
            $table->primary(['series_id', 'genre_id']);
        });

        // 8. Casts Table
        Schema::create('casts', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('character_name');
            $table->string('avatar_url')->nullable();
            $table->string('role')->default('Actor');
            $table->timestamps();
        });

        Schema::create('movie_cast', function (Blueprint $table) {
            $table->foreignId('movie_id')->constrained('movies')->onDelete('cascade');
            $table->foreignId('cast_id')->constrained('casts')->onDelete('cascade');
            $table->primary(['movie_id', 'cast_id']);
        });

        Schema::create('series_cast', function (Blueprint $table) {
            $table->foreignId('series_id')->constrained('series')->onDelete('cascade');
            $table->foreignId('cast_id')->constrained('casts')->onDelete('cascade');
            $table->primary(['series_id', 'cast_id']);
        });

        // 9. Ratings & Reviews
        Schema::create('ratings', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade');
            $table->unsignedBigInteger('content_id');
            $table->enum('content_type', ['movie', 'series']);
            $table->tinyInteger('rating'); // 1 to 5
            $table->text('review')->nullable();
            $table->timestamps();
            $table->unique(['user_id', 'content_id', 'content_type']);
        });

        // 10. Watchlists
        Schema::create('watchlists', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade');
            $table->unsignedBigInteger('content_id');
            $table->enum('content_type', ['movie', 'series']);
            $table->timestamps();
            $table->unique(['user_id', 'content_id', 'content_type']);
        });

        // 11. Watch Histories / Continue Watching
        Schema::create('watch_histories', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade');
            $table->unsignedBigInteger('content_id');
            $table->enum('content_type', ['movie', 'series']);
            $table->foreignId('episode_id')->nullable()->constrained('episodes')->onDelete('cascade');
            $table->unsignedBigInteger('progress_seconds')->default(0);
            $table->unsignedBigInteger('duration_seconds')->default(0);
            $table->timestamps();
            $table->unique(['user_id', 'content_id', 'content_type']);
        });

        // 12. Notifications
        Schema::create('notifications', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('user_id')->default(0); // 0 = broadcast
            $table->string('title');
            $table->text('message');
            $table->enum('type', ['movie', 'series', 'episode', 'recommendation', 'system'])->default('system');
            $table->boolean('is_read')->default(false);
            $table->unsignedBigInteger('action_content_id')->nullable();
            $table->string('action_content_type')->nullable();
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('notifications');
        Schema::dropIfExists('watch_histories');
        Schema::dropIfExists('watchlists');
        Schema::dropIfExists('ratings');
        Schema::dropIfExists('series_cast');
        Schema::dropIfExists('movie_cast');
        Schema::dropIfExists('casts');
        Schema::dropIfExists('series_genre');
        Schema::dropIfExists('movie_genre');
        Schema::dropIfExists('episodes');
        Schema::dropIfExists('seasons');
        Schema::dropIfExists('series');
        Schema::dropIfExists('movies');
        Schema::dropIfExists('genres');
        Schema::dropIfExists('users');
    }
};
