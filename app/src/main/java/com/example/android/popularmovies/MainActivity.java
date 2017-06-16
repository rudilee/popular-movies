package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity {
    private List<MovieDetail> mMovieDetails = null;
    private MovieThumbnailAdapter mMovieThumbnailAdapter = new MovieThumbnailAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        RecyclerView movieThumbnailsRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_thumbnails);
        movieThumbnailsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        movieThumbnailsRecyclerView.setAdapter(mMovieThumbnailAdapter);

        loadMovieList();
    }

    private void loadMovieList() {
        new LoadMovieListTask().execute();
    }

    private class LoadMovieListTask extends AsyncTask<String, Void, List<MovieDetail> > {
        final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/";

        @Override
        protected List<MovieDetail> doInBackground(String... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TMDB_BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();

            TheMovieDbService service = retrofit.create(TheMovieDbService.class);
            Call<DiscoverMovieResponse> caller = service.discoverMovie(BuildConfig.TMDB_API_KEY);
            List<MovieDetail> movieDetails = null;

            try {
                DiscoverMovieResponse movieResponse = caller.execute().body();
                if (movieResponse != null) {
                    movieDetails = movieResponse.results;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return movieDetails;
        }

        @Override
        protected void onPostExecute(List<MovieDetail> movieDetails) {
            if (movieDetails != null) {
                mMovieDetails = movieDetails;
                mMovieThumbnailAdapter.setMovieDetails(movieDetails);
            }
        }
    }
}
