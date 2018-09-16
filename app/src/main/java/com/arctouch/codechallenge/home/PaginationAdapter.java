package com.arctouch.codechallenge.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.MovieImageUrlBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suleiman on 19/10/16.
 */

public class PaginationAdapter extends RecyclerView.Adapter<PaginationAdapter.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Movie> movies;
    private Context context;

    private boolean isLoadingAdded = false;

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final MovieImageUrlBuilder movieImageUrlBuilder = new MovieImageUrlBuilder();

        private final TextView titleTextView;
        private final TextView genresTextView;
        private final TextView releaseDateTextView;
        private final ImageView posterImageView;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            genresTextView = itemView.findViewById(R.id.genresTextView);
            releaseDateTextView = itemView.findViewById(R.id.releaseDateTextView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
        }

        void bind(Movie movie) {
            titleTextView.setText(movie.title);
            genresTextView.setText(TextUtils.join(", ", movie.genres));
            releaseDateTextView.setText(movie.releaseDate);
            posterImageView.setImageResource(R.drawable.ic_image_placeholder);
            String posterPath = movie.posterPath;
            if (!TextUtils.isEmpty(posterPath)) {
                Glide.with(itemView)
                        .load(movieImageUrlBuilder.buildPosterUrl(posterPath))
                        .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                        .into(posterImageView);
            }
        }
    }

    PaginationAdapter(Context context, ArrayList<Movie> pMovies) {
        this.context = context;
        movies = pMovies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
                return new ViewHolder(view);
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                return new ViewHolder(v2);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                holder.bind(movies.get(position));
                break;
            case LOADING:
//                Do nothing
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (movies == null)
            return 0;
        if (!isLoadingAdded)
            return movies.size();
        return movies.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movies.size()) ? LOADING : ITEM;
    }




    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void addLoadingFooter() {
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
    }

    public Movie getItem(int position) {
        return movies.get(position);
    }
}