package com.example.android.popularmovies;

import java.util.Date;

/**
 * Created by rudilee on 6/16/17.
 */

class MovieDetail {
    private String title;
    private String posterUrl;
    private String synopsis;
    private int userRating;
    private Date releaseDate;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public int getUserRating() {
        return userRating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }
}
