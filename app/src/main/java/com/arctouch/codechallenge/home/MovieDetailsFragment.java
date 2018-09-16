package com.arctouch.codechallenge.home;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.MovieImageUrlBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class MovieDetailsFragment extends Fragment {
    View v;
    MovieDetailsViewModel model;

    private TextView titleTextView;
    private TextView genresTextView;
    private TextView releaseDateTextView;
    private TextView overviewTextView;
    private TextView overviewTitleTextView;
    private ImageView posterImageView;
    private ImageView backdropImageView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(MovieDetailsViewModel.class);
        model.getMovie().observe(this, r -> fillView());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (v == null)
            v = View.inflate(getActivity(), R.layout.movie_details, null);
        titleTextView = v.findViewById(R.id.titleTextView);
        genresTextView = v.findViewById(R.id.genresTextView);
        releaseDateTextView = v.findViewById(R.id.releaseDateTextView);
        overviewTextView = v.findViewById(R.id.overviewTextView1);
        overviewTitleTextView = v.findViewById(R.id.overviewTextView);
        posterImageView = v.findViewById(R.id.posterImageView);
        backdropImageView = v.findViewById(R.id.backdropImageView);
        return  v;
    }

    private void fillView() {
        if (v == null)
            return;
        Movie movie = model.getMovie().getValue();
        if (movie == null)
            return;
        backdropImageView.setImageBitmap(null);
        titleTextView.setText(movie.title);
        genresTextView.setText(TextUtils.join(", ", movie.genres));
        releaseDateTextView.setText(movie.releaseDate);
        overviewTextView.setText(movie.overview);
        overviewTitleTextView.setVisibility(movie.overview.length() > 0? View.VISIBLE : View.GONE);

        String posterPath = model.getPosterUrl();
        if (!TextUtils.isEmpty(posterPath)) {
            Glide.with(v)
                    .load(posterPath)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                    .into(posterImageView);
        }
        String backdropPath = model.getBackdropUrl();
        if (!TextUtils.isEmpty(backdropPath)) {
            Glide.with(v)
                    .load(backdropPath)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            AlphaAnimation animation1 = new AlphaAnimation(0f, 0.3f);
                            animation1.setDuration(500);
                            animation1.setFillAfter(true);
                            backdropImageView.startAnimation(animation1);
                            return false;
                        }
                    })
                    .into(backdropImageView);
        }
    }
}
