package com.example.ahmeddongl.topmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.preference.PreferenceManager;

import com.example.ahmeddongl.topmovies.Data.MoviesContract;

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
