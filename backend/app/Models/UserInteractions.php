<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Rating extends Model
{
    use HasFactory;

    protected $fillable = ['user_id', 'content_id', 'content_type', 'rating', 'review'];
}

class Watchlist extends Model
{
    use HasFactory;

    protected $fillable = ['user_id', 'content_id', 'content_type'];
}

class WatchHistory extends Model
{
    use HasFactory;

    protected $fillable = [
        'user_id', 'content_id', 'content_type',
        'episode_id', 'progress_seconds', 'duration_seconds'
    ];
}

class Notification extends Model
{
    use HasFactory;

    protected $fillable = [
        'user_id', 'title', 'message', 'type',
        'is_read', 'action_content_id', 'action_content_type'
    ];
}
