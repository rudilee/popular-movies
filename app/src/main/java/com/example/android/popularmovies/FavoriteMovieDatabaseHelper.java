package com.example.android.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.FavoriteMovieContract.MovieDetail;

/**
 * Created by rudilee on 7/24/17.
 */

public class FavoriteMovieDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favorite_movie.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteMovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_FAVORITE_MOVIE_TABLE =
                "CREATE TABLE " + MovieDetail.TABLE_NAME + " (" +
                    MovieDetail.COLUMN_POSTER_PATH + " TEXT," +
                    MovieDetail.COLUMN_OVERVIEW + " TEXT," +
                    MovieDetail.COLUMN_RELEASE_DATE + " TEXT," +
                    MovieDetail.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL," +
                    MovieDetail.COLUMN_TITLE + " TEXT NOT NULL," +
                    MovieDetail.COLUMN_BACKDROP_PATH + " TEXT," +
                    MovieDetail.COLUMN_VOTE_AVERAGE + " REAL" +
                ");";

        db.execSQL(CREATE_FAVORITE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String DROP_FAVORITE_MOVIE_TABLE = "DROP TABLE IF EXISTS " + MovieDetail.TABLE_NAME;

        db.execSQL(DROP_FAVORITE_MOVIE_TABLE);

        onCreate(db);
    }
}
