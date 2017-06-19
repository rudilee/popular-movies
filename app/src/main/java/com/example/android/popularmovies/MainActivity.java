package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity {
    private final String POPULARITY_ASC = "popularity.asc";
    private final String POPULARITY_DESC = "popularity.desc";
    private final String AVERAGE_VOTE_ASC = "vote_average.asc";
    private final String AVERAGE_VOTE_DESC = "vote_average.desc";

    private List<MovieDetail> mMovieDetails = null;
    private MovieThumbnailAdapter mMovieThumbnailAdapter = new MovieThumbnailAdapter();
    private FrameLayout mLoadingHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        RecyclerView movieThumbnailsRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_thumbnails);
        movieThumbnailsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        movieThumbnailsRecyclerView.setAdapter(mMovieThumbnailAdapter);

        mLoadingHolder = (FrameLayout) findViewById(R.id.loading_holder);

        loadMovieList(POPULARITY_DESC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String sortBy = "";

        switch (item.getItemId()) {
            case R.id.sort_popularity_asc: sortBy = POPULARITY_ASC;
                break;
            case R.id.sort_popularity_desc: sortBy = POPULARITY_DESC;
                break;
            case R.id.sort_average_vote_asc: sortBy = AVERAGE_VOTE_ASC;
                break;
            case R.id.sort_average_vote_desc: sortBy = AVERAGE_VOTE_DESC;
                break;
        }

        if (!sortBy.isEmpty()) {
            loadMovieList(sortBy);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMovieList(String sortBy) {
        new LoadMovieListTask().execute(sortBy);
    }

    private void toggleLoading(boolean loading) {
        float fromAlpha = loading ? 0f : 1f;
        float toAlpha = loading ? 1f : 0f;

        AlphaAnimation animation = new AlphaAnimation(fromAlpha, toAlpha);
        animation.setDuration(200);

        mLoadingHolder.setAnimation(animation);
        mLoadingHolder.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private class LoadMovieListTask extends AsyncTask<String, Void, List<MovieDetail> > {
        final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            toggleLoading(true);
        }

        @Override
        protected List<MovieDetail> doInBackground(String... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TMDB_BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();

            TheMovieDbService service = retrofit.create(TheMovieDbService.class);
            Call<DiscoverMovieResponse> caller = service.discoverMovie(BuildConfig.TMDB_API_KEY, params[0]);
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
            super.onPostExecute(movieDetails);

            if (movieDetails != null) {
                mMovieDetails = movieDetails;
                mMovieThumbnailAdapter.setMovieDetails(movieDetails);
            }

            toggleLoading(false);
        }
    }
}
