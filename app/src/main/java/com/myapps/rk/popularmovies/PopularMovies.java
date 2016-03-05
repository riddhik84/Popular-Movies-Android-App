package com.myapps.rk.popularmovies;

import android.util.Log;

/**
 * Created by Riddhi Kakadia on 2/27/2016.
 */
public class PopularMovies {

    String movieName;
    String moviePoster;
    String movieOverview;
    String movieRating;
    String movieReleaseDate;

    public PopularMovies(String movieName, String moviePoster, String movieOverview,
                         String movieRating, String movieReleaseDate)
    {
        this.movieName = movieName;
        //Log.d("", "Movie name in class ***************" +movieName);
        this.moviePoster = moviePoster;
        //Log.d("", "Movie Poster name in class ***************" +moviePoster);
        this.movieOverview = movieOverview;
        //Log.d("", "Movie Overview name in class ***************" +movieOverview);
        this.movieRating = movieRating;
        //Log.d("", "Movie Rating in class ***************" +movieRating);
        this.movieReleaseDate = movieReleaseDate;
        //Log.d("", "Movie movieReleaseDate name in class ***************" +movieReleaseDate);
    }
}
