package com.example.ahmeddongl.topmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesListFragment extends Fragment {

    public static MovieAdapter sMovieAdapter;

    public MoviesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_list, container, false);

        // Set our Adapter.
        sMovieAdapter =
                new MovieAdapter(
                        getActivity(), // The current context (this activity)
                        R.layout.movie_item, // The name of the layout ID.
                        new ArrayList<Movie>()//empty array list
                );

        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView)rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(sMovieAdapter);

        //click listener on grid
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //pass the clicked item object
                Intent intent = new Intent(getActivity(), MovieDetail.class)
                        .putExtra(Intent.EXTRA_TEXT, sMovieAdapter.mMovieItems.get(position));
                startActivity(intent);
            }
        });

        return rootView;
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
       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        movieTask.execute(sortBy);
    }

}
