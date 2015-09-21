package com.example.ahmeddongl.topmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MoviesList extends ActionBarActivity implements MoviesListFragment.Callback {

    private String mSortBy;
    private boolean mTwoPane;
    private static String DETAIL_FRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
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
        mSortBy =  Utility.getPreferredSortBy(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortBy = Utility.getPreferredSortBy(this);
        // update sort by in our second pane using the fragment manager
        if (sortBy != null && !sortBy.equals(mSortBy)) {
            MoviesListFragment ff = (MoviesListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if ( null != ff ) {
                ff.onSortByChanged();
            }
            MovieDetailFragment df = (MovieDetailFragment)getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if ( null != df ) {
                df.onMovieChanged();
            }
            mSortBy = sortBy;
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

}
