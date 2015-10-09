package com.example.ahmeddongl.topmovies.Controller;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ahmeddongl.topmovies.Controller.Adapters.MovieAdapter;
import com.example.ahmeddongl.topmovies.Model.Data.MoviesContract;
import com.example.ahmeddongl.topmovies.Model.sync.MoviesSyncAdapter;
import com.example.ahmeddongl.topmovies.R;
import com.example.ahmeddongl.topmovies.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,SwipeRefreshLayout.OnRefreshListener{

    private MovieAdapter mMovieAdapter;
    private static final int MOVIES_LOADER = 0;

    //restore scroll position
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    // These indices are tied to MOVIES_COLUMNS.  If MOVIES_COLUMNS changes, these
    // must change.
    static final int COL_MOV_ID = 1;

    private SwipeRefreshLayout mRefreshLayout;
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

    public MoviesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_list, container, false);

        linkViews(rootView);

        //implement pull to refresh
        pullToRefresh();

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
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mGridView = (GridView)rootView.findViewById(R.id.movie_grid);
    }

    //implement pull to refresh functionality
    private void pullToRefresh(){
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    //Initialize grid view with adapter
    private void InitializeGridView(){
        // Initialize our Adapter.
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);
        // Get a reference to the GridView, and attach this adapter to it.
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
                    if (sortBy.equals("popularity.desc")) {
                        ((Callback) getActivity())
                                .onItemSelected(MoviesContract.MostPopularEntry.buildPopularMoviesUriWithMovieId(
                                        cursor.getLong(COL_MOV_ID)
                                ));
                    } else if (sortBy.equals("vote_average.desc")) {
                        ((Callback) getActivity())
                                .onItemSelected(MoviesContract.HighestRatedEntry.buildHighestMoviesUriWithMovieId(
                                        cursor.getLong(COL_MOV_ID)
                                ));
                    } else {
                        ((Callback) getActivity())
                                .onItemSelected(MoviesContract.FavoriteEntry.buildFavoriteMoviesUriWithMovieId(
                                        cursor.getLong(COL_MOV_ID)
                                ));
                    }
                }
            }
        });
    }

    @Override public void onRefresh() {
        onSortByChanged();
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

    /*
     since we read sort by when we create the loader, all we need to do is restart things
     */
    void onSortByChanged( ) {
        updateMovies();
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        mRefreshLayout.setRefreshing(false);
    }

    public void updateMovies() {
        MoviesSyncAdapter.syncImmediately(getActivity());
    }
}
