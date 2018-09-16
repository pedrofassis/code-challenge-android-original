package com.arctouch.codechallenge.api;


/**
    Implementaion for TmdbApi to avoid exposing interface and take better care of data

 */

import android.content.Context;
import android.support.v4.os.ConfigurationCompat;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public final class TmdbImpl {

    private static TmdbImpl INSTANCE;
    private static TmdbApi api;
    private static String DEFAULT_LANGUAGE = "pt-BR";
    private static String DEFAULT_REGION = "BR";

    /**
     * Initialize locale information
     * Should be called at initialization and confirguration changes
     * @param context
     */
    public static void init(Context context) {
        Locale current = ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0);
        DEFAULT_LANGUAGE = current.toString().replace('_', '-');
        DEFAULT_REGION = current.getCountry();
    }

    /**
     * Handles instantiation to ensure singleton - Constructor not exposed
     * @return INSTANCE
     */
    public static TmdbImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TmdbImpl();
            api = new Retrofit.Builder()
                    .baseUrl(TmdbApi.URL)
                    .client(new OkHttpClient.Builder().build())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(TmdbApi.class);
        }
        return INSTANCE;
    }

    /**
     * Constructor not exposed to avoid multiple instantiation
     */
    private TmdbImpl() { }

    /**
     * Asynchronous call to genres
     * @param result - Callback
     */
    public void getGenres(TmdbResponse result) {
        if (result == null)
            return;
        api.genres(TmdbApi.API_KEY, DEFAULT_LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> result.onResponse(response));
    }

    /**
     * Asynchronous call to upcomingMovies
     * @param result - Callback
     */
    public void getUpcomingMovies(long Page, TmdbResponse result) {
        if (result == null)
            return;
        api.upcomingMovies(TmdbApi.API_KEY, DEFAULT_LANGUAGE, Page, DEFAULT_REGION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> result.onResponse(r));
    }

    /**
     * Asynchronous call to search
     * @param result - Callback
     */

    public void searchMovies(long Page, String Query, TmdbResponse result) {
        if (result == null)
            return;
        api.searchMovies(TmdbApi.API_KEY, DEFAULT_LANGUAGE, Page, Query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> result.onResponse(r));
    }

    /**
     * Interface that holds result from Api
     */
    public interface TmdbResponse {
        void onResponse(Object result);
    }
}
