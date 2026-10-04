<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

class StoreProgressRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'content_id' => 'required|integer',
            'content_type' => 'required|in:movie,series',
            'episode_id' => 'nullable|integer',
            'progress_seconds' => 'required|integer|min:0',
            'duration_seconds' => 'required|integer|min:0',
        ];
    }
}
