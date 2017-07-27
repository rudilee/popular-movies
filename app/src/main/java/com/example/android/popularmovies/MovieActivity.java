package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.themoviedb.MovieDetail;
import com.example.android.popularmovies.themoviedb.MovieReview;
import com.example.android.popularmovies.themoviedb.MovieReviewsResponse;
import com.example.android.popularmovies.themoviedb.MovieVideo;
import com.example.android.popularmovies.themoviedb.MovieVideosResponse;
import com.example.android.popularmovies.themoviedb.TheMovieDb;
import com.example.android.popularmovies.themoviedb.TheMovieDbService;
import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MovieActivity extends AppCompatActivity {
    private final String MOVIE_VIDEOS_KEY = "movie-videos";
    private final String MOVIE_REVIEWS_KEY = "movie-reviews";

    private TheMovieDbService mService;
    private List<MovieVideo> mVideos;
    private List<MovieReview> mReviews;
    private MovieDetail mMovieDetail = null;

    @BindView(R.id.title_toolbar) Toolbar mTitleToolbar;
    @BindView(R.id.iv_backdrop) ImageView mBackdrop;
    @BindView(R.id.iv_poster) ImageView mPoster;
    @BindView(R.id.tv_release_date) TextView mReleaseDate;
    @BindView(R.id.rb_rating) RatingBar mRating;
    @BindView(R.id.tv_average_rate) TextView mAverageRate;
    @BindView(R.id.tv_overview) TextView mOverview;
    @BindView(R.id.movie_videos) LinearLayout mMovieVideos;
    @BindView(R.id.movie_reviews) LinearLayout mMovieReviews;
    @BindView(R.id.cb_favorite) CheckBox mFavoriteMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Intent intentFromMainActivity = getIntent();
        if (intentFromMainActivity != null) {
            if (intentFromMainActivity.hasExtra("movie-detail")) {
                mService = new Retrofit.Builder()
                        .baseUrl(TheMovieDb.TMDB_BASE_URL)
                        .addConverterFactory(MoshiConverterFactory.create())
                        .build()
                        .create(TheMovieDbService.class);

                mMovieDetail = intentFromMainActivity.getParcelableExtra("movie-detail");

                /*
                    Kind of a hack to workaround of event triggered from setChecked()
                    by bind ButterKnife after setChecked() called.
                 */
                ((CheckBox) findViewById(R.id.cb_favorite)).setChecked(checkIfMovieIsFavorited());

                ButterKnife.bind(this);

                String year = String.valueOf(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(mMovieDetail.releaseDate).getYear());
                String averageRate = new DecimalFormat("###.#").format(mMovieDetail.averageVote) + "/10";

                CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                collapsingToolbar.setTitle(mMovieDetail.title);

                Point screenSize = new Point();
                getWindowManager().getDefaultDisplay().getSize(screenSize);

                Picasso.with(this)
                        .load(TheMovieDb.TMDB_IMAGE_BASE_URL + TheMovieDb.TMDB_BACKDROP_SIZE + mMovieDetail.backdropPath)
                        .resize(screenSize.x, getResources().getDimensionPixelSize(R.dimen.backdrop_height))
                        .centerCrop()
                        .into(mBackdrop);

                Picasso.with(this)
                        .load(TheMovieDb.TMDB_IMAGE_BASE_URL + TheMovieDb.TMDB_POSTER_SIZE + mMovieDetail.posterPath)
                        .placeholder(R.mipmap.placeholder)
                        .into(mPoster);

                mReleaseDate.setText(year);

                mRating.setMax(10);
                mRating.setRating(mMovieDetail.averageVote / 2);

                mAverageRate.setText(averageRate);
                mOverview.setText(mMovieDetail.overview);

                if (savedInstanceState == null) {
                    new LoadMovieVideosTask().execute(mMovieDetail.id);
                    new LoadMovieReviewsTask().execute(mMovieDetail.id);
                }
            }
        }

        setSupportActionBar(mTitleToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            mTitleToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(MOVIE_VIDEOS_KEY, (ArrayList<? extends Parcelable>) mVideos);
        outState.putParcelableArrayList(MOVIE_REVIEWS_KEY, (ArrayList<? extends Parcelable>) mReviews);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mVideos = savedInstanceState.getParcelableArrayList(MOVIE_VIDEOS_KEY);
        mReviews = savedInstanceState.getParcelableArrayList(MOVIE_REVIEWS_KEY);

        populateVideos(mVideos);
        populateReviews(mReviews);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @OnCheckedChanged(R.id.cb_favorite)
    public void favoriteMovie(CompoundButton buttonView, boolean isChecked) {
        if (mMovieDetail == null) {
            return;
        }

        String message = isChecked ? "Movie added to favorite list" : "Movie removed from favorite list";

        if (isChecked) {
            ContentValues values = new ContentValues();
            values.put(FavoriteMovieContract.MovieDetail.COLUMN_POSTER_PATH, mMovieDetail.posterPath);
            values.put(FavoriteMovieContract.MovieDetail.COLUMN_OVERVIEW, mMovieDetail.overview);
            values.put(FavoriteMovieContract.MovieDetail.COLUMN_RELEASE_DATE, mMovieDetail.releaseDate);
            values.put(FavoriteMovieContract.MovieDetail.COLUMN_ID, mMovieDetail.id);
            values.put(FavoriteMovieContract.MovieDetail.COLUMN_TITLE, mMovieDetail.title);
            values.put(FavoriteMovieContract.MovieDetail.COLUMN_BACKDROP_PATH, mMovieDetail.backdropPath);
            values.put(FavoriteMovieContract.MovieDetail.COLUMN_VOTE_AVERAGE, mMovieDetail.averageVote);

            getContentResolver().insert(FavoriteMovieContentProvider.CONTENT_URI, values);
        } else {
            String[] whereArgs = {String.valueOf(mMovieDetail.id)};

            getContentResolver().delete(FavoriteMovieContentProvider.CONTENT_URI, FavoriteMovieContract.MovieDetail.COLUMN_ID + " = ?", whereArgs);
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean checkIfMovieIsFavorited() {
        boolean movieFavorited = false;
        String[] selectionArgs = {String.valueOf(mMovieDetail.id)};

        Cursor cursor = getContentResolver().query(FavoriteMovieContentProvider.CONTENT_URI, null, FavoriteMovieContract.MovieDetail.COLUMN_ID + " = ?", selectionArgs, null, null);
        if (cursor != null) {
            movieFavorited = cursor.getCount() > 0;

            cursor.close();
        }

        return movieFavorited;
    }

    private void toggleLoading(boolean loading, int progressId, int layoutId) {
        ProgressBar progress = (ProgressBar) findViewById(progressId);
        LinearLayout layout = (LinearLayout) findViewById(layoutId);

        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        layout.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private void toggleVideosLoading(boolean loading) {
        toggleLoading(loading, R.id.pb_videos_loading, R.id.movie_videos);
    }

    private void toggleReviewsLoading(boolean loading) {
        toggleLoading(loading, R.id.pb_reviews_loading, R.id.movie_reviews);
    }

    private void populateVideos(List<MovieVideo> videos) {
        if (videos == null) {
            return;
        }

        for (int i = 0; i < videos.size(); i++) {
            View videoView = getLayoutInflater().inflate(R.layout.movie_video, null);

            ImageButton videoIconButton = ButterKnife.findById(videoView, R.id.ib_video_icon);
            TextView nameTextView = ButterKnife.findById(videoView, R.id.tv_name);
            TextView typeTextView = ButterKnife.findById(videoView, R.id.tv_type);

            MovieVideo video = videos.get(i);
            nameTextView.setText(video.name);
            typeTextView.setText(video.type);

            videoIconButton.setTag(video);
            videoIconButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MovieVideo video = (MovieVideo) view.findViewById(R.id.ib_video_icon).getTag();

                    if (video.site.contentEquals("YouTube")) {
                        String videoUrl = "https://www.youtube.com/watch?v=" + video.key;
                        Intent playYoutubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        startActivity(playYoutubeIntent);
                    }
                }
            });

            mMovieVideos.addView(videoView);
        }
    }

    private void populateReviews(List<MovieReview> reviews) {
        if (reviews == null) {
            return;
        }

        for (int i = 0; i < reviews.size(); i++) {
            View reviewView = getLayoutInflater().inflate(R.layout.movie_review, null);

            TextView contentTextView = ButterKnife.findById(reviewView, R.id.tv_content);
            TextView authorTextView = ButterKnife.findById(reviewView, R.id.tv_author);

            MovieReview review = reviews.get(i);
            contentTextView.setText(review.content);
            authorTextView.setText(review.author);

            mMovieReviews.addView(reviewView);
        }
    }

    private class LoadMovieVideosTask extends AsyncTask<Integer, Void, List<MovieVideo> > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            toggleVideosLoading(true);
        }

        @Override
        protected List<MovieVideo> doInBackground(Integer... params) {
            Call<MovieVideosResponse> videosCaller;
            videosCaller = mService.listVideos(params[0]);

            try {
                if (videosCaller != null) {
                    MovieVideosResponse response = videosCaller.execute().body();
                    if (response != null) {
                        mVideos = response.results;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return mVideos;
        }

        @Override
        protected void onPostExecute(List<MovieVideo> movieVideos) {
            super.onPostExecute(movieVideos);

            populateVideos(movieVideos);
            toggleVideosLoading(false);
        }
    }

    private class LoadMovieReviewsTask extends AsyncTask<Integer, Void, List<MovieReview> > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            toggleReviewsLoading(true);
        }

        @Override
        protected List<MovieReview> doInBackground(Integer... params) {

            Call<MovieReviewsResponse> reviewsCaller;
            reviewsCaller = mService.listReviews(params[0]);

            if (reviewsCaller != null) {
                try {
                    MovieReviewsResponse response = reviewsCaller.execute().body();
                    if (response != null) {
                        mReviews = response.results;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return mReviews;
        }

        @Override
        protected void onPostExecute(List<MovieReview> movieReviews) {
            super.onPostExecute(movieReviews);

            populateReviews(movieReviews);
            toggleReviewsLoading(false);
        }
    }
}
