package com.example.ahmeddongl.topmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

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
public class MovieAdapter extends ArrayAdapter<Movie> {

    Activity mActivity;
    int mLayoutResourceId;
    List<Movie> mMovieItems = null;

    public MovieAdapter(Activity mActivity,int mLayoutResourceId,List<Movie> mMovieItems) {
        super(mActivity,mLayoutResourceId,mMovieItems);
        this.mActivity = mActivity;
        this.mLayoutResourceId = mLayoutResourceId;
        this.mMovieItems=mMovieItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder  holder = null;

        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity)mActivity).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceId, parent, false);

            // well set up the ViewHolder
            holder = new ViewHolder();
            holder.mMoviePicture = (ImageView) convertView.findViewById(R.id.movie_picture);
            holder.mMovieText = (TextView) convertView.findViewById(R.id.movie_text);
            // store the holder with the view.
            convertView.setTag(holder);
        }
        else {
            // we've just avoided calling findViewById() on resource every time
            // just use the viewHolder
            holder = (ViewHolder) convertView.getTag();
        }

        Movie movieItem = mMovieItems.get(position);
        holder.mMovieText.setText(movieItem.mOriginalTitle);
        //load image with picasso and append base path url
        Picasso.with(mActivity).load("http://image.tmdb.org/t/p/w185"+movieItem.mPosterPath).into(holder.mMoviePicture);

        return convertView;
    }
}

