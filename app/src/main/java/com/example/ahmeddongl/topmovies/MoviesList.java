package com.example.ahmeddongl.topmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MoviesList extends ActionBarActivity {

    private String mSortBy;
    private final String MOVIES_FRAGMENT_TAG = "FFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.listFragment, new MoviesListFragment(), MOVIES_FRAGMENT_TAG)
                    .commit();
        }
        mSortBy =  Utility.getPreferredSortBy(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortBy = Utility.getPreferredSortBy(this);
        // update sort by in our second pane using the fragment manager
        if (sortBy != null && !sortBy.equals(mSortBy)) {
            MoviesListFragment ff = (MoviesListFragment)getSupportFragmentManager().findFragmentByTag(MOVIES_FRAGMENT_TAG);
            if ( null != ff ) {
                ff.onSortByChanged();
            }
            mSortBy = sortBy;
        }
    }
}
