package com.example.ahmeddongl.topmovies.Data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Ahmed Donkl on 9/14/2015.
 */
public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIES_WITH_SORT = 101;
    static final int MOVIES_WITH_MOVIE_ID = 102;

    private static final SQLiteQueryBuilder sMoviesQueryBuilder;

    static{
        sMoviesQueryBuilder = new SQLiteQueryBuilder();
        sMoviesQueryBuilder.setTables(MoviesContract.MoviesEntry.TABLE_NAME );
    }

    //Movies.movies_sort_by = ?
    private static final String sMoviesSortBySelection =
            MoviesContract.MoviesEntry.COLUMN_MOV_SORT_BY + " = ? ";

    //Movies.movies_id = ?
    private static final String sMoviesIDSelection =
             MoviesContract.MoviesEntry.COLUMN_MOV_ID + " = ? ";

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES  + "/*", MOVIES_WITH_SORT);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES  + "/#", MOVIES_WITH_MOVIE_ID);
        return matcher;
    }

    private Cursor getMoviesWithSort(Uri uri, String[] projection, String sortOrder) {
        String sortBy = uri.getPathSegments().get(1);

        return sMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMoviesSortBySelection,
                new String[]{sortBy},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMoviesWithMovieId(Uri uri, String[] projection, String sortOrder) {
        String id = uri.getPathSegments().get(1);

        return sMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
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
            case MOVIES:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIES_WITH_SORT:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIES_WITH_MOVIE_ID:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
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
            case MOVIES:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIES_WITH_SORT:
                retCursor = getMoviesWithSort(uri, projection, sortOrder);
                break;
            case MOVIES_WITH_MOVIE_ID:
                retCursor = getMoviesWithMovieId(uri, projection, sortOrder);
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

        if (match == MOVIES) {
                long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.MoviesEntry.buildMoviesUriWithID(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
            }
            else{
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
            case MOVIES:
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_WITH_SORT:
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME, sMoviesSortBySelection,new String[]{uri.getPathSegments().get(1)});
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
        if (match == MOVIES) {
            rowsUpdated = db.update(MoviesContract.MoviesEntry.TABLE_NAME, values, selection,
                    selectionArgs);
        }
       else {
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
        if (match == MOVIES) {
            db.beginTransaction();
            int returnCount = 0;
            try {
                for (ContentValues value : values) {
                    long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, value);
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
        }
        else {
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
