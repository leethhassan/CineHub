<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Movie extends Model
{
    use HasFactory;

    protected $fillable = [
        'title', 'description', 'poster', 'backdrop',
        'trailer_url', 'video_url', 'release_date',
        'duration_minutes', 'rating', 'age_rating',
        'language', 'country', 'is_published',
        'is_trending', 'is_featured', 'views_count'
    ];

    protected $casts = [
        'is_published' => 'boolean',
        'is_trending' => 'boolean',
        'is_featured' => 'boolean',
        'rating' => 'float',
        'duration_minutes' => 'integer',
        'views_count' => 'integer'
    ];

    public function genres()
    {
        return $this->belongsToMany(Genre::class, 'movie_genre');
    }

    public function cast()
    {
        return $this->belongsToMany(Cast::class, 'movie_cast');
    }

    public function ratings()
    {
        return $this->hasMany(Rating::class, 'content_id')->where('content_type', 'movie');
    }
}
