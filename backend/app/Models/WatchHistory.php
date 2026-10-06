<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class WatchHistory extends Model
{
    use HasFactory;

    protected $table = 'watch_histories';

    protected $fillable = [
        'user_id',
        'content_id',
        'content_type',
        'episode_id',
        'progress_seconds',
        'duration_seconds',
    ];

    protected function casts(): array
    {
        return [
            'progress_seconds' => 'integer',
            'duration_seconds' => 'integer',
        ];
    }

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function episode()
    {
        return $this->belongsTo(Episode::class);
    }
}
