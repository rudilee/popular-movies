package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rudilee on 6/16/17.
 */

public class MovieThumbnailAdapter extends RecyclerView.Adapter<MovieThumbnailAdapter.MovieThumbnailViewHolder> {
    private final String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/";
    private final String TMDB_POSTER_SIZE = "w342";

    private List<MovieDetail> mMovieDetails = null;

    public void setMovieDetails(List<MovieDetail> movieDetails) {
        mMovieDetails = movieDetails;

        notifyDataSetChanged();
    }

    @Override
    public MovieThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View movieThumbnailView = LayoutInflater.from(context).inflate(R.layout.movie_thumbnail, parent, false);
        movieThumbnailView.setMinimumHeight(parent.getMeasuredHeight() / 2);

        return new MovieThumbnailViewHolder(movieThumbnailView);
    }

    @Override
    public void onBindViewHolder(MovieThumbnailViewHolder holder, int position) {
        MovieDetail movieDetail = mMovieDetails.get(position);

        Picasso.with(holder.thumbnailImageView.getContext())
                .load(TMDB_POSTER_BASE_URL + TMDB_POSTER_SIZE + movieDetail.poster_path)
                .placeholder(R.mipmap.placeholder)
                .into(holder.thumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return mMovieDetails == null ? 0 : mMovieDetails.size();
    }

    class MovieThumbnailViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;

        public MovieThumbnailViewHolder(View itemView) {
            super(itemView);

            thumbnailImageView = (ImageView) itemView.findViewById(R.id.iv_movie_thumbnail);
        }
    }
}
