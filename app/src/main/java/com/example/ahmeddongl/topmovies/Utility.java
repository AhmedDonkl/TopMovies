package com.example.ahmeddongl.topmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.preference.PreferenceManager;

import com.example.ahmeddongl.topmovies.Model.Data.MoviesContract;
import com.example.ahmeddongl.topmovies.Model.Movie;
import com.example.ahmeddongl.topmovies.Model.Review;
import com.example.ahmeddongl.topmovies.Model.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed Donkl on 9/18/2015.
 */

public class Utility {

    public static String getPreferredSortBy(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    public static Movie convertCursorRowToMovieObject(Cursor cursor) {
        ContentValues cv = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, cv);
        // get row indices for our cursor
        return new Movie(
                cv.getAsLong(MoviesContract.COLUMN_MOV_ID),
                cv.getAsString(MoviesContract.COLUMN_MOV_ORIGINAL_TITLE),
                cv.getAsString(MoviesContract.COLUMN_MOV_RELEASE_DATE),
                cv.getAsString(MoviesContract.COLUMN_MOV_OVERVIEW),
                cv.getAsString(MoviesContract.COLUMN_MOV_POSTER_PATH),
                cv.getAsDouble(MoviesContract.COLUMN_MOV_VOTE_AVERAGE)
        );
    }

    public static List<Trailer> convertCursorToTrailerList(Cursor cursor) {
        List<Trailer> trailersList = new ArrayList<>();
        ContentValues cv = new ContentValues();
        while (cursor.moveToNext()){
            DatabaseUtils.cursorRowToContentValues(cursor, cv);
            // get row indices for our cursor
            trailersList.add(new Trailer(
                    cv.getAsString(MoviesContract.TrailersEntry.COLUMN_TRI_NAME),
                    cv.getAsString(MoviesContract.TrailersEntry.COLUMN_TRI_LINK)
            ));
        }
        return trailersList;
    }

    public static List<Review> convertCursorToReviewList(Cursor cursor) {
        List<Review> reviewsList = new ArrayList<>();
        ContentValues cv = new ContentValues();
        while (cursor.moveToNext()){
            DatabaseUtils.cursorRowToContentValues(cursor, cv);
            // get row indices for our cursor
            reviewsList.add(new Review(
                    cv.getAsString(MoviesContract.ReviewsEntry.COLUMN_REV_AUTHOR),
                    cv.getAsString(MoviesContract.ReviewsEntry.COLUMN_REV_CONTENT)
            ));
        }
        return reviewsList;
    }

    public static ContentValues convertMovieObjectToContentValue(Movie movie) {
        ContentValues moviesValues = new ContentValues();

        moviesValues.put(MoviesContract.COLUMN_MOV_ID, movie.mId);
        moviesValues.put(MoviesContract.COLUMN_MOV_ORIGINAL_TITLE,movie.mOriginalTitle );
        moviesValues.put(MoviesContract.COLUMN_MOV_RELEASE_DATE, movie.mReleaseDate);
        moviesValues.put(MoviesContract.COLUMN_MOV_OVERVIEW, movie.mOverview);
        moviesValues.put(MoviesContract.COLUMN_MOV_POSTER_PATH,movie.mPosterPath);
        moviesValues.put(MoviesContract.COLUMN_MOV_VOTE_AVERAGE, movie.mVoteAverage);

        return moviesValues;
    }

}
