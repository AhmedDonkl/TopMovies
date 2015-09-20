package com.example.ahmeddongl.topmovies.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ahmed Donkl on 9/14/2015.
 */

/**
 * Defines table and column names for the movies database.
 */
public class MoviesContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.ahmeddongl.topmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOST_POPULAR = "mostPopular";
    public static final String PATH_HIGHEST_RATED= "highestRated";

    /* Tables Common Column */
    // Column with the id of the movie.
    public static final String COLUMN_MOV_ID = "movie_id";
    // Column with the title of the movie.
    public static final String COLUMN_MOV_ORIGINAL_TITLE = "original_title";
    // Column with the date of the movie.
    public static final String COLUMN_MOV_RELEASE_DATE = "release_date";
    // Column with the overview of the movie.
    public static final String COLUMN_MOV_OVERVIEW = "overview";
    // Column with the poster path of the movie.
    public static final String COLUMN_MOV_POSTER_PATH = "poster_path";
    // Column with the rate of the movie.
    public static final String COLUMN_MOV_VOTE_AVERAGE = "vote_average";

    /* Inner class that defines the table contents of the Most popular table */
    public static final class MostPopularEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOST_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOST_POPULAR;

        public static final String TABLE_NAME = "mostPopular";

        public static Uri buildPopularMoviesUriWithID(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPopularMoviesUriWithMovieId(Long movieId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }

    }

    /* Inner class that defines the table contents of the Highest Rated table */
    public static final class HighestRatedEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HIGHEST_RATED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HIGHEST_RATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HIGHEST_RATED;

        public static final String TABLE_NAME = "highestRated";

        public static Uri buildHighestMoviesUriWithID(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildHighestMoviesUriWithMovieId(Long movieId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }

    }
}
