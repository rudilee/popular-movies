package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.themoviedb.MovieDetail;
import com.example.android.popularmovies.themoviedb.TheMovieDb;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

        Picasso.with(holder.mMovieThumbnail.getContext())
                .load(TheMovieDb.TMDB_IMAGE_BASE_URL + TheMovieDb.TMDB_POSTER_SIZE + movieDetail.posterPath)
                .placeholder(R.mipmap.placeholder)
                .into(holder.mMovieThumbnail);
    }

    @Override
    public int getItemCount() {
        return mMovieDetails == null ? 0 : mMovieDetails.size();
    }

    public interface MovieThumbnailClickHandler {
        void onClick(MovieDetail movieDetail);
    }

    class MovieThumbnailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_movie_thumbnail) ImageView mMovieThumbnail;

        public MovieThumbnailViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            MovieDetail movieDetail = mMovieDetails.get(getAdapterPosition());
            mClickHandler.onClick(movieDetail);
        }
    }
}
