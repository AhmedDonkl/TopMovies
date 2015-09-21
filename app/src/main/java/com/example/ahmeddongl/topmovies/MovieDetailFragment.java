package com.example.ahmeddongl.topmovies;

/**
 * Created by Ahmed Donkl on 9/20/2015.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    static final String DETAIL_URI = "URI";
    private Uri mUri;

    private TextView movieName;
    private TextView movieReleaseDate;
    private TextView movieRate;
    private TextView movieOverview;
    private ImageView movieImage;


    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        }
        //link views
        movieName = (TextView)rootView.findViewById(R.id.detailMovieName);
        movieReleaseDate = (TextView)rootView.findViewById(R.id.detailMovieReleaseDate);
        movieRate = (TextView)rootView.findViewById(R.id.detailMovieRate);
        movieOverview = (TextView)rootView.findViewById(R.id.detailMovieOverview);
        movieImage = (ImageView)rootView.findViewById(R.id.detailMovieImage);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(),SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void onMovieChanged() {
        // replace the uri, since the Movie has changed
        Uri uri = mUri;
        if (null != uri) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        //get object from intent
        Movie movieItem = Utility.convertCursorRowToMovieObject(data);

        //set data on views
        movieName.setText(movieItem.mOriginalTitle);
        movieReleaseDate.setText(movieItem.mReleaseDate);
        movieRate.setText(String.valueOf(movieItem.mVoteAverage)+"/10");
        movieOverview.setText(movieItem.mOverview);
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185" + movieItem.mPosterPath)
                .resize(150,190)
                .into(movieImage);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

}
