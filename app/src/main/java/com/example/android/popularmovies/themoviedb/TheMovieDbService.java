package com.example.android.popularmovies.themoviedb;

import com.example.android.popularmovies.BuildConfig;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by rudilee on 6/16/17.
 */

public interface TheMovieDbService {
    @GET("movie/{sort}?api_key=" + BuildConfig.TMDB_API_KEY)
    Call<MovieListResponse> listMovie(@Path("sort") String order);

    @GET("movie/{movie_id}/videos?api_key=" + BuildConfig.TMDB_API_KEY)
    Call<MovieVideosResponse> listVideos(@Path("movie_id") int movieId);

    @GET("movie/{movie_id}/reviews?api_key=" + BuildConfig.TMDB_API_KEY)
    Call<MovieReviewsResponse> listReviews(@Path("movie_id") int movieId);
}
