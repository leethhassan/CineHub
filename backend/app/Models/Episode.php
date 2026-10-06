<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Episode extends Model
{
    use HasFactory;

    protected $fillable = [
        'season_id', 'series_id', 'episode_number',
        'title', 'description', 'duration_minutes',
        'thumbnail', 'video_url', 'release_date'
    ];

    public function season()
    {
        return $this->belongsTo(Season::class);
    }

    public function series()
    {
        return $this->belongsTo(Series::class);
    }
}
