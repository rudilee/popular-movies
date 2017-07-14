package com.example.android.popularmovies.themoviedb;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by rudilee on 6/16/17.
 */

public class MovieListResponse {
    public int page;
    public List<MovieDetail> results;
    public @Json(name = "total_results") int totalResults;
    public @Json(name = "total_pages") int totalPages;
}
