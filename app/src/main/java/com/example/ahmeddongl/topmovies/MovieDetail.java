package com.example.ahmeddongl.topmovies;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetail extends ActionBarActivity {

    private TextView movieName;
    private TextView movieReleaseDate;
    private TextView movieRate;
    private TextView movieOverview;
    private ImageView movieImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // get passed intent
        Intent intent = getIntent();
        //get object from intent
        Movie movieItem = (Movie) intent.getSerializableExtra(intent.EXTRA_TEXT);

        //link views
        movieName = (TextView)findViewById(R.id.detailMovieName);
        movieReleaseDate = (TextView)findViewById(R.id.detailMovieReleaseDate);
        movieRate = (TextView)findViewById(R.id.detailMovieRate);
        movieOverview = (TextView)findViewById(R.id.detailMovieOverview);
        movieImage = (ImageView)findViewById(R.id.detailMovieImage);

        //set data on views
        movieName.setText(movieItem.mOriginalTitle);
        movieReleaseDate.setText(movieItem.mReleaseDate);
        movieRate.setText(String.valueOf(movieItem.mVoteAverage)+"/10");
        movieOverview.setText(movieItem.mOverview);
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185"+movieItem.mPosterPath).resize(150,190).into(movieImage);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
