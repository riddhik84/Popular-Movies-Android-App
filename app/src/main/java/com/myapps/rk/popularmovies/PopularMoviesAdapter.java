package com.myapps.rk.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by RKs on 2/27/2016.
 */
public class PopularMoviesAdapter extends ArrayAdapter<PopularMovies> {

    private static final String LOG_TAG = PopularMoviesAdapter.class.getSimpleName();

    public PopularMoviesAdapter(Activity context, List<PopularMovies> popularMoviesList) {
        super(context, 0, popularMoviesList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PopularMovies popularMovies = getItem(position);
        //Log.d(LOG_TAG, "Position number.....*************** " +position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movies_list_item, parent, false);
            //Log.d(LOG_TAG, "Inside if (converview == null) *************** ");
        }

            TextView movieName = (TextView) convertView.findViewById(R.id.movie_name);
            movieName.setText(popularMovies.movieName);
            //Log.d(LOG_TAG, "Movie name is *******************" + popularMovies.movieName);

            ImageView moviePoster = (ImageView) convertView.findViewById(R.id.movie_poster);
            //moviePoster.setImageResource(popularMovies.moviePoster);
            //Log.d(LOG_TAG, "Movie Poster is *******************" + popularMovies.moviePoster);
            Picasso.with(getContext()).
                    load(popularMovies.moviePoster).
                    placeholder(R.drawable.placeholder).
                    error(R.drawable.error).
                    noFade().
                    fit().
                    //resize(500, 600)
                    //centerCrop().
                    into(moviePoster);

        return convertView;
    }
}
