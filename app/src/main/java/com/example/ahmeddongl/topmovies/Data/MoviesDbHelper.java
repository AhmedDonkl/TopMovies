package com.example.ahmeddongl.topmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ahmed Donkl on 9/14/2015.
 */

/**
 * Manages a local database for movies data.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOST_POPULAR_TABLE = "CREATE TABLE " + MoviesContract.MostPopularEntry.TABLE_NAME + " (" +
                MoviesContract.MostPopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.COLUMN_MOV_ID + " LONG NOT NULL, " +
                MoviesContract.COLUMN_MOV_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_MOV_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_MOV_OVERVIEW + " TEXT NOT NULL," +
                MoviesContract.COLUMN_MOV_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_MOV_VOTE_AVERAGE + " DOUBLE NOT NULL);";

        final String SQL_CREATE_HIGHEST_RATED_TABLE = "CREATE TABLE " + MoviesContract.HighestRatedEntry.TABLE_NAME + " (" +
                MoviesContract.HighestRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.COLUMN_MOV_ID + " LONG NOT NULL, " +
                MoviesContract.COLUMN_MOV_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_MOV_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_MOV_OVERVIEW + " TEXT NOT NULL," +
                MoviesContract.COLUMN_MOV_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_MOV_VOTE_AVERAGE + " DOUBLE NOT NULL);";

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + MoviesContract.FavoriteEntry.TABLE_NAME + " (" +
                MoviesContract.HighestRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.COLUMN_MOV_ID + " LONG NOT NULL, " +
                MoviesContract.COLUMN_MOV_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_MOV_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_MOV_OVERVIEW + " TEXT NOT NULL," +
                MoviesContract.COLUMN_MOV_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.COLUMN_MOV_VOTE_AVERAGE + " DOUBLE NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOST_POPULAR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HIGHEST_RATED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MostPopularEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.HighestRatedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}