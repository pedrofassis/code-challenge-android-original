package com.arctouch.codechallenge.home;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.MovieImageUrlBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MovieDetailsFragment extends Fragment {
    View v;
    RelativeLayout root;
    Movie movie;

    private final MovieImageUrlBuilder movieImageUrlBuilder = new MovieImageUrlBuilder();

    private TextView titleTextView;
    private TextView genresTextView;
    private TextView releaseDateTextView;
    private TextView overviewTextView;
    private TextView overviewTitleTextView;
    private ImageView posterImageView;
    private ImageView backdropImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (v == null)
            v = View.inflate(getActivity(), R.layout.movie_details, null);

        titleTextView = v.findViewById(R.id.titleTextView);
        genresTextView = v.findViewById(R.id.genresTextView);
        releaseDateTextView = v.findViewById(R.id.releaseDateTextView);
        overviewTextView = v.findViewById(R.id.overviewTextView1);
        overviewTitleTextView = v.findViewById(R.id.overviewTextView);
        posterImageView = v.findViewById(R.id.posterImageView);
        backdropImageView = v.findViewById(R.id.backdropImageView);
        root = v.findViewById(R.id.root);
        fillView();
        return  v;
    }

    public void setData(Movie pMovie) {
        movie = pMovie;
        fillView();
    }

    private void fillView() {
        if ((v == null) || (movie == null))
            return;
        posterImageView.setImageBitmap(null);
        backdropImageView.setImageBitmap(null);
        titleTextView.setText(movie.title);
        genresTextView.setText(TextUtils.join(", ", movie.genres));
        releaseDateTextView.setText(movie.releaseDate);
        overviewTextView.setText(movie.overview);
        overviewTitleTextView.setVisibility(movie.overview.length() > 0? View.VISIBLE : View.GONE);

        String posterPath = movie.posterPath;
        if (!TextUtils.isEmpty(posterPath)) {
            Glide.with(v)
                    .load(movieImageUrlBuilder.buildPosterUrl(posterPath))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                    .into(posterImageView);
        }
        String backdropPath = movie.backdropPath;
        if (!TextUtils.isEmpty(backdropPath)) {
            Glide.with(v)
                    .load(movieImageUrlBuilder.buildBackdropUrl(backdropPath))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                    .into(backdropImageView);
        }
    }

    @Override
    public void onStop() {
        v = null;
        super.onStop();
    }
}
