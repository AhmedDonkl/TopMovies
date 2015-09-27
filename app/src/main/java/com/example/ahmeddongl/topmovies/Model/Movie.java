package com.example.ahmeddongl.topmovies.Model;

/**
 * Created by Ahmed Dongl on 9/3/2015.
 */
/*This Class will represent a Movie*/
public class Movie {

    public Long mId;
    public String mOriginalTitle;
    public String mReleaseDate;
    public String mOverview;
    public String mPosterPath;
    public  double mVoteAverage;

    public Movie(Long mId,String mOriginalTitle,String mReleaseDate,String mOverview,String mPosterPath,double mVoteAverage) {

        this.mId=mId;
        this.mOriginalTitle=mOriginalTitle;
        this.mReleaseDate=mReleaseDate;
        this.mOverview=mOverview;
        this.mPosterPath=mPosterPath;
        this.mVoteAverage=mVoteAverage;
    }
}
