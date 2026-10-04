<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Notification;
use Illuminate\Http\Request;

class NotificationController extends Controller
{
    public function index(Request $request)
    {
        $userId = $request->user()->id;
        $notifications = Notification::where('user_id', $userId)
            ->orWhere('user_id', 0)
            ->orderBy('created_at', 'desc')
            ->take(50)
            ->get();

        return response()->json([
            'success' => true,
            'data' => $notifications
        ]);
    }

    public function markAsRead(Request $request, $id)
    {
        Notification::where('id', $id)->update(['is_read' => true]);

        return response()->json([
            'success' => true,
            'message' => 'Marked as read'
        ]);
    }

    public function markAllAsRead(Request $request)
    {
        Notification::where('user_id', $request->user()->id)
            ->orWhere('user_id', 0)
            ->update(['is_read' => true]);

        return response()->json([
            'success' => true,
            'message' => 'All marked as read'
        ]);
    }
}
