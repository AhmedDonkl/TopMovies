package com.example.ahmeddongl.topmovies.Controller;

/**
 * Created by Ahmed Donkl on 9/20/2015.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmeddongl.topmovies.Controller.Adapters.ExpandableReviewAdapter;
import com.example.ahmeddongl.topmovies.Controller.Adapters.ExpandableTrailersAdapter;
import com.example.ahmeddongl.topmovies.Model.Data.MoviesContract;
import com.example.ahmeddongl.topmovies.Model.FetchData.FetchReviewData;
import com.example.ahmeddongl.topmovies.Model.FetchData.FetchTrailerData;
import com.example.ahmeddongl.topmovies.Model.Movie;
import com.example.ahmeddongl.topmovies.Model.Review;
import com.example.ahmeddongl.topmovies.Model.Trailer;
import com.example.ahmeddongl.topmovies.R;
import com.example.ahmeddongl.topmovies.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    private static final int TRAILERS_LOADER = 1;
    private static final int REVIEWS_LOADER = 2;
    static final String DETAIL_URI = "URI";
    private static final String Movies_SHARE_HASH_TAG = " #TopMoviesApp";
    private String mMovieId = "0";
    private String mPosterLink;

    private Uri mUri;
    private Uri mFavoriteUriWithId;
    private Uri mTrailerUriWithId;
    private Uri mReviewUriWithId;

    private TextView movieName;
    private TextView movieReleaseDate;
    private RatingBar movieRate;
    private TextView movieOverview;
    private ImageView movieImage;
    private ImageView expandedImage;
    private View viewContainer;

    private Button favoriteButton;
    private ExpandableListView trailersExpandableList;
    private ExpandableListView reviewsExpandableList;

    private List<Trailer> trailersList;
    private List<Review> reviewsList;

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        }

        //Movie Id
        if(mUri != null){
            mMovieId = mUri.getPathSegments().get(1);
        }

        //to handle view details in tablet
        if(!mMovieId.equals("0")){
            rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
            //link views
            linkViews(rootView);

            //to save movie trailers and reviews on database
            updateTrailersAndReviews();

            //check if this movie is favorite and start it
            checkFavorite();

            //add or remove movie from favorite table
            addOrRemoveFavorite();

            //zoom movie poster
            zoomMoviePoster();
        }

        return rootView;
    }

    //zoom movie poster on click
    private void zoomMoviePoster(){
        // Hook up clicks on the thumbnail views.
        movieImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(movieImage,expandedImage);
            }
        });

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    //to check if this movie is favorite to started it
    private void checkFavorite(){
        //build favorite uri with movie id
        mFavoriteUriWithId = MoviesContract.FavoriteEntry
                .buildFavoriteMoviesUriWithMovieId(Long.valueOf(mMovieId));

        //check if Movie is favorite to started image view
        Cursor favoriteCheck = getActivity().getContentResolver().query(mFavoriteUriWithId, null, null, null, null);
        if(favoriteCheck != null && favoriteCheck.getCount() > 0){
            favoriteButton.setBackgroundResource(R.drawable.favorite_filled_pi);
            favoriteButton.setText("1");
        }
    }

    //to add or remove movie from favorite
    private void addOrRemoveFavorite(){
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if it's blank save else remove
                if(favoriteButton.getText().toString().equals("0")){
                    //make image started
                    favoriteButton.setBackgroundResource(R.drawable.favorite_filled_pi);
                    favoriteButton.setText("1");
                    //add this movie to favorite table on data base
                    //make try and catch to handle insert when sort base is favorites
                    Cursor movieData;
                    String SortBase = mUri.getPathSegments().get(0);
                    try {
                        mUri = Uri.parse(mUri.toString().replace(SortBase,"mostPopular"));
                        movieData = getActivity().getContentResolver().query(mUri, null, null, null, null);
                    }
                    catch (Exception e){
                        mUri = Uri.parse(mUri.toString().replace(SortBase,"highestRated"));
                        movieData = getActivity().getContentResolver().query(mUri, null, null, null, null);
                    }
                    movieData.moveToFirst();
                    Movie movieObject = Utility.convertCursorRowToMovieObject(movieData);
                    ContentValues movieContent = Utility.convertMovieObjectToContentValue(movieObject);
                    getActivity().getContentResolver().insert(MoviesContract.FavoriteEntry.CONTENT_URI, movieContent);

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
                                    favoriteButton.setBackgroundResource(R.drawable.favorite_blank_pi);
                                    favoriteButton.setText("0");
                                    //delete movie from favorite
                                    getActivity().getContentResolver().delete(mFavoriteUriWithId, null, null);

                                    //make toast to user
                                    Toast.makeText(getActivity(), "Removed From Favorites", Toast.LENGTH_LONG).show();
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
        favoriteButton = (Button)rootView.findViewById(R.id.favorite);
        trailersExpandableList = (ExpandableListView) rootView.findViewById(R.id.movies_trailers);
        reviewsExpandableList = (ExpandableListView) rootView.findViewById(R.id.movies_reviews);
        expandedImage = (ImageView) rootView.findViewById(R.id.expanded_image);
        viewContainer = rootView.findViewById(R.id.detail_container);
    }

    //inflate Trailers on list
    private void inflateTrailersList(Cursor trailersData){
        trailersList = Utility.convertCursorToTrailerList(trailersData);
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
                watchYoutubeVideo(trailersList.get(childPosition).link);
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
    private void inflateReviewsList(Cursor reviewData){
        reviewsList = Utility.convertCursorToReviewList(reviewData);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case DETAIL_LOADER:
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
            case TRAILERS_LOADER:
                //build trailer uri with movie id
                mTrailerUriWithId = MoviesContract.TrailersEntry
                        .buildTrailerUriWithMovieId(Long.valueOf(mMovieId));
                return new CursorLoader(
                        getActivity(),
                        mTrailerUriWithId,
                        null,
                        null,
                        null,
                        null
                );
            case REVIEWS_LOADER:
                //build review uri with movie id
                mReviewUriWithId = MoviesContract.ReviewsEntry
                        .buildReviewUriWithMovieId(Long.valueOf(mMovieId));
                return new CursorLoader(
                        getActivity(),
                        mReviewUriWithId,
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

        switch (loader.getId()){
            case DETAIL_LOADER:
                //get object from intent
                Movie movieItem = Utility.convertCursorRowToMovieObject(data);
                //set data on views
                movieName.setText(movieItem.originalTitle);
                movieReleaseDate.setText(movieItem.releaseDate);
                movieRate.setRating((float) movieItem.voteAverage / 2);
                movieOverview.setText(movieItem.overview);
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185" + movieItem.posterPath)
                        .resize(150,190)
                        .into(movieImage);
                mPosterLink = movieItem.posterPath;
            case TRAILERS_LOADER:
                //inflate Trailer on Expandable list
                inflateTrailersList(data);
            case REVIEWS_LOADER:
                //inflate reviews on Expandable list
                inflateReviewsList(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    void onMovieChanged() {
        // replace the uri, since the Movie has changed
        Uri uri = mUri;
        if (null != uri) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            getLoaderManager().restartLoader(TRAILERS_LOADER, null, this);
            getLoaderManager().restartLoader(REVIEWS_LOADER, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_share) {
            String share = null;
            try{
                share = trailersList.get(0).link;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            // Attach an intent to this ShareActionProvider.  You can update this at any time,
            // like when the user selects a new piece of data they might like to share.
            if ( share != null) {
                createShareMovieIntent(share);
            } else {
                Toast.makeText(getActivity(),"No Trailers available",Toast.LENGTH_LONG).show();
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createShareMovieIntent(String share) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "http://www.youtube.com/watch?v=" + share + Movies_SHARE_HASH_TAG);
        startActivity(shareIntent);
    }

    public void updateTrailersAndReviews() {
        //fetch trailers
        FetchTrailerData trailerTask = new FetchTrailerData(getActivity());
        trailerTask.execute(mMovieId);
        //fetch reviews
        FetchReviewData reviewTask = new FetchReviewData(getActivity());
        reviewTask.execute(mMovieId);
    }

    //this function responsible for zooming movie detail poster image
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void zoomImageFromThumb(final View thumbView,ImageView expandedImage) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = expandedImage;
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/original" +mPosterLink)
                .into(expandedImageView);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);

        viewContainer.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}