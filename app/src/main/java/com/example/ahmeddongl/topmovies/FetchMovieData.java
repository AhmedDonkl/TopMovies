package com.example.ahmeddongl.topmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ahmeddongl.topmovies.Data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Ahmed Dongl on 8/22/2015.
 */

/*this class responsible for fetch movie data from api */
public class FetchMovieData extends AsyncTask<String, Void, Void> {

    private final Context mContext;

    public FetchMovieData(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        try {

            final String MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_PARAM = "api_key";
            final String API_KEY = "";

            //Url of json file no need to uri builder
            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, params[0])
                    .appendQueryParameter(API_PARAM, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d("link",builtUri.toString());
            // Create the request to url, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();

        } catch (IOException e) {
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            e.printStackTrace();
            Log.i("Error",e.getMessage());
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                    Log.i("Error",e.getMessage());
                }
            }
        }

        try
        {
             GetMovieDataFromJson(movieJsonStr,params[0]);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("Error", e.getMessage());
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    private void GetMovieDataFromJson(String movieJsonStr, String sortBy) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String Array_RESULT = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_ORIGINAL_TITLE = "original_title";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_VOTE_AVERAGE = "vote_average";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(Array_RESULT);

        // Insert the new Movies information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

        for(int i = 0; i < movieArray.length(); i++) {
            // Get the JSON object representing the time
            JSONObject movieObject = movieArray.getJSONObject(i);

            //get data from Object and append to content Values Vector
            ContentValues weatherValues = new ContentValues();

            weatherValues.put(MoviesContract.MoviesEntry.COLUMN_MOV_ID, movieObject.getInt(MOVIE_ID));
            weatherValues.put(MoviesContract.MoviesEntry.COLUMN_MOV_ORIGINAL_TITLE, movieObject.getString(MOVIE_ORIGINAL_TITLE));
            weatherValues.put(MoviesContract.MoviesEntry.COLUMN_MOV_RELEASE_DATE, movieObject.getString(MOVIE_RELEASE_DATE));
            weatherValues.put(MoviesContract.MoviesEntry.COLUMN_MOV_OVERVIEW, movieObject.getString(MOVIE_OVERVIEW));
            weatherValues.put(MoviesContract.MoviesEntry.COLUMN_MOV_POSTER_PATH, movieObject.getString(MOVIE_POSTER_PATH));
            weatherValues.put(MoviesContract.MoviesEntry.COLUMN_MOV_VOTE_AVERAGE, movieObject.getDouble(MOVIE_VOTE_AVERAGE));
            weatherValues.put(MoviesContract.MoviesEntry.COLUMN_MOV_SORT_BY, sortBy);

            cVVector.add(weatherValues);
        }

        // build uri to get cursor with Sort by
        Uri moviesUriBySort = MoviesContract.MoviesEntry.buildMoviesUriWithSortBy(sortBy);

        int deleted = 0;
        //delete data from database which have same sort by
        deleted = mContext.getContentResolver().delete(moviesUriBySort,null,null);
        Log.d("Row Deleted ",String.valueOf(deleted));

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, cvArray);
        }
        Log.d("Row Inserted ",String.valueOf(inserted));

    }

}
