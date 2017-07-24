package com.example.android.popularmovies.themoviedb;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by rudilee on 6/16/17.
 */

public class MovieDetail implements Parcelable {
    public @Json(name = "poster_path") String posterPath;
    public String overview;
    public @Json(name = "release_date") String releaseDate;
    public int id;
    public String title;
    public @Json(name = "backdrop_path") String backdropPath;
    public @Json(name = "vote_average") Float averageVote;

    public MovieDetail() {}

    private MovieDetail(Parcel in) {
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        id = in.readInt();
        title = in.readString();
        backdropPath = in.readString();
        averageVote = in.readFloat();
    }

    public static final Creator<MovieDetail> CREATOR = new Creator<MovieDetail>() {
        @Override
        public MovieDetail createFromParcel(Parcel in) {
            return new MovieDetail(in);
        }

        @Override
        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };

    @Override
    public String toString() {
        return  "Title: " + title +
                "Overview: " + overview +
                "Release Date: " + releaseDate +
                "Vote Average: " + String.valueOf(averageVote);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(backdropPath);
        dest.writeFloat(averageVote);
    }
}
