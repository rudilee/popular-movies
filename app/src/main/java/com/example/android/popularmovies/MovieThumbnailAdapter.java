package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by rudilee on 6/16/17.
 */

public class MovieThumbnailAdapter extends RecyclerView.Adapter<MovieThumbnailAdapter.MovieThumbnailViewHolder> {
    private ArrayList<MovieDetail> movieDetails = new ArrayList<>();

    public void setMovieDetails(ArrayList<MovieDetail> movieDetails) {
        this.movieDetails = movieDetails;

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
        MovieDetail movieDetail = movieDetails.get(position);

        Picasso.with(holder.thumbnailImageView.getContext())
                .load(movieDetail.getPosterUrl())
                .placeholder(R.mipmap.placeholder)
                .into(holder.thumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return movieDetails.size();
    }

    class MovieThumbnailViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;

        public MovieThumbnailViewHolder(View itemView) {
            super(itemView);

            thumbnailImageView = (ImageView) itemView.findViewById(R.id.iv_movie_thumbnail);
        }
    }
}
