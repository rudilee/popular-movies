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
    private List<MovieDetail> mMovieDetails = null;
    private final MovieThumbnailClickHandler mClickHandler;

    public MovieThumbnailAdapter(MovieThumbnailClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

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
                .load(TheMovieDb.TMDB_POSTER_BASE_URL + TheMovieDb.TMDB_POSTER_SIZE + movieDetail.poster_path)
                .placeholder(R.mipmap.placeholder)
                .into(holder.thumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return mMovieDetails == null ? 0 : mMovieDetails.size();
    }

    public interface MovieThumbnailClickHandler {
        void onClick(MovieDetail movieDetail);
    }

    class MovieThumbnailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView thumbnailImageView;

        public MovieThumbnailViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            thumbnailImageView = (ImageView) itemView.findViewById(R.id.iv_movie_thumbnail);
        }

        @Override
        public void onClick(View view) {
            MovieDetail movieDetail = mMovieDetails.get(getAdapterPosition());
            mClickHandler.onClick(movieDetail);
        }
    }
}
