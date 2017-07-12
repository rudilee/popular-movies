package com.example.android.popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormat;

import java.text.DecimalFormat;

public class MovieActivity extends AppCompatActivity {
    /*
    * https://stackoverflow.com/a/17410076
    * */
    public int dpToPixel(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Toolbar titleToolbar = (Toolbar) findViewById(R.id.title_toolbar);
        setSupportActionBar(titleToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView backdropImageView = (ImageView) findViewById(R.id.iv_backdrop);
        ImageView posterImageView = (ImageView) findViewById(R.id.iv_poster);
        TextView releaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        TextView averageRateTextView = (TextView) findViewById(R.id.tv_average_rate);
        TextView overviewTextView = (TextView) findViewById(R.id.tv_overview);

        Intent intentFromMainActivity = getIntent();
        if (intentFromMainActivity != null) {
            if (intentFromMainActivity.hasExtra("movie-detail")) {
                MovieDetail movieDetail = intentFromMainActivity.getParcelableExtra("movie-detail");

                String year = String.valueOf(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(movieDetail.release_date).getYear());
                String averageRate = new DecimalFormat("###.#").format(movieDetail.vote_average) + "/10";

                CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                collapsingToolbar.setTitle(movieDetail.title);

                Point screenSize = new Point();
                getWindowManager().getDefaultDisplay().getSize(screenSize);

                Picasso.with(this)
                        .load(TheMovieDb.TMDB_IMAGE_BASE_URL + TheMovieDb.TMDB_BACKDROP_SIZE + movieDetail.backdrop_path)
                        .resize(screenSize.x, dpToPixel(256))
                        .centerCrop()
                        .into(backdropImageView);

                Picasso.with(this)
                        .load(TheMovieDb.TMDB_IMAGE_BASE_URL + TheMovieDb.TMDB_POSTER_SIZE + movieDetail.poster_path)
                        .placeholder(R.mipmap.placeholder)
                        .into(posterImageView);

                releaseDateTextView.setText(year);
                averageRateTextView.setText(averageRate);
                overviewTextView.setText(movieDetail.overview);
            }
        }
    }
}
