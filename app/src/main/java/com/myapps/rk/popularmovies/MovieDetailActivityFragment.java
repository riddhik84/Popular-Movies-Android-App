package com.myapps.rk.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    @Bind(R.id.movie_name)
    TextView movie_name;
    @Bind(R.id.movie_image)
    ImageView movie_image_thumbnail;
    @Bind(R.id.movie_ratings)
    TextView movie_ratings;
    @Bind(R.id.movie_overview)
    TextView movie_overview;
    @Bind(R.id.movie_release_date)
    TextView movie_release_date;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootview);

        Intent intent = getActivity().getIntent();

        //Movie Image Thumbnail
        if(intent != null && intent.hasExtra("MovieThumbnail")){
            String movieThumbnail = intent.getStringExtra("MovieThumbnail");
            //Log.d(LOG_TAG, "Movie thumbnail in detail screen ******** " +movieThumbnail);
            Picasso.with(getContext()).
                    load(movieThumbnail).
                    placeholder(R.drawable.placeholder).
                    error(R.drawable.error_thumbnail).
                    noFade().
                    //fit().
                    resize(550, 650).
                    //centerCrop().
                    into(movie_image_thumbnail);
        }
        //Movie Name
        if(intent != null && intent.hasExtra("MovieName")){
            String movieName = intent.getStringExtra("MovieName");
            //Log.d(LOG_TAG, "Movie name in detail screen ******** " +movieName);
            movie_name.setText(movieName);
        }
        //Movie Release date
        if(intent != null && intent.hasExtra("MovieReleaseDate")){
            String movieReleaseDate = intent.getStringExtra("MovieReleaseDate");
            //Log.d(LOG_TAG, "Movie release date in detail screen ******** " + movieReleaseDate);
            movie_release_date.setText("Release Date: "+movieReleaseDate);
        }
        //Movie Ratings
        if(intent != null && intent.hasExtra("MovieRatings")){
            String movieRatings = intent.getStringExtra("MovieRatings");
            //Log.d(LOG_TAG, "Movie ratings in detail screen ******** " + movieRatings);
            movie_ratings.setText("Average Rating: " +movieRatings +"/10");
        }

        //Movie Overview
        if(intent != null && intent.hasExtra("MovieOverview")){
            String movieOverview = intent.getStringExtra("MovieOverview");
            //Log.d(LOG_TAG, "Movie overview in detail screen ******** " + movieOverview);
            movie_overview.setText(movieOverview);
        }

        return rootview;
    }
}
