package com.arctouch.codechallenge.data;

/**
 *  GenresDataSource
 *  Data source for genres list
 *  Acts as cache
 *  Genres list gets updated at the very beginning of app life cycle
 *  All application flow depends initially of me - better be good
 *  TODO - Establish update checkpoints through app life
 */

import android.arch.lifecycle.MutableLiveData;

import com.arctouch.codechallenge.api.TmdbImpl;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.GenreResponse;

import java.util.ArrayList;
import java.util.List;

public class GenresDataSource {
    /**
     * LiveData for genres list
     * Gets updated once on app life cycle, at its very beggining
     *
     */
    private static MutableLiveData<List<Genre>> genresList;

    public static MutableLiveData<List<Genre>> getGenresList() {
        if (genresList == null) {
            genresList = new MutableLiveData<>();
            update();
        }
        return genresList;
    }

    public static List<Genre> getGenres() {
        return genresList.getValue();
    }

    private static void update() {
        TmdbImpl.getInstance().getGenres(r -> setGenres(((GenreResponse) r).genres));
    }

    private static void setGenres(List<Genre> genres) {
        if (genresList.getValue() == null)
            genresList.setValue(new ArrayList<>());
        else
            genresList.getValue().clear();
        genresList.getValue().addAll(genres);
    }
}
