package com.example.ahmeddongl.topmovies.Model.Data;

/**
 * Created by Ahmed Dongl on 9/3/2015.
 */
/*This Class will represent a Movie*/
public class Movie {

    public Long id;
    public String originalTitle;
    public String releaseDate;
    public String overview;
    public String posterPath;
    public  double voteAverage;

    public Movie(Long id,String originalTitle,String releaseDate,String overview,String posterPath,double voteAverage) {

        this.id=id;
        this.originalTitle=originalTitle;
        this.releaseDate=releaseDate;
        this.overview=overview;
        this.posterPath=posterPath;
        this.voteAverage=voteAverage;
    }
}
