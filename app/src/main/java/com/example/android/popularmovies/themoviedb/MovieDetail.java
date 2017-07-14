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
    public Boolean adult;
    public String overview;
    public String release_date;
    public @Json(name = "genre_ids") List<Integer> genreIds;
    public int id;
    public @Json(name = "original_title") String originalTitle;
    public String title;
    public @Json(name = "backdrop_path") String backdropPath;
    public Float popularity;
    public @Json(name = "vote_count") int voteCount;
    public Boolean video;
    public @Json(name = "vote_average") Float averageVote;

    private MovieDetail(Parcel in) {
        posterPath = in.readString();
        overview = in.readString();
        release_date = in.readString();
        id = in.readInt();
        originalTitle = in.readString();
        title = in.readString();
        backdropPath = in.readString();
        popularity = in.readFloat();
        voteCount = in.readInt();
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
                "Release Date: " + release_date +
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
        dest.writeString(release_date);
        dest.writeInt(id);
        dest.writeString(originalTitle);
        dest.writeString(title);
        dest.writeString(backdropPath);
        dest.writeFloat(popularity);
        dest.writeInt(voteCount);
        dest.writeFloat(averageVote);
    }
}
