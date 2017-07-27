package com.example.android.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.android.popularmovies.FavoriteMovieContract.MovieDetail;

import java.util.HashMap;

/**
 * Created by rudilee on 7/27/17.
 */

public class FavoriteMovieContentProvider extends ContentProvider {
    private static String PATH = "/favorite_movies";

    public static final String PROVIDER_NAME = FavoriteMovieContentProvider.class.getName();
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + PATH);

    private static final int FAVORITE_MOVIES = 1;
    private static final int FAVORITE_MOVIES_ID = 2;

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(PROVIDER_NAME, PATH, FAVORITE_MOVIES);
        mUriMatcher.addURI(PROVIDER_NAME, PATH + "/#", FAVORITE_MOVIES_ID);
    }

    private SQLiteDatabase mDatabase;

    private void notifyChange(Uri uri) {
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        FavoriteMovieDatabaseHelper databaseHelper = new FavoriteMovieDatabaseHelper(context);
        mDatabase = databaseHelper.getWritableDatabase();

        return mDatabase != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieDetail.TABLE_NAME);

        switch (mUriMatcher.match(uri)) {
            case FAVORITE_MOVIES:
                queryBuilder.setProjectionMap(new HashMap<String, String>());
                break;
            case FAVORITE_MOVIES_ID:
                queryBuilder.appendWhere(MovieDetail.COLUMN_ID + " = " + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException();
        }

        Cursor cursor = queryBuilder.query(mDatabase, projection, selection, selectionArgs, null, null, sortOrder);

        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case FAVORITE_MOVIES:
                return "vnd.android.cursor.dir/vnd.popularmovies.favorites";
            case FAVORITE_MOVIES_ID:
                return "vnd.android.cursor.item/vnd.popularmovies.favorites";
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (values == null) {
            throw new NullPointerException("Movie details can not be empty");
        }

        long result = mDatabase.insert(MovieDetail.TABLE_NAME, null, values);
        if (result == -1) {
            throw new SQLException("Failed to add new Movie");
        }

        Uri newRowUri = ContentUris.withAppendedId(CONTENT_URI, values.getAsInteger(MovieDetail.COLUMN_ID));
        notifyChange(newRowUri);

        return newRowUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int affectedCount;

        switch (mUriMatcher.match(uri)) {
            case FAVORITE_MOVIES:
                affectedCount = mDatabase.delete(MovieDetail.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_MOVIES_ID:
                String id = uri.getPathSegments().get(1);
                String whereClause = MovieDetail.COLUMN_ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");

                affectedCount = mDatabase.delete(MovieDetail.TABLE_NAME, whereClause, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException();
        }

        notifyChange(uri);

        return affectedCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int affectedCount;

        switch (mUriMatcher.match(uri)) {
            case FAVORITE_MOVIES:
                affectedCount = mDatabase.update(MovieDetail.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FAVORITE_MOVIES_ID:
                String id = uri.getPathSegments().get(1);
                String whereClause = MovieDetail.COLUMN_ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");

                affectedCount = mDatabase.update(MovieDetail.TABLE_NAME, values, whereClause, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException();
        }

        notifyChange(uri);

        return affectedCount;
    }
}
