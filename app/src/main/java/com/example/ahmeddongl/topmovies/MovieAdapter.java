package com.example.ahmeddongl.topmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Ahmed Dongl on 9/3/2015.
 */

// our ViewHolder.
// caches our views
class ViewHolder {
    TextView mMovieText ;
    ImageView mMoviePicture;
}

/*This is our adapter for the movies */
public class MovieAdapter extends CursorAdapter {

    ViewHolder  holder = null;

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);

        // well set up the ViewHolder
        holder = new ViewHolder();
        holder.mMoviePicture = (ImageView) view.findViewById(R.id.movie_picture);
        holder.mMovieText = (TextView) view.findViewById(R.id.movie_text);
        // store the holder with the view.
        view.setTag(holder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // we've just avoided calling findViewById() on resource every time
        // just use the viewHolder
        holder = (ViewHolder) view.getTag();

        Movie movieItem = Utility.convertCursorRowToMovieObject(cursor);
        holder.mMovieText.setText(movieItem.mOriginalTitle);
        //load image with picasso and append base path url
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185"+movieItem.mPosterPath).into(holder.mMoviePicture);
    }

}

