package com.myapps.rk.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.myapps.rk.popularmovies.R;
import com.myapps.rk.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by RKs on 2/27/2016.
 */
public class TrailersAdapter extends CursorAdapter{

    private static final String LOG_TAG = TrailersAdapter.class.getSimpleName();

    public TrailersAdapter(Context context, Cursor cursor, int flags, int loaderID){
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.trailers_list_detail_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String trailerLink = cursor.getString(cursor.getColumnIndex(MoviesContract.Trailers.COLUMN_KEY));
        Log.d(LOG_TAG, "*************** Movie Trailer link ********* " + trailerLink);

        String trailerName = cursor.getString(cursor.getColumnIndex(MoviesContract.Trailers.COLUMN_NAME));
        Log.d(LOG_TAG, "*************** Movie Trailer trailerName ********* " + trailerName);
        viewHolder.movieTrailer.setText(trailerName);
    }

    public static class ViewHolder {
        public final TextView movieTrailer;

        public ViewHolder(View view){
            movieTrailer = (TextView) view.findViewById(R.id.trailer_item_view);
        }
    }
}