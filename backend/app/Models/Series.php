<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Series extends Model
{
    use HasFactory;

    protected $fillable = [
        'title', 'description', 'poster', 'backdrop',
        'trailer_url', 'release_date', 'rating',
        'language', 'country', 'is_published',
        'is_trending', 'is_featured', 'views_count'
    ];

    protected $casts = [
        'is_published' => 'boolean',
        'is_trending' => 'boolean',
        'is_featured' => 'boolean',
        'rating' => 'float',
        'views_count' => 'integer'
    ];

    public function seasons()
    {
        return $this->hasMany(Season::class);
    }

    public function episodes()
    {
        return $this->hasMany(Episode::class);
    }

    public function genres()
    {
        return $this->belongsToMany(Genre::class, 'series_genre');
    }

    public function cast()
    {
        return $this->belongsToMany(Cast::class, 'series_cast');
    }
}
