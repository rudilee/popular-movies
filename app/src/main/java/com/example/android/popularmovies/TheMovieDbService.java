package com.example.android.popularmovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by rudilee on 6/16/17.
 */

interface TheMovieDbService {
    @GET("discover/movie?api_key=" + BuildConfig.TMDB_API_KEY)
    Call<MovieListResponse> discoverMovie(@Query("sort_by") String sortBy);

    @GET("movie/popular?api_key=" + BuildConfig.TMDB_API_KEY)
    Call<MovieListResponse> popularMovie();

    @GET("movie/top_rated?api_key=" + BuildConfig.TMDB_API_KEY)
    Call<MovieListResponse> topRatedMovie();
}
