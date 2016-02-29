package com.myapps.rk.popularmovies;

import android.util.Log;

/**
 * Created by Riddhi Kakadia on 2/27/2016.
 */
public class PopularMovies {

    String movieName;
    //int moviePoster;
    String moviePoster;

    public PopularMovies(String movieName, String moviePoster)
    {
        this.movieName = movieName;
        Log.d("", "Movie name in class ***************" +movieName);
        this.moviePoster = moviePoster;
        Log.d("", "Movie Poster name in class ***************" +moviePoster);
    }
}
