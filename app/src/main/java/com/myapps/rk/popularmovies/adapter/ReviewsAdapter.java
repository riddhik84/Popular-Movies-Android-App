package com.myapps.rk.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.myapps.rk.popularmovies.R;
import com.myapps.rk.popularmovies.data.MoviesContract;

/**
 * Created by Riddhi Kakadia on 2/27/2016.
 */
public class ReviewsAdapter extends CursorAdapter {

    private static final String LOG_TAG = ReviewsAdapter.class.getSimpleName();

    public ReviewsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.reviews_list_detail_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String reviewAuthor = cursor.getString(cursor.getColumnIndex(MoviesContract.Reviews.COLUMN_AUTHOR));
      //  Log.d(LOG_TAG, "*************** Movie reviewAuthor ********* " + reviewAuthor);
        viewHolder.reviewAuthor.setText(context.getResources().getString(R.string.author_header) + " "+ reviewAuthor);

        String reviewContent = cursor.getString(cursor.getColumnIndex(MoviesContract.Reviews.COLUMN_CONTENT));
      //  Log.d(LOG_TAG, "*************** Movie reviewContent ********* " + reviewContent);
        viewHolder.reviewContent.setText(context.getResources().getString(R.string.review_header) + " "+ reviewContent + "\n");
    }

    public static class ViewHolder {
        public final TextView reviewAuthor;
        public final TextView reviewContent;

        public ViewHolder(View view) {
            reviewAuthor = (TextView) view.findViewById(R.id.review_author);
            reviewContent = (TextView) view.findViewById(R.id.review_content);
        }
    }
}