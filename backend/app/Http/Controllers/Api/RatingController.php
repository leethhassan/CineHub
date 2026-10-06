<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Rating;
use App\Models\Movie;
use App\Models\Series;
use Illuminate\Http\Request;

class RatingController extends Controller
{
    public function store(Request $request)
    {
        $validated = $request->validate([
            'content_id' => 'required|integer',
            'content_type' => 'required|in:movie,series',
            'rating' => 'required|integer|min:1|max:5',
            'review' => 'nullable|string|max:1000',
        ]);

        $rating = Rating::updateOrCreate(
            [
                'user_id' => $request->user()->id,
                'content_id' => $validated['content_id'],
                'content_type' => $validated['content_type'],
            ],
            [
                'rating' => $validated['rating'],
                'review' => $validated['review'] ?? '',
            ]
        );

        $this->recalculateAverageRating($validated['content_id'], $validated['content_type']);

        return response()->json([
            'success' => true,
            'message' => 'Rating submitted successfully',
            'data' => $rating
        ]);
    }

    public function update(Request $request, $id)
    {
        $validated = $request->validate([
            'rating' => 'required|integer|min:1|max:5',
            'review' => 'nullable|string|max:1000',
        ]);

        $rating = Rating::where('id', $id)
            ->where('user_id', $request->user()->id)
            ->firstOrFail();

        $rating->update([
            'rating' => $validated['rating'],
            'review' => $validated['review'] ?? $rating->review,
        ]);

        $this->recalculateAverageRating($rating->content_id, $rating->content_type);

        return response()->json([
            'success' => true,
            'message' => 'Rating updated successfully',
            'data' => $rating
        ]);
    }

    public function destroy(Request $request, $id)
    {
        $rating = Rating::where('user_id', $request->user()->id)
            ->where(function ($q) use ($id) {
                $q->where('id', $id)->orWhere('content_id', $id);
            })
            ->first();

        if ($rating) {
            $contentId = $rating->content_id;
            $contentType = $rating->content_type;
            $rating->delete();
            $this->recalculateAverageRating($contentId, $contentType);
        }

        return response()->json([
            'success' => true,
            'message' => 'Rating removed'
        ]);
    }

    private function recalculateAverageRating(int $contentId, string $contentType): void
    {
        $avg = Rating::where('content_id', $contentId)
            ->where('content_type', $contentType)
            ->avg('rating');

        $ratingValue = $avg ? round((float) $avg, 1) : 0.0;

        if ($contentType === 'movie') {
            Movie::where('id', $contentId)->update(['rating' => $ratingValue]);
        } elseif ($contentType === 'series') {
            Series::where('id', $contentId)->update(['rating' => $ratingValue]);
        }
    }
}
