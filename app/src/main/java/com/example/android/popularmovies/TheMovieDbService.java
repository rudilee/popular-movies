package com.example.android.popularmovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by rudilee on 6/16/17.
 */

interface TheMovieDbService {
    @GET("movie/{sort}?api_key=" + BuildConfig.TMDB_API_KEY)
    Call<MovieListResponse> listMovie(@Path("sort") String order);
}
