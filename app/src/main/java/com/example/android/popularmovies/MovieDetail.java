package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rudilee on 6/16/17.
 */

class MovieDetail implements Parcelable {
    public String poster_path;
    public Boolean adult;
    public String overview;
    public String release_date;
    public List<Integer> genre_ids;
    public int id;
    public String original_title;
    public String title;
    public String backdrop_path;
    public Float popularity;
    public int vote_count;
    public Boolean video;
    public Float vote_average;

    protected MovieDetail(Parcel in) {
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        id = in.readInt();
        original_title = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        popularity = in.readFloat();
        vote_count = in.readInt();
        vote_average = in.readFloat();
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
                "Vote Average: " + String.valueOf(vote_average);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(title);
        dest.writeString(backdrop_path);
        dest.writeFloat(popularity);
        dest.writeInt(vote_count);
        dest.writeFloat(vote_average);
    }
}
