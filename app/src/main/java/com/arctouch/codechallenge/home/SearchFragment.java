package com.arctouch.codechallenge.home;

/**
 *  SearchFragment
 *  Displayed on user search queries
 *  Handles search movies result
 */

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

public class SearchFragment extends Fragment {
    private View v;
    private ProgressBar progressBar;

    private PaginationAdapter adapter;
    private SearchFragmentInterface listener;

    private SearchViewModel model;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        model.getMoviesList().observe(this, r -> listUpdated());
        model.setOnReset(this::reset);
    }

    private void reset() {
        hideNothingFound();
        adapter.removeLoadingFooter();
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (v == null)
            v = View.inflate(getActivity(), R.layout.home_fragment, null);

        model = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        this.progressBar = v.findViewById(R.id.progressBar);

        if (adapter == null)
            adapter = new PaginationAdapter(getActivity(), model.getMoviesList().getValue());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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
            public boolean isLastPage() {
                return model.isLastPage();
            }

            @Override
            public boolean isLoading() {
                return model.isLoading();
            }
        });

        recyclerView.removeOnItemTouchListener(touchListener);
        recyclerView.addOnItemTouchListener(touchListener);

        hideNothingFound();
        return v;
    }

    private final RecyclerItemClickListener touchListener = new RecyclerItemClickListener(getActivity(),
            (view, position) -> {
                if (listener != null)
                    listener.itemSelected(adapter.getItem(position));
            });

    private void listUpdated() {
        if (model.getItemCount() == 0) {
            if (model.isLoading())
                progressBar.setVisibility(View.VISIBLE);
            else
                showNothingFound();
        }
        else {
            hideNothingFound();
            progressBar.setVisibility(View.GONE);
            adapter.removeLoadingFooter();
            if (!model.isLastPage()) {
                adapter.addLoadingFooter();
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showNothingFound() {
        progressBar.setVisibility(View.GONE);
        v.findViewById(R.id.imageView).setVisibility(View.VISIBLE);
        v.findViewById(R.id.textView).setVisibility(View.VISIBLE);
    }

    private void hideNothingFound() {
        v.findViewById(R.id.imageView).setVisibility(View.GONE);
        v.findViewById(R.id.textView).setVisibility(View.GONE);
    }

    public void setListener(SearchFragmentInterface listener) {
        this.listener = listener;
    }

    public interface SearchFragmentInterface {
        void itemSelected(Movie pItem);
    }
}