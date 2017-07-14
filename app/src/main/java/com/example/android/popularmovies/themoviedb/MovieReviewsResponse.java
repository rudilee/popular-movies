package com.example.android.popularmovies.themoviedb;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by rudilee on 7/14/17.
 */

public class MovieReviewsResponse {
    public int id;
    public int page;
    public List<MovieReview> results;
    public @Json(name = "total_pages") int totalPages;
    public @Json(name = "total_results") int totalResults;
}
