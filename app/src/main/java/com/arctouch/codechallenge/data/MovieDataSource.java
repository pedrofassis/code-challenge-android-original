package com.arctouch.codechallenge.data;

/**
 *  MoviesDataSource
 *  Data source for movies list
 *  Handles upcoming movie list and seatch movies requests
 */

import com.arctouch.codechallenge.api.TmdbImpl;
import com.arctouch.codechallenge.model.UpcomingMoviesResponse;

public class MovieDataSource {

    public static void getUpcoming(long Page, UpcomingMovieListCallback callback) {
        TmdbImpl.getInstance().getUpcomingMovies(Page, r -> {
                    if (callback != null)
                        callback.updated((UpcomingMoviesResponse) r);
                }
        );
    }

    public static void search(String Query, long Page, UpcomingMovieListCallback callback) {
        TmdbImpl.getInstance().searchMovies(Page, Query, r -> {
                    if (callback != null)
                        callback.updated((UpcomingMoviesResponse) r);
                }
        );
    }

    public interface UpcomingMovieListCallback {
        void updated(UpcomingMoviesResponse response);
    }
}
