package com.example.android.popularmovies;

import android.provider.BaseColumns;

/**
 * Created by rudilee on 7/24/17.
 */

public final class FavoriteMovieContract {
    private FavoriteMovieContract() {}

    public static class MovieDetail implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
    }
}
