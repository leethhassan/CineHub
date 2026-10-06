<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

class StoreMovieRequest extends FormRequest
{
    public function authorize(): bool
    {
        return $this->user() && $this->user()->role === 'admin';
    }

    public function rules(): array
    {
        return [
            'title' => 'required|string|max:255',
            'description' => 'required|string',
            'poster' => 'required|url',
            'backdrop' => 'required|url',
            'trailer_url' => 'nullable|url',
            'video_url' => 'required|url',
            'release_date' => 'required|string',
            'duration_minutes' => 'required|integer|min:1',
            'rating' => 'nullable|numeric|min:0|max:5',
            'genre_ids' => 'nullable|array',
            'genre_ids.*' => 'integer|exists:genres,id',
        ];
    }
}
