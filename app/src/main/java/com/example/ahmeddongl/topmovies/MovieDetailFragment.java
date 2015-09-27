package com.example.ahmeddongl.topmovies;

/**
 * Created by Ahmed Donkl on 9/20/2015.
 */

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmeddongl.topmovies.Data.MoviesContract;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    static final String DETAIL_URI = "URI";
    private String movieId;

    private Uri mUri;
    private Uri favoriteUriWithId;
    private Uri trailerUriWithId;
    private Uri reviewUriWithId;

    private TextView movieName;
    private TextView movieReleaseDate;
    private RatingBar movieRate;
    private TextView movieOverview;
    private ImageView movieImage;
    private ImageButton favoriteButton;
    private ExpandableListView trailersExpandableList;
    private ExpandableListView reviewsExpandableList;

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

        //Movie Id
        if(mUri != null){
        movieId = mUri.getPathSegments().get(1);
        }

        //link views
        linkViews(rootView);

        //to save movie trailers and reviews on database
        updateTrailersAndReviews();

        //check if this movie is favorite and start it
        checkFavorite();

        //add or remove movie from favorite table
        addOrRemoveFavorite();

        //inflate Trailer on Expandable list
        inflateTrailersList();

        //inflate reviews on Expandable list
        inflateReviewsList();

        return rootView;
    }

    //to check if this movie is favorite to started it
    private void checkFavorite(){
        //build favorite uri with movie id
        favoriteUriWithId = MoviesContract.FavoriteEntry
                .buildFavoriteMoviesUriWithMovieId(Long.valueOf(movieId));

        //check if Movie is favorite to started image view
        Cursor favoriteCheck = getActivity().getContentResolver().query(favoriteUriWithId, null, null, null, null);
        if(favoriteCheck != null && favoriteCheck.getCount() > 0){
            favoriteButton.setImageResource(R.drawable.favorite_filled_pi);
        }
    }

    //to add or remove movie from favorite
    private void addOrRemoveFavorite(){
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
                    Toast.makeText(getActivity(), "Added To Favorites", Toast.LENGTH_LONG).show();
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
    }

    //to link views
    private void linkViews (View rootView){
        movieName = (TextView)rootView.findViewById(R.id.detailMovieName);
        movieReleaseDate = (TextView)rootView.findViewById(R.id.detailMovieReleaseDate);
        movieRate = (RatingBar)rootView.findViewById(R.id.detailMovieRate);
        movieOverview = (TextView)rootView.findViewById(R.id.detailMovieOverview);
        movieImage = (ImageView)rootView.findViewById(R.id.detailMovieImage);
        favoriteButton = (ImageButton)rootView.findViewById(R.id.favorite);
        trailersExpandableList = (ExpandableListView) rootView.findViewById(R.id.movies_trailers);
        reviewsExpandableList = (ExpandableListView) rootView.findViewById(R.id.movies_reviews);
    }

    //inflate Trailers on list
    private void inflateTrailersList(){
        //build trailer uri with movie id
        trailerUriWithId = MoviesContract.TrailersEntry
                .buildTrailerUriWithMovieId(Long.valueOf(movieId));

        //check if trailers is in database
        final Cursor trailersData = getActivity().getContentResolver().query(trailerUriWithId, null, null, null, null);
        final List<Trailer> trailersList = Utility.convertCursorToTrailerList(trailersData);
        ExpandableTrailersAdapter trailersAdapter = new ExpandableTrailersAdapter(getActivity(),trailersList);

        trailersExpandableList.setAdapter(trailersAdapter);
        trailersExpandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });

        // Expandable list view  on child click listener
        trailersExpandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                watchYoutubeVideo(trailersList.get(childPosition).mLink);
                return false;
            }
        });

    }

    //intent to play trailers video
    private void watchYoutubeVideo(String id){
        try{
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v="+id));
            startActivity(intent);
        }catch (ActivityNotFoundException ex){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        }
    }

    //inflate Review on list
    private void inflateReviewsList(){
        //build review uri with movie id
        reviewUriWithId = MoviesContract.ReviewsEntry
                .buildReviewUriWithMovieId(Long.valueOf(movieId));

        //check if review is in database
        final Cursor reviewData = getActivity().getContentResolver().query(reviewUriWithId, null, null, null, null);
        final List<Review> reviewsList = Utility.convertCursorToReviewList(reviewData);
        ExpandableReviewAdapter reviewAdapter = new ExpandableReviewAdapter(getActivity(),reviewsList);

        reviewsExpandableList.setAdapter(reviewAdapter);
        reviewsExpandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });

    }

    //this function called to make expandable list view on scroll view
    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

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

    void onMovieChanged() {
        // replace the uri, since the Movie has changed
        Uri uri = mUri;
        if (null != uri) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    public void updateTrailersAndReviews() {
        //fetch trailers
        FetchTrailerData trailerTask = new FetchTrailerData(getActivity());
        trailerTask.execute(movieId);
        //fetch reviews
        FetchReviewData reviewTask = new FetchReviewData(getActivity());
        reviewTask.execute(movieId);
    }
}
