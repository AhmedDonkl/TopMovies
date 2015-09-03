package com.example.ahmeddongl.topmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/*Splash Screen to appear when start app*/
public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent intent = new Intent(this,MoviesList.class);

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
        //to delete activity from stack to prevent user to back to it
        finish();
    }

}
