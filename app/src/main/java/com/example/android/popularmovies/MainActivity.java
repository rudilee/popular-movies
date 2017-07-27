package com.example.android.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.android.popularmovies.themoviedb.MovieDetail;
import com.example.android.popularmovies.themoviedb.MovieListResponse;
import com.example.android.popularmovies.themoviedb.TheMovieDb;
import com.example.android.popularmovies.themoviedb.TheMovieDbService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieThumbnailAdapter.MovieThumbnailClickHandler {
    private final String MOVIE_LIST_STATE_KEY = "movie-thumbnail-list";
    private final String MOVIE_DETAILS_KEY = "movie-details";
    private final String POPULAR_MOVIE = "popular";
    private final String TOP_RATED_MOVIE = "top_rated";
    private final String FAVORITE_MOVIE = "favorite_movie";

    private final MovieThumbnailAdapter mMovieThumbnailAdapter = new MovieThumbnailAdapter(this);
    private GridLayoutManager mMovieListLayoutManager;
    private Parcelable mMovieListState = null;
    private List<MovieDetail> mMovieDetails;

    @BindView(R.id.main_toolbar) Toolbar mMainToolbar;
    @BindView(R.id.rv_movie_thumbnails) RecyclerView mMovieThumbnails;
    @BindView(R.id.loading_holder) FrameLayout mLoadingHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mMainToolbar);

        int column = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3;
        mMovieListLayoutManager = new GridLayoutManager(this, column);

        mMovieThumbnails.setLayoutManager(mMovieListLayoutManager);
        mMovieThumbnails.setAdapter(mMovieThumbnailAdapter);

        if (savedInstanceState == null) {
            loadMovieList(POPULAR_MOVIE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String option = "";

        switch (item.getItemId()) {
            case R.id.popular_movie: option = POPULAR_MOVIE;
                break;
            case R.id.top_rated_movie: option = TOP_RATED_MOVIE;
                break;
            case R.id.favorite_movie: option = FAVORITE_MOVIE;
        }

        if (!option.isEmpty()) {
            loadMovieList(option);
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

    private void loadMovieList(String option) {
        new LoadMovieListTask().execute(option);
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
        private boolean mRetrieveSuccess = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mMovieDetails != null) {
                mMovieDetails.clear();
            }

            toggleLoading(true);
        }

        private List<MovieDetail> loadFavoriteMovies() {
            FavoriteMovieDatabaseHelper dbHelper = new FavoriteMovieDatabaseHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(FavoriteMovieContract.MovieDetail.TABLE_NAME, null, null, null, null, null, FavoriteMovieContract.MovieDetail.COLUMN_ID);

            while (cursor.moveToNext()) {
                MovieDetail movieDetail = new MovieDetail();
                movieDetail.posterPath = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MovieDetail.COLUMN_POSTER_PATH));
                movieDetail.overview = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MovieDetail.COLUMN_OVERVIEW));
                movieDetail.releaseDate = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MovieDetail.COLUMN_RELEASE_DATE));
                movieDetail.id = cursor.getInt(cursor.getColumnIndex(FavoriteMovieContract.MovieDetail.COLUMN_ID));
                movieDetail.title = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MovieDetail.COLUMN_TITLE));
                movieDetail.backdropPath = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MovieDetail.COLUMN_BACKDROP_PATH));
                movieDetail.averageVote = cursor.getFloat(cursor.getColumnIndex(FavoriteMovieContract.MovieDetail.COLUMN_VOTE_AVERAGE));

                mMovieDetails.add(movieDetail);
            }

            cursor.close();

            mRetrieveSuccess = true;

            return mMovieDetails;
        }

        private List<MovieDetail> loadMoviesByOrder(String order) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TheMovieDb.TMDB_BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();

            TheMovieDbService service = retrofit.create(TheMovieDbService.class);
            Call<MovieListResponse> caller;

            caller = service.listMovie(order);

            try {
                if (caller != null) {
                    MovieListResponse movieListResponse = caller.execute().body();
                    if (movieListResponse != null) {
                        mMovieDetails = movieListResponse.results;
                    }
                }

                mRetrieveSuccess = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return mMovieDetails;
        }

        @Override
        protected List<MovieDetail> doInBackground(String... params) {
            if (params[0].equals(FAVORITE_MOVIE)) {
                return loadFavoriteMovies();
            }

            return loadMoviesByOrder(params[0]);
        }

        @Override
        protected void onPostExecute(List<MovieDetail> movieDetails) {
            super.onPostExecute(movieDetails);

            if (movieDetails != null) {
                mMovieThumbnailAdapter.setMovieDetails(movieDetails);
            } else if (!mRetrieveSuccess) {
                Toast.makeText(MainActivity.this, "Encounter problem with your Internet connection", Toast.LENGTH_LONG).show();
            }

            toggleLoading(false);
        }
    }
}
