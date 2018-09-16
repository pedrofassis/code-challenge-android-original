package com.arctouch.codechallenge.home;

/**
 *  Handles data for SearchFragment
 */


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.arctouch.codechallenge.data.GenresDataSource;
import com.arctouch.codechallenge.data.MovieDataSource;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private long totalPages = 0;
    private MutableLiveData<ArrayList<Movie>> moviesList;
    private Runnable onReset;

    private boolean isLoading = false;
    private int currentPage = 0;
    private String currentQuery;

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isLastPage() {
        return currentPage == totalPages;
    }

    public MutableLiveData<ArrayList<Movie>> getMoviesList() {
        if (moviesList == null) {
            moviesList = new MutableLiveData<>();
        }
        if (moviesList.getValue() == null) {
            moviesList.setValue(new ArrayList<>());
        }
        return moviesList;
    }

    private void reset() {
        currentPage = 0;
        getMoviesList().getValue().clear();
        if (onReset != null)
            onReset.run();
    }

    public void search(String Query) {
        reset();
        currentQuery = Query;
        loadNextPage();
    }

    private void updatePage(int Page) {
        isLoading = true;
        MovieDataSource.search(currentQuery, Page, r->{
            totalPages = r.totalPages;
            updateMovies(Page, r.results);
            isLoading = false;
        });
    }

    private void updateMovies(long Page, List<Movie> Movies) {
        if (moviesList.getValue() == null) {
            moviesList.setValue(new ArrayList<>());
        }
        for (Movie movie : Movies) {
            movie.genres = new ArrayList<>();
            for (Genre genre : GenresDataSource.getGenres()) {
                if (movie.genreIds.contains(genre.id)) {
                    movie.genres.add(genre);
                }
            }
        }
        getMoviesList().getValue().addAll(Movies);
        getMoviesList().setValue(getMoviesList().getValue());
    }

    public boolean loadNextPage() {
        /*if (currentPage == totalPages)
            return false;*/
        currentPage++;
        updatePage(currentPage);
        return currentPage == totalPages;
    }

    public void setOnReset(Runnable onReset) {
        this.onReset = onReset;
    }

    public int getItemCount() {
        return getMoviesList().getValue().size();
    }
}
