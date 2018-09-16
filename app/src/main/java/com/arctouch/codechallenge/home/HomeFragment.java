package com.arctouch.codechallenge.home;

/**
 *  HomeFragment
 *  First fragment added to app
 *  Handles upcoming movies list
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

public class HomeFragment extends Fragment {
    private View v;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private PaginationAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private HomeViewModel model;

    private HomeFragmentInterface listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        //Register observable for upcoming movies list
        model.getMoviesList().observe(this, r -> listUpdated());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (v == null)
            v = View.inflate(getActivity(), R.layout.home_fragment, null);

        // Here we update the reference for the HomeActivity on ViewModel after screen rotation
        // This is necessary because of the reference for the adapter
        // TODO - Better way?
        model = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);

        recyclerView = v.findViewById(R.id.recyclerView);
        this.progressBar = v.findViewById(R.id.progressBar);

        // Adapter will hold directly a reference for the ViewModel list
        // to avoid memory usage
        if (adapter == null)    // Avoid recreation after screen rotation
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

        hideNothingFound();
        return v;
    }

    public void loadFirstPage() {
        model.loadNextPage();
    }

    private void listUpdated() {
        if (model.getMovies().size() == 0) {
            if (model.isLoading())
                progressBar.setVisibility(View.VISIBLE);
            else
                showNothingFound();
        }
        else {
            progressBar.setVisibility(View.GONE);
            hideNothingFound();
            adapter.removeLoadingFooter();
            adapter.notifyDataSetChanged();
            if (!model.isLastPage()) {
                adapter.addLoadingFooter();
            }
        }
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

    public void setListener(HomeFragmentInterface listener) {
        this.listener = listener;
    }

    public interface HomeFragmentInterface {
        void itemSelected(Movie pItem);
    }
}
