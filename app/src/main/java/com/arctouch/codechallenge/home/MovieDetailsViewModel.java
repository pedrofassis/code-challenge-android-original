package com.arctouch.codechallenge.home;

/**
 *  Handles data for MovieDetailsFragment
 */

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.MovieImageUrlBuilder;

public class MovieDetailsViewModel extends ViewModel {
    private MutableLiveData<Movie> movie;
    private final MovieImageUrlBuilder movieImageUrlBuilder = new MovieImageUrlBuilder();

    public MutableLiveData<Movie> getMovie() {
        if (movie == null) {
            movie = new MutableLiveData<>();
        }
        return movie;
    }

    public String getPosterUrl() {
        if ((getMovie().getValue() == null) || (TextUtils.isEmpty(getMovie().getValue().posterPath)))
            return null;
        return movieImageUrlBuilder.buildPosterUrl(movie.getValue().posterPath);
    }

    public String getBackdropUrl() {
        if ((getMovie().getValue() == null) || (TextUtils.isEmpty(getMovie().getValue().backdropPath)))
            return null;
        return movieImageUrlBuilder.buildBackdropUrl(movie.getValue().backdropPath);
    }

    public void setMovie(Movie pMovie) {
        getMovie().setValue(pMovie);
    }
}
