package com.arctouch.codechallenge.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.model.Movie;

public class HomeActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    HomeFragment homeFragment;
    SearchFragment searchFragment;
    MovieDetailsFragment movieDetailsFragment;
    SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(this);
        homeFragment = (HomeFragment) fm.findFragmentByTag("home");
        movieDetailsFragment = (MovieDetailsFragment) fm.findFragmentByTag("details");
        searchFragment = (SearchFragment) fm.findFragmentByTag("search");

        if (homeFragment == null) {
            homeFragment = new HomeFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.root, homeFragment, "home").commit();
        }
        homeFragment.setListener(this::showDetails);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            showSearch(query);
        }
    }

    private void showDetails(Movie item) {
        if (movieDetailsFragment == null)
            movieDetailsFragment = new MovieDetailsFragment();
        movieDetailsFragment.setData(item);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.root, movieDetailsFragment, "details").addToBackStack("home");
        ft.commit();
    }

    private void showSearch(String query) {
        if (searchFragment == null)
            searchFragment = new SearchFragment();
        searchFragment.setListener(this::showDetails);
        searchFragment.setQuery(query);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.root, searchFragment, "search").addToBackStack("home");
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryRefinementEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                    searchView.setQuery(null, false);
                    searchView.onActionViewCollapsed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(
                getSupportFragmentManager().getBackStackEntryCount() > 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setDisplayHomeAsUpEnabled(
                getSupportFragmentManager().getBackStackEntryCount() > 0);
    }
}
