package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieThumbnailAdapter.MovieThumbnailClickHandler {
    private final String MOVIE_LIST_STATE_KEY = "movie-thumbnail-list";
    private final String MOVIE_DETAILS_KEY = "movie-details";

    private final MovieThumbnailAdapter mMovieThumbnailAdapter = new MovieThumbnailAdapter(this);
    private FrameLayout mLoadingHolder;
    private final GridLayoutManager mMovieListLayoutManager = new GridLayoutManager(this, 2);
    private Parcelable mMovieListState = null;
    private List<MovieDetail> mMovieDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView movieThumbnailsRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_thumbnails);
        movieThumbnailsRecyclerView.setLayoutManager(mMovieListLayoutManager);
        movieThumbnailsRecyclerView.setAdapter(mMovieThumbnailAdapter);

        mLoadingHolder = (FrameLayout) findViewById(R.id.loading_holder);

        if (savedInstanceState == null) {
            loadMovieList(TheMovieDb.POPULARITY_DESC);
        }
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
            case R.id.sort_popularity_asc: sortBy = TheMovieDb.POPULARITY_ASC;
                break;
            case R.id.sort_popularity_desc: sortBy = TheMovieDb.POPULARITY_DESC;
                break;
            case R.id.sort_average_vote_asc: sortBy = TheMovieDb.AVERAGE_VOTE_ASC;
                break;
            case R.id.sort_average_vote_desc: sortBy = TheMovieDb.AVERAGE_VOTE_DESC;
                break;
        }

        if (!sortBy.isEmpty()) {
            loadMovieList(sortBy);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(MOVIE_LIST_STATE_KEY, mMovieListLayoutManager.onSaveInstanceState());
        outState.putParcelableArrayList(MOVIE_DETAILS_KEY, (ArrayList<? extends Parcelable>) mMovieDetails);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mMovieListState = savedInstanceState.getParcelable(MOVIE_LIST_STATE_KEY);
        mMovieDetails = savedInstanceState.getParcelableArrayList(MOVIE_DETAILS_KEY);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMovieListState != null) {
            mMovieThumbnailAdapter.setMovieDetails(mMovieDetails);
            mMovieListLayoutManager.onRestoreInstanceState(mMovieListState);
        }
    }

    @Override
    public void onClick(MovieDetail movieDetail) {
        Intent intentToDisplayMovieActivity = new Intent(this, MovieActivity.class);
        intentToDisplayMovieActivity.putExtra("movie-detail", movieDetail);

        startActivity(intentToDisplayMovieActivity);
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
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            toggleLoading(true);
        }

        @Override
        protected List<MovieDetail> doInBackground(String... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TheMovieDb.TMDB_BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();

            TheMovieDbService service = retrofit.create(TheMovieDbService.class);
            Call<DiscoverMovieResponse> caller = service.discoverMovie(params[0]);

            try {
                DiscoverMovieResponse movieResponse = caller.execute().body();
                if (movieResponse != null) {
                    mMovieDetails = movieResponse.results;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return mMovieDetails;
        }

        @Override
        protected void onPostExecute(List<MovieDetail> movieDetails) {
            super.onPostExecute(movieDetails);

            if (movieDetails != null) {
                mMovieThumbnailAdapter.setMovieDetails(movieDetails);
            }

            toggleLoading(false);
        }
    }
}
