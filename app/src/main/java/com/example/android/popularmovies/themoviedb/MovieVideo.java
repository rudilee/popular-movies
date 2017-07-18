package com.example.android.popularmovies.themoviedb;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

/**
 * Created by rudilee on 7/14/17.
 */

public class MovieVideo implements Parcelable {
    public String id;
    public @Json(name = "iso_639_1") String iso639Part1;
    public @Json(name = "iso_3166_1") String iso3166Part1;
    public String key;
    public String name;
    public String site;
    public int size;
    public String type;

    protected MovieVideo(Parcel in) {
        id = in.readString();
        iso639Part1 = in.readString();
        iso3166Part1 = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
        type = in.readString();
    }

    public static final Creator<MovieVideo> CREATOR = new Creator<MovieVideo>() {
        @Override
        public MovieVideo createFromParcel(Parcel in) {
            return new MovieVideo(in);
        }

        @Override
        public MovieVideo[] newArray(int size) {
            return new MovieVideo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(iso639Part1);
        dest.writeString(iso3166Part1);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeInt(size);
        dest.writeString(type);
    }
}
