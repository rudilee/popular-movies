package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormat;

import java.text.DateFormat;
import java.text.DecimalFormat;

public class MovieActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        TextView titleTextView = (TextView) findViewById(R.id.tv_title);
        ImageView posterImageView = (ImageView) findViewById(R.id.iv_poster);
        TextView releaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        TextView averageRateTextView = (TextView) findViewById(R.id.tv_average_rate);
        TextView overviewTextView = (TextView) findViewById(R.id.tv_overview);

        Intent intentFromMainActivity = getIntent();
        if (intentFromMainActivity != null) {
            if (intentFromMainActivity.hasExtra("movie-detail")) {
                MovieDetail movieDetail = (MovieDetail) intentFromMainActivity.getSerializableExtra("movie-detail");

                String year = String.valueOf(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(movieDetail.release_date).getYear());
                String averageRate = new DecimalFormat("###.#").format(movieDetail.vote_average) + "/10";

                titleTextView.setText(movieDetail.title);

                Picasso.with(this)
                        .load(TheMovieDb.TMDB_POSTER_BASE_URL + TheMovieDb.TMDB_POSTER_SIZE + movieDetail.poster_path)
                        .placeholder(R.mipmap.placeholder)
                        .into(posterImageView);

                releaseDateTextView.setText(year);
                averageRateTextView.setText(averageRate);
                overviewTextView.setText(movieDetail.overview);
            }
        }
    }
}
