package com.example.ahmeddongl.topmovies.Controller;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ahmeddongl.topmovies.Controller.Adapters.MovieAdapter;
import com.example.ahmeddongl.topmovies.Model.Data.MoviesContract;
import com.example.ahmeddongl.topmovies.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchResultsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private MovieAdapter mSearchAdapter;
    private static final int SEARCH_LOADER = 0;

    private GridView mGridView;
    //restore scroll position
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    // These indices are tied to MOVIES_COLUMNS.  If MOVIES_COLUMNS changes, these
    // must change.
    static final int COL_MOV_ID = 1;
    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri idUri);
    }

    public SearchResultsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_list, container, false);

        //link Ui views
        linkViews(rootView);

        //Initialize grid view with adapter
        InitializeGridView();

        //get position from bundle
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    //to link views
    private void linkViews (View rootView){
        mGridView = (GridView)rootView.findViewById(R.id.movie_grid);
    }

    //Initialize grid view with adapter
    private void InitializeGridView(){
        // Initialize our Adapter.
        mSearchAdapter = new MovieAdapter(getActivity(), null, 0);
        // Get a reference to the GridView, and attach this adapter to it.
        mGridView.setAdapter(mSearchAdapter);
        // On click on item listener
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    // construct search uri with id
                        ((Callback) getActivity())
                                .onItemSelected(MoviesContract.SearchEntry.buildSearchMoviesUriWithMovieId(
                                        cursor.getLong(COL_MOV_ID)
                                ));
                }
            }
        });
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
        getLoaderManager().initLoader(SEARCH_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // build uri to get cursor of searched items
        Uri searchUri =  MoviesContract.SearchEntry.CONTENT_URI;;
        return new CursorLoader(getActivity(),
                searchUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mSearchAdapter.swapCursor(cursor);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mSearchAdapter.swapCursor(null);
    }
}

