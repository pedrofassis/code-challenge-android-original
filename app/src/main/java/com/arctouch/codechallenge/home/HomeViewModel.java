package com.arctouch.codechallenge.home;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.arctouch.codechallenge.data.GenresDataSource;
import com.arctouch.codechallenge.data.MovieDataSource;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private long totalPages = 10L;
    private MutableLiveData<HashMap<Long, List<Movie>>> moviesList;

    private boolean isLoading = false;
    private int currentPage = 0;

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isLastPage() {
        return currentPage == totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public MutableLiveData<HashMap<Long, List<Movie>>> getMoviesList() {
        if (moviesList == null) {
            moviesList = new MutableLiveData<>();
        }
        return moviesList;
    }

    public List<Movie> getAllMovies() {
        ArrayList<Movie> res = new ArrayList<>();
        for (int a = 1; a <= currentPage; a++)
            res.addAll(getMovies(a));
        return res;
    }

    public List<Movie> getMovies(long Page) {
        return moviesList.getValue().get(Page);
    }

    public int getTotalPages() {
        return (int)totalPages;
    }

    private void updatePage(int Page) {
        isLoading = true;
        MovieDataSource.getUpcoming(Page, r->{
            totalPages = r.totalPages;
            updateMovies(Page, r.results);
            isLoading = false;
        });
    }

    private void updateMovies(long Page, List<Movie> Movies) {
        if (moviesList.getValue() == null) {
            moviesList.setValue(new HashMap<>());
        }
        for (Movie movie : Movies) {
            movie.genres = new ArrayList<>();
            for (Genre genre : GenresDataSource.getGenres()) {
                if (movie.genreIds.contains(genre.id)) {
                    movie.genres.add(genre);
                }
            }
        }
        getMoviesList().getValue().put(Page, Movies);
        getMoviesList().setValue(getMoviesList().getValue());
    }

    public boolean loadNextPage() {
        if (currentPage == totalPages)
            return false;
        currentPage++;
        updatePage(currentPage);
        return currentPage == totalPages;
    }
}
