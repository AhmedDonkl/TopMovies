package com.example.ahmeddongl.topmovies.Data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Ahmed Donkl on 9/14/2015.
 */
public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int HIGHEST_RATED = 101;
    static final int HIGHEST_RATED_WITH_MOVIE_ID = 102;
    static final int MOST_POPULAR = 103;
    static final int MOST_POPULAR_WITH_MOVIE_ID = 104;

    //Movies.movies_id = ?
    private static final String sMoviesIDSelection =
             MoviesContract.COLUMN_MOV_ID + " = ? ";


    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MoviesContract.PATH_MOST_POPULAR, MOST_POPULAR);
        matcher.addURI(authority, MoviesContract.PATH_MOST_POPULAR  + "/#", MOST_POPULAR_WITH_MOVIE_ID);
        matcher.addURI(authority, MoviesContract.PATH_HIGHEST_RATED, HIGHEST_RATED);
        matcher.addURI(authority, MoviesContract.PATH_HIGHEST_RATED  + "/#", HIGHEST_RATED_WITH_MOVIE_ID);
        return matcher;
    }

    private Cursor getPopularMoviesWithMovieId(Uri uri, String[] projection, String sortOrder) {
        String id = uri.getPathSegments().get(1);

        return mOpenHelper.getReadableDatabase().query(
                MoviesContract.MostPopularEntry.TABLE_NAME,
                projection,
                sMoviesIDSelection,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getHighestMoviesWithMovieId(Uri uri, String[] projection, String sortOrder) {
        String id = uri.getPathSegments().get(1);

        return mOpenHelper.getReadableDatabase().query(
                MoviesContract.HighestRatedEntry.TABLE_NAME,
                projection,
                sMoviesIDSelection,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOST_POPULAR:
                return MoviesContract.MostPopularEntry.CONTENT_TYPE;
            case MOST_POPULAR_WITH_MOVIE_ID:
                return MoviesContract.MostPopularEntry.CONTENT_ITEM_TYPE;
            case HIGHEST_RATED:
                return MoviesContract.HighestRatedEntry.CONTENT_TYPE;
            case HIGHEST_RATED_WITH_MOVIE_ID:
                return MoviesContract.HighestRatedEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOST_POPULAR:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MostPopularEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOST_POPULAR_WITH_MOVIE_ID:
                retCursor = getPopularMoviesWithMovieId(uri, projection, sortOrder);
                break;
            case HIGHEST_RATED:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.HighestRatedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case HIGHEST_RATED_WITH_MOVIE_ID:
                retCursor = getHighestMoviesWithMovieId(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOST_POPULAR:
                long _id = db.insert(MoviesContract.MostPopularEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.MostPopularEntry.buildPopularMoviesUriWithID(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
            break;
            case HIGHEST_RATED:
                 _id = db.insert(MoviesContract.HighestRatedEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.HighestRatedEntry.buildHighestMoviesUriWithID(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOST_POPULAR:
                rowsDeleted = db.delete(
                        MoviesContract.MostPopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case HIGHEST_RATED:
                rowsDeleted = db.delete(
                        MoviesContract.HighestRatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(
        Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOST_POPULAR:
                rowsUpdated = db.update(
                        MoviesContract.MostPopularEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case HIGHEST_RATED:
                rowsUpdated = db.update(
                        MoviesContract.HighestRatedEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOST_POPULAR:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MostPopularEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case HIGHEST_RATED:
                db.beginTransaction();
                 returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.HighestRatedEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }

    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
