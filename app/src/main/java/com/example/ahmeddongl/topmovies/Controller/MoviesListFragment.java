package com.example.ahmeddongl.topmovies.Controller;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ahmeddongl.topmovies.Controller.Adapters.MovieAdapter;
import com.example.ahmeddongl.topmovies.Model.Data.MoviesContract;
import com.example.ahmeddongl.topmovies.Model.FetchData.FetchMovieData;
import com.example.ahmeddongl.topmovies.R;
import com.example.ahmeddongl.topmovies.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private MovieAdapter mMovieAdapter;
    private static final int MOVIES_LOADER = 0;

    //restore scroll position
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    // These indices are tied to MOVIES_COLUMNS.  If MOVIES_COLUMNS changes, these
    // must change.
    static final int COL_MOV_ID = 1;
    static final int COL_MOV_ORIGINAL_TITLE = 2;
    static final int COL_MOV_RELEASE_DATE = 3;
    static final int COL_MOV_OVERVIEW = 4;
    static final int COL_MOV_POSTER_PATH = 5;
    static final int COL_MOV_VOTE_AVERAGE = 6;
    static final int COL_MOV_SORT_BY = 7;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri idUri);
    }

    public MoviesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_list, container, false);

        // Initialize our Adapter.
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        // Get a reference to the GridView, and attach this adapter to it.
        mGridView = (GridView)rootView.findViewById(R.id.movie_grid);
        mGridView.setAdapter(mMovieAdapter);

        // On click on item listener
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String sortBy = Utility.getPreferredSortBy(getActivity());
                    //check about sort by to get correct uri
                    if(sortBy.equals("popularity.desc")) {
                        ((Callback) getActivity())
                                .onItemSelected(MoviesContract.MostPopularEntry.buildPopularMoviesUriWithMovieId(
                                        cursor.getLong(COL_MOV_ID)
                                ));
                    }
                    else if(sortBy.equals("vote_average.desc")){
                        ((Callback) getActivity())
                                .onItemSelected(MoviesContract.HighestRatedEntry.buildHighestMoviesUriWithMovieId(
                                        cursor.getLong(COL_MOV_ID)
                                ));
                    }
                    else{
                        ((Callback) getActivity())
                                .onItemSelected(MoviesContract.FavoriteEntry.buildFavoriteMoviesUriWithMovieId(
                                        cursor.getLong(COL_MOV_ID)
                                ));
                    }
                }
            }
        });

        //get position from bundle
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to GridView.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortBy =Utility.getPreferredSortBy(getActivity());

        // build uri to get cursor with Sort by
        Uri moviesUriBySort;
        if(sortBy.equals("popularity.desc")) {
            moviesUriBySort = MoviesContract.MostPopularEntry.CONTENT_URI;
        }
        else if(sortBy.equals("vote_average.desc")){
            moviesUriBySort = MoviesContract.HighestRatedEntry.CONTENT_URI;
        }
        else{
            moviesUriBySort = MoviesContract.FavoriteEntry.CONTENT_URI;
        }

        return new CursorLoader(getActivity(),
                moviesUriBySort,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);

        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateMovies();
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movies_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(),SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // since we read sort by when we create the loader, all we need to do is restart things
    void onSortByChanged( ) {
        updateMovies();
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    public void updateMovies() {
        FetchMovieData movieTask = new FetchMovieData(getActivity());
        String sortBy = Utility.getPreferredSortBy(getActivity());
        movieTask.execute(sortBy);
    }

}
