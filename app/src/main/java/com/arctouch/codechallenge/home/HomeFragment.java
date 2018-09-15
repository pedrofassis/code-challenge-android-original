package com.arctouch.codechallenge.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.api.Tmdb;
import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.GenreResponse;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.model.UpcomingMoviesResponse;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    View v;
    private ProgressBar progressBar;

    RecyclerView recyclerView;
    PaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;
    private HomeFragmentInterface listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (v == null)
            v = View.inflate(getActivity(), R.layout.home_fragment, null);

        recyclerView = v.findViewById(R.id.recyclerView);
        this.progressBar = v.findViewById(R.id.progressBar);

        if (adapter == null)
            adapter = new PaginationAdapter(getActivity());

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1; //Increment page index to load the next one
                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                (view, position) -> {
                    if (listener != null)
                        listener.itemSelected(adapter.getItem(position));
                }));

        if (Cache.getGenres().size() == 0) {
            Tmdb.getInstance(getActivity()).getGenres(result -> {
                Cache.setGenres(((GenreResponse) result).genres);
                loadFirstPage();
            });
        } else {
            progressBar.setVisibility(View.GONE);
        }
        return v;
    }

    private void loadFirstPage() {
        currentPage = PAGE_START;
        Tmdb.getInstance(getActivity()).getUpcomingMovies(1L, result -> {
            UpcomingMoviesResponse r = (UpcomingMoviesResponse) result;
            for (Movie movie : r.results) {
                movie.genres = new ArrayList<>();
                for (Genre genre : Cache.getGenres()) {
                    if (movie.genreIds.contains(genre.id)) {
                        movie.genres.add(genre);
                    }
                }
            }
            adapter.addAll(r.results);
            progressBar.setVisibility(View.GONE);
            TOTAL_PAGES = r.totalPages;
            isLastPage = TOTAL_PAGES == 1;
            if (!isLastPage) {
                adapter.addLoadingFooter();
            }
        });
    }

    private void loadNextPage() {
        Tmdb.getInstance(getActivity()).getUpcomingMovies(currentPage, result -> {
            UpcomingMoviesResponse r = (UpcomingMoviesResponse) result;
            for (Movie movie : r.results) {
                movie.genres = new ArrayList<>();
                for (Genre genre : Cache.getGenres()) {
                    if (movie.genreIds.contains(genre.id)) {
                        movie.genres.add(genre);
                    }
                }
            }
            adapter.removeLoadingFooter();
            isLoading = false;
            TOTAL_PAGES = r.totalPages;
            isLastPage = TOTAL_PAGES <= currentPage;
            adapter.addAll(r.results);
            if (!isLastPage) {
                adapter.addLoadingFooter();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (v == null)
            return;
        if (isLoading)
            return;
        if ((adapter == null) || (adapter.getItemCount() == 0))
            loadFirstPage();
    }

    @Override
    public void onStop() {
        v = null;
        super.onStop();
    }

    public void setListener(HomeFragmentInterface listener) {
        this.listener = listener;
    }

    public interface HomeFragmentInterface {
        void itemSelected(Movie pItem);
    }
}
