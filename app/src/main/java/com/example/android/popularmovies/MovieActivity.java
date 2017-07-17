package com.example.android.popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.themoviedb.MovieDetail;
import com.example.android.popularmovies.themoviedb.MovieVideo;
import com.example.android.popularmovies.themoviedb.MovieVideosResponse;
import com.example.android.popularmovies.themoviedb.TheMovieDb;
import com.example.android.popularmovies.themoviedb.TheMovieDbService;
import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MovieActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Toolbar titleToolbar = (Toolbar) findViewById(R.id.title_toolbar);
        setSupportActionBar(titleToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intentFromMainActivity = getIntent();
        if (intentFromMainActivity != null) {
            if (intentFromMainActivity.hasExtra("movie-detail")) {
                ImageView backdropImageView = (ImageView) findViewById(R.id.iv_backdrop);
                ImageView posterImageView = (ImageView) findViewById(R.id.iv_poster);
                TextView releaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
                RatingBar averageRateRatingBar = (RatingBar) findViewById(R.id.rb_average_rate);
                TextView averageRateTextView = (TextView) findViewById(R.id.tv_average_rate);
                TextView overviewTextView = (TextView) findViewById(R.id.tv_overview);

                MovieDetail movieDetail = intentFromMainActivity.getParcelableExtra("movie-detail");

                String year = String.valueOf(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(movieDetail.release_date).getYear());
                String averageRate = new DecimalFormat("###.#").format(movieDetail.averageVote) + "/10";

                CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                collapsingToolbar.setTitle(movieDetail.title);

                Point screenSize = new Point();
                getWindowManager().getDefaultDisplay().getSize(screenSize);

                Picasso.with(this)
                        .load(TheMovieDb.TMDB_IMAGE_BASE_URL + TheMovieDb.TMDB_BACKDROP_SIZE + movieDetail.backdropPath)
                        .resize(screenSize.x, getResources().getDimensionPixelSize(R.dimen.backdrop_height))
                        .centerCrop()
                        .into(backdropImageView);

                Picasso.with(this)
                        .load(TheMovieDb.TMDB_IMAGE_BASE_URL + TheMovieDb.TMDB_POSTER_SIZE + movieDetail.posterPath)
                        .placeholder(R.mipmap.placeholder)
                        .into(posterImageView);

                releaseDateTextView.setText(year);

                averageRateRatingBar.setMax(10);
                averageRateRatingBar.setRating(movieDetail.averageVote / 2);

                averageRateTextView.setText(averageRate);
                overviewTextView.setText(movieDetail.overview);

                new LoadMovieVideosTask().execute(movieDetail.id);
            }
        }
    }

    private void populateVideos(List<MovieVideo> videos) {
        LinearLayout videoList = (LinearLayout) findViewById(R.id.movie_videos);

        for (int i = 0; i < videos.size(); i++) {
            View videoView = getLayoutInflater().inflate(R.layout.movie_video, null);

            Button playVideoButton = (Button) videoView.findViewById(R.id.b_play_video);
            TextView nameTextView = (TextView) videoView.findViewById(R.id.tv_name);
            TextView typeTextView = (TextView) videoView.findViewById(R.id.tv_type);

            MovieVideo video = videos.get(i);
            nameTextView.setText(video.name);
            typeTextView.setText(video.type);

            playVideoButton.setTag(video);
            playVideoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MovieVideo video = (MovieVideo) view.findViewById(R.id.b_play_video).getTag();

//                    if (video.site == "YouTube") {
                        String videoUrl = "https://www.youtube.com/watch?v=" + video.key;
                        Intent playYoutubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        startActivity(playYoutubeIntent);
//                    }
                }
            });

            videoList.addView(videoView);
        }
    }

    private class LoadMovieVideosTask extends AsyncTask<Integer, Void, List<MovieVideo> > {
        @Override
        protected List<MovieVideo> doInBackground(Integer... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TheMovieDb.TMDB_BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();

            TheMovieDbService service = retrofit.create(TheMovieDbService.class);
            Call<MovieVideosResponse> caller;

            caller = service.listVideos(params[0]);

            try {
                if (caller != null) {
                    MovieVideosResponse movieListResponse = caller.execute().body();
                    if (movieListResponse != null) {
                        return movieListResponse.results;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<MovieVideo> movieVideos) {
            super.onPostExecute(movieVideos);

            populateVideos(movieVideos);
        }
    }
}
