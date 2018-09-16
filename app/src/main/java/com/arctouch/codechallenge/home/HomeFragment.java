package com.arctouch.codechallenge.home;

import android.arch.lifecycle.ViewModelProviders;
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
import com.arctouch.codechallenge.model.Movie;

public class HomeFragment extends Fragment {
    View v;
    private ProgressBar progressBar;

    RecyclerView recyclerView;
    PaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    HomeViewModel model;

    private HomeFragmentInterface listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //GenresDataSource.getGenresList().observe(this, s -> model.loadNextPage());
        model = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        model.getMoviesList().observe(this, r -> listUpdated());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (v == null)
            v = View.inflate(getActivity(), R.layout.home_fragment, null);

        model = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        recyclerView = v.findViewById(R.id.recyclerView);
        this.progressBar = v.findViewById(R.id.progressBar);

        if (adapter == null)
            adapter = new PaginationAdapter(getActivity(), model.getMovies());

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                if (!model.isLoading())
                    model.loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return model.getTotalPages();
            }

            @Override
            public boolean isLastPage() {
                return model.isLastPage();
            }

            @Override
            public boolean isLoading() {
                return model.isLoading();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                (view, position) -> {
                    if (listener != null)
                        listener.itemSelected(adapter.getItem(position));
                }));

        return v;
    }

    public void loadFirstPage() {
        model.loadNextPage();
    }

    private void listUpdated() {
        if (model.getMovies().size() > 0)
            progressBar.setVisibility(View.GONE);
        adapter.removeLoadingFooter();
        adapter.notifyDataSetChanged();
        if (!model.isLastPage()) {
            adapter.addLoadingFooter();
        }
    }

    public void setListener(HomeFragmentInterface listener) {
        this.listener = listener;
    }

    public interface HomeFragmentInterface {
        void itemSelected(Movie pItem);
    }
}
