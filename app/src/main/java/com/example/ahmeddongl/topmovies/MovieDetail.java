package com.example.ahmeddongl.topmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetail extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
    }

        /**
         * A placeholder fragment containing a simple view.
         */
        public static class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

            private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

            private static final int DETAIL_LOADER = 0;

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
                return inflater.inflate(R.layout.fragment_movie_detail, container, false);
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

            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                getLoaderManager().initLoader(DETAIL_LOADER, null, this);
                super.onActivityCreated(savedInstanceState);
            }

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Log.v(LOG_TAG, "In onCreateLoader");
                Intent intent = getActivity().getIntent();
                if (intent == null) {
                    return null;
                }

                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                return new CursorLoader(
                        getActivity(),
                        intent.getData(),
                        MoviesListFragment.MOVIES_COLUMNS,
                        null,
                        null,
                        null
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                Log.v(LOG_TAG, "In onLoadFinished");
                if (!data.moveToFirst()) { return; }

                //get object from intent
                Movie movieItem = Utility.convertCursorRowToMovieObject(data);

                //link views
                movieName = (TextView)getView().findViewById(R.id.detailMovieName);
                movieReleaseDate = (TextView)getView().findViewById(R.id.detailMovieReleaseDate);
                movieRate = (TextView)getView().findViewById(R.id.detailMovieRate);
                movieOverview = (TextView)getView().findViewById(R.id.detailMovieOverview);
                movieImage = (ImageView)getView().findViewById(R.id.detailMovieImage);

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

}

