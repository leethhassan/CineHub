<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

class BroadcastNotificationRequest extends FormRequest
{
    public function authorize(): bool
    {
        return $this->user() && $this->user()->role === 'admin';
    }

    public function rules(): array
    {
        return [
            'title' => 'required|string|max:255',
            'message' => 'required|string',
            'type' => 'nullable|string|in:movie,series,episode,recommendation,system',
            'action_content_id' => 'nullable|integer',
            'action_content_type' => 'nullable|string|in:movie,series',
        ];
    }
}
