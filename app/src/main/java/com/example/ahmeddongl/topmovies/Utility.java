package com.example.ahmeddongl.topmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.preference.PreferenceManager;

import com.example.ahmeddongl.topmovies.Model.Data.MoviesContract;
import com.example.ahmeddongl.topmovies.Model.Data.Movie;
import com.example.ahmeddongl.topmovies.Model.Data.Review;
import com.example.ahmeddongl.topmovies.Model.Data.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed Donkl on 9/18/2015.
 */

/** Utility help function class**/
public class Utility {
    /** return sort base saved on shared preference**/
    public static String getPreferredSortBy(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    /** convert cursor to movie object**/
    public static Movie convertCursorRowToMovieObject(Cursor cursor) {
        ContentValues cv = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor,cv);
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

    /** convert cursor to trailers list**/
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

    /** convert cursor to reviews list**/
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

    /** convert movie object to content value**/
    public static ContentValues convertMovieObjectToContentValue(Movie movie) {
        ContentValues moviesValues = new ContentValues();
        moviesValues.put(MoviesContract.COLUMN_MOV_ID, movie.id);
        moviesValues.put(MoviesContract.COLUMN_MOV_ORIGINAL_TITLE,movie.originalTitle );
        moviesValues.put(MoviesContract.COLUMN_MOV_RELEASE_DATE, movie.releaseDate);
        moviesValues.put(MoviesContract.COLUMN_MOV_OVERVIEW, movie.overview);
        moviesValues.put(MoviesContract.COLUMN_MOV_POSTER_PATH,movie.posterPath);
        moviesValues.put(MoviesContract.COLUMN_MOV_VOTE_AVERAGE, movie.voteAverage);

        return moviesValues;
    }
}
