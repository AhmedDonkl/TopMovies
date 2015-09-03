package com.example.ahmeddongl.topmovies;

import java.io.Serializable;

/**
 * Created by Ahmed Dongl on 9/3/2015.
 */
/*This Class will represent a Movie*/
public class Movie implements Serializable {

     int mId;
     String mOriginalTitle;
     String mReleaseDate;
     String mOverview;
     String mPosterPath;
     double mVoteAverage;

    public Movie(int mId,String mOriginalTitle,String mReleaseDate,String mOverview,String mPosterPath,double mVoteAverage) {

        this.mId=mId;
        this.mOriginalTitle=mOriginalTitle;
        this.mReleaseDate=mReleaseDate;
        this.mOverview=mOverview;
        this.mPosterPath=mPosterPath;
        this.mVoteAverage=mVoteAverage;
    }

}
