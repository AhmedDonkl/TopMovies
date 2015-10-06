package com.example.ahmeddongl.topmovies.Model.FetchData;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ahmeddongl.topmovies.Model.Data.MoviesContract;
import com.example.ahmeddongl.topmovies.R;

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
public class FetchTrailerData extends AsyncTask<String, Void, Void> {

    private final Context mContext;

    public FetchTrailerData(Context context) {
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
                    "http://api.themoviedb.org/3/movie/"+params[0]+"/videos?";
            final String API_PARAM = "api_key";
            final String API_KEY = mContext.getString(R.string.api_key);;

            //Url of json file no need to uri builder
            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_PARAM, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d("Trailers link",builtUri.toString());
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
            GetTrailersDataFromJson(movieJsonStr, params[0]);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("Error", e.getMessage());
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    private void GetTrailersDataFromJson(String movieJsonStr, String movieId) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String Array_RESULT = "results";
        final String TRAILER_NAME = "name";
        final String TRAILER_KEY = "key";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(Array_RESULT);

        // Insert the new trailers information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

        for(int i = 0; i < movieArray.length(); i++) {
            // Get the JSON object representing the time
            JSONObject movieObject = movieArray.getJSONObject(i);

            //get data from Object and append to content Values Vector
            ContentValues moviesValues = new ContentValues();

            moviesValues.put(MoviesContract.COLUMN_MOV_ID,movieId);
            moviesValues.put(MoviesContract.TrailersEntry.COLUMN_TRI_NAME, movieObject.getString(TRAILER_NAME));
            moviesValues.put(MoviesContract.TrailersEntry.COLUMN_TRI_LINK, movieObject.getString(TRAILER_KEY));

            cVVector.add(moviesValues);
        }

        // build uri of Trailers and trailers with id
        Uri trailerUri = MoviesContract.TrailersEntry.CONTENT_URI;
        Uri trailerWithIdUri = MoviesContract.TrailersEntry.buildTrailerUriWithMovieId(Long.valueOf(movieId));

        int deleted = 0;
        //delete data from database
        deleted = mContext.getContentResolver().delete(trailerWithIdUri,null,null);
        Log.d("Trailers Row Deleted ",String.valueOf(deleted));

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(trailerUri, cvArray);
        }
        Log.d("Trailer Row Inserted ",String.valueOf(inserted));

    }
}
