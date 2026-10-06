<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Cast extends Model
{
    use HasFactory;

    protected $fillable = [
        'name',
        'character_name',
        'avatar_url',
        'role',
    ];

    public function movies()
    {
        return $this->belongsToMany(Movie::class, 'movie_cast');
    }

    public function series()
    {
        return $this->belongsToMany(Series::class, 'series_cast');
    }
}
