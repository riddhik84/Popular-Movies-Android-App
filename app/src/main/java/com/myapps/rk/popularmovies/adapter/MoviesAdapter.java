package com.myapps.rk.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.myapps.rk.popularmovies.R;
import com.myapps.rk.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Riddhi Kakadia on 2/27/2016.
 */
public class MoviesAdapter extends CursorAdapter{

    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    public MoviesAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.movies_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        //ImageView moviePoster = (ImageView) view.findViewById(R.id.sample_img);
        String moviePosterImg = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_POSTER_PATH));
        //Log.d(LOG_TAG, "*************** Picasso moviePosterImg ********* " + moviePosterImg);

        Picasso.with(context).
                load(moviePosterImg).
                placeholder(R.drawable.movie_icon).
                error(R.drawable.movie_icon).
                noFade().
                fit().
                //resize(600, 600).
                centerCrop().
                into(viewHolder.moviePoster);

        //Accessibility
        String movieName = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_ORIGINAL_TITLE));
        viewHolder.moviePoster.setContentDescription(movieName);
    }

    public static class ViewHolder {
        public final ImageView moviePoster;

        public ViewHolder(View view){
            moviePoster = (ImageView) view.findViewById(R.id.movie_poster);
        }
    }
}