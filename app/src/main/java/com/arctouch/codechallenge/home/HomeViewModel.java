package com.arctouch.codechallenge.home;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.arctouch.codechallenge.data.GenresDataSource;
import com.arctouch.codechallenge.data.MovieDataSource;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private long totalPages = 0;
    private MutableLiveData<ArrayList<Movie>> moviesList;

    private boolean isLoading = false;
    private int currentPage = 0;

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isLastPage() {
        return currentPage >= totalPages;
    }

    public MutableLiveData<ArrayList<Movie>> getMoviesList() {
        if (moviesList == null) {
            moviesList = new MutableLiveData<>();
        }
        return moviesList;
    }

    public ArrayList<Movie> getMovies() {
        if (getMoviesList().getValue() == null) {
            getMoviesList().setValue(new ArrayList<>());
            //loadNextPage();
        }
        return getMoviesList().getValue();
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
        currentPage++;
        updatePage(currentPage);
        return currentPage == totalPages;
    }
}
