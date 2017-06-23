package com.example.android.popularmovies;

import java.util.List;

/**
 * Created by rudilee on 6/16/17.
 */

class MovieListResponse {
    public int page;
    public List<MovieDetail> results;
    public int total_results;
    public int total_pages;
}
