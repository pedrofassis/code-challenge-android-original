package com.arctouch.codechallenge.home;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ClipData;

import com.arctouch.codechallenge.data.GenresDataSource;
import com.arctouch.codechallenge.data.MovieDataSource;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private static final int PAGE_START = 1;

    private long totalPages = 10L;
    private MutableLiveData<HashMap<Long, List<Movie>>> moviesList;
    private Runnable onReset;

    private boolean isLoading = false;
    private int currentPage = 1;
    private String currentQuery;
    private int itemCount = 0;

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
        if (moviesList.getValue() == null) {
            moviesList.setValue(new HashMap<>());
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
        if (getMoviesList().getValue().get(Page) != null)
            return getMoviesList().getValue().get(Page);
        return new ArrayList<>();
    }

    public int getTotalPages() {
        return (int)totalPages;
    }

    public void reset() {
        currentPage = 1;
        getMoviesList().setValue(new HashMap<>());
        itemCount = 0;
        if (onReset != null)
            onReset.run();
    }

    public void search(String Query) {
        currentQuery = Query;
        updatePage(currentPage);
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
        itemCount += Movies.size();
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

    public void setOnReset(Runnable onReset) {
        this.onReset = onReset;
    }

    public int getItemCount() {
        return itemCount;
    }
}
