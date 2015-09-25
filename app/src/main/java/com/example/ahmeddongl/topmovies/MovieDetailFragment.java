package com.example.ahmeddongl.topmovies;

/**
 * Created by Ahmed Donkl on 9/20/2015.
 */

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmeddongl.topmovies.Data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    static final String DETAIL_URI = "URI";
    private Uri mUri;
    private Uri favoriteUriWithId;

    private TextView movieName;
    private TextView movieReleaseDate;
    private RatingBar movieRate;
    private TextView movieOverview;
    private ImageView movieImage;
    private ImageButton favoriteButton;

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
        movieRate = (RatingBar)rootView.findViewById(R.id.detailMovieRate);
        movieOverview = (TextView)rootView.findViewById(R.id.detailMovieOverview);
        movieImage = (ImageView)rootView.findViewById(R.id.detailMovieImage);
        favoriteButton = (ImageButton)rootView.findViewById(R.id.favorite);

        //get movie id from uri
        String movieId = mUri.getPathSegments().get(1);
        favoriteUriWithId = MoviesContract.FavoriteEntry
                .buildFavoriteMoviesUriWithMovieId(Long.valueOf(movieId));

        //check if Movie is favorite to started image view
        Cursor favoriteCheck = getActivity().getContentResolver().query(favoriteUriWithId, null, null, null, null);
        if(favoriteCheck != null && favoriteCheck.getCount() > 0){
            favoriteButton.setImageResource(R.drawable.favorite_filled_pi);
        }

        //add or remove movie from favorite table
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  // if it's blank save else remove
                if(favoriteButton.getDrawable().getConstantState()
                        .equals(getResources().getDrawable(R.drawable.favorite_blank_pi).getConstantState())){
                    //make image started
                    favoriteButton.setImageResource(R.drawable.favorite_filled_pi);

                    //add this movie to favorite table on data base
                    Cursor movieData = getActivity().getContentResolver().query(mUri, null, null, null, null);
                    movieData.moveToFirst();
                    Movie movieObject = Utility.convertCursorRowToMovieObject(movieData);
                    ContentValues movieContent =  Utility.convertMovieObjectToContentValue(movieObject);
                    getActivity().getContentResolver().insert(MoviesContract.FavoriteEntry.CONTENT_URI,movieContent);

                    //make toast to user about succeed
                    Toast.makeText(getActivity(),"Added To Favorites", Toast.LENGTH_LONG).show();
                }
                else{
                    new AlertDialog.Builder(getActivity())
                        .setTitle("Remove Favorite Movie")
                        .setMessage("Are you sure you want to Remove this Movie?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //remove start from image
                                favoriteButton.setImageResource(R.drawable.favorite_blank_pi);

                                //delete movie from favorite
                                getActivity().getContentResolver().delete(favoriteUriWithId,null,null);

                                //make toast to user
                                Toast.makeText(getActivity(),"Removed From Favorites", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                }
            }
        });

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
        movieRate.setRating((float) movieItem.mVoteAverage / 2);
        movieOverview.setText(movieItem.mOverview);
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185" + movieItem.mPosterPath)
                .resize(150,190)
                .into(movieImage);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

}
