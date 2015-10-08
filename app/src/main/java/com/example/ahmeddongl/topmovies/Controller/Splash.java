package com.example.ahmeddongl.topmovies.Controller;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.example.ahmeddongl.topmovies.R;

/*Splash Screen to appear when start app*/
public class Splash extends Activity {

    //play music when start app
    MediaPlayer songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent intent = new Intent(this,MoviesList.class);
        //Start music
        songs = MediaPlayer.create(this, R.raw.show);
        songs.start();

        Thread thread = new Thread() {

            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(intent);
                }
            }

        };

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //to stop music
        songs.release();
        //to delete activity from stack to prevent user to back to it
        finish();
    }

}
