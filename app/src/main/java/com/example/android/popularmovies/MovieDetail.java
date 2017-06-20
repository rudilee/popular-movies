package com.example.android.popularmovies;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rudilee on 6/16/17.
 */

class MovieDetail implements Serializable {
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

    @Override
    public String toString() {
        return  "Title: " + title +
                "Overview: " + overview +
                "Release Date: " + release_date +
                "Vote Average: " + String.valueOf(vote_average);
    }
}
