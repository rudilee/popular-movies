package com.example.android.popularmovies.themoviedb;

import com.squareup.moshi.Json;

/**
 * Created by rudilee on 7/14/17.
 */

public class MovieVideo {
    public String id;
    public @Json(name = "iso_639_1") String iso6391;
    public @Json(name = "iso_3166_1") String iso31661;
    public String key;
    public String name;
    public String site;
    public int size;
    public String type;
}
