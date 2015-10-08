package com.example.ahmeddongl.topmovies.Controller;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.example.ahmeddongl.topmovies.Model.FetchData.FetchSearchData;
import com.example.ahmeddongl.topmovies.R;

public class SearchResults extends ActionBarActivity implements SearchResultsFragment.Callback{

    private String mQuery;
    private boolean mTwoPane;
    private static String DETAIL_FRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        //get search query
        handleIntent(this.getIntent());
        //set title as search query
        getSupportActionBar().setTitle("Search Results : '"+mQuery+"'");
        if (findViewById(R.id.movies_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movies_detail_container, new MovieDetailFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        updateSearch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // update sort by in our second pane using the fragment manager
        if (mQuery != null) {
            MovieDetailFragment df = (MovieDetailFragment)getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if ( null != df ) {
                df.onMovieChanged();
            }
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.DETAIL_URI, contentUri);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movies_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetail.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }

    //handle search intent
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
        }
    }

    /** fetch search results **/
    public void updateSearch() {
        FetchSearchData searchTask = new FetchSearchData(this);
        searchTask.execute(mQuery);
    }
}
