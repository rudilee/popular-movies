package com.example.android.popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.themoviedb.MovieDetail;
import com.example.android.popularmovies.themoviedb.TheMovieDb;
import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormat;

import java.text.DecimalFormat;

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
            }
        }
    }
}
