package com.example.ahmeddongl.topmovies;

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

import com.example.ahmeddongl.topmovies.Data.MoviesContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private MovieAdapter mMovieAdapter;
    private static final int MOVIES_LOADER = 0;

    static final String[] MOVIES_COLUMNS = {
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOV_ID,
            MoviesContract.MoviesEntry.COLUMN_MOV_ORIGINAL_TITLE,
            MoviesContract.MoviesEntry.COLUMN_MOV_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_MOV_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_MOV_POSTER_PATH,
            MoviesContract.MoviesEntry.COLUMN_MOV_VOTE_AVERAGE,
            MoviesContract.MoviesEntry.COLUMN_MOV_SORT_BY
    };

    // These indices are tied to MOVIES_COLUMNS.  If MOVIES_COLUMNS changes, these
    // must change.
    static final int COL_MOV_ID = 0;
    static final int COL_MOV_ORIGINAL_TITLE = 1;
    static final int COL_MOV_RELEASE_DATE = 2;
    static final int COL_MOV_OVERVIEW = 3;
    static final int COL_MOV_POSTER_PATH = 4;
    static final int COL_MOV_VOTE_AVERAGE = 5;
    static final int COL_MOV_SORT_BY = 6;

    public MoviesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_list, container, false);

        // Initialize our Adapter.
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView)rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(mMovieAdapter);

        // On click on item listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), MovieDetail.class)
                            .setData(MoviesContract.MoviesEntry.buildMoviesUriWithMovieId(
                                     cursor.getDouble(COL_MOV_ID)
                            ));
                    startActivity(intent);
                }
            }
        });

        return rootView;
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
        Uri moviesUriBySort = MoviesContract.MoviesEntry.buildMoviesUriWithSortBy(sortBy);

        return new CursorLoader(getActivity(),
                moviesUriBySort,
                MOVIES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);
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

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public  void updateMovies() {
        FetchMovieData movieTask = new FetchMovieData(getActivity());
        String sortBy = Utility.getPreferredSortBy(getActivity());
        movieTask.execute(sortBy);
    }

}
