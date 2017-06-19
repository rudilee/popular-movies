package com.example.android.popularmovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by rudilee on 6/16/17.
 */

public interface TheMovieDbService {
    @GET("discover/movie")
    Call<DiscoverMovieResponse> discoverMovie(@Query("api_key") String apiKey, @Query("sort_by") String sortBy);
}
