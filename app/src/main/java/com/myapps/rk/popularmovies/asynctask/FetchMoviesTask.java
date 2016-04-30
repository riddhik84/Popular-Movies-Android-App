package com.myapps.rk.popularmovies.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.myapps.rk.popularmovies.BuildConfig;
import com.myapps.rk.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import com.myapps.rk.popularmovies.data.MoviesContract.*;
import com.myapps.rk.popularmovies.data.MoviesDbHelper;

import junit.framework.Test;

/**
 * Created by Riddhi Kakadia on 4/13/2016.
 * <p/>
 * 1. sort by popularity:
 * http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=
 * 2. sort by highly rated:
 * http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=
 */

public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private final Context mContext;

    public FetchMoviesTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        //API KEY
        final String apiKey = BuildConfig.MOVIEDB_API_KEY;

        //Will contain the raw JSON response as a string.
        String moviesJsonResponse = null;

        final String MOVIES_BASE_URL = mContext.getString(R.string.movies_base_url);
        final String QUERY_PARAM = "sort_by";
        final String API_KEY = "api_key";

        String queryParamValue = "popularity.desc";
        String sortOrder = params[0];

        try {
            if (params[0].equalsIgnoreCase(mContext.getResources().getString(R.string.pref_sort_favourite))) {
                return null; //param[0] will not be favourite
            } else {
                //Fetch movies data
                if (params[0].equalsIgnoreCase(mContext.getResources().getString(R.string.pref_sort_most_popular))) {
                    queryParamValue = "popularity.desc";
                } else if (params[0].equals(mContext.getResources().getString(R.string.pref_sort_high_rated))) {
                    queryParamValue = "vote_average.desc";
                }

                Uri buildUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, queryParamValue)
                        .appendQueryParameter(API_KEY, apiKey)
                        .build();

                URL moviesUrl = new URL(buildUri.toString());
                //   Log.d(LOG_TAG, "Build URL = " + buildUri.toString());

                //Request/Response to/from MoviesDB
                HttpRequestResponse hrr = new HttpRequestResponse();
                moviesJsonResponse = hrr.doGetRequest(moviesUrl.toString());
                //    Log.d(LOG_TAG, "json response: " + moviesJsonResponse);

                //Get data from Json and insert it in table
                getMoviesFromJson(moviesJsonResponse, sortOrder);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Exception", e);
            return null;
        } finally {

        }
        return null;
    }

    protected void getMoviesFromJson(String movieJsonString, String sortOrder) throws JSONException {
        //   Log.d(LOG_TAG, "getMoviesFromJson() sortOrder " + sortOrder);

        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String MOVIE_ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String VOTE_AVERAGE = "vote_average";

        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_IMAGE_TYPE = "w185";
        String fullPosterPath = "";

        JSONObject moviesOutput = new JSONObject(movieJsonString);
        JSONArray moviesArray = moviesOutput.getJSONArray("results");
        //Log.d(LOG_TAG, "****** JSON result length.. " + results.length());

        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());
        for (int i = 0; i < moviesArray.length(); i++) {

            fullPosterPath = POSTER_BASE_URL + POSTER_IMAGE_TYPE;

            JSONObject movieData = moviesArray.getJSONObject(i);

            ContentValues movieValues = new ContentValues();

            fullPosterPath = fullPosterPath + movieData.getString(POSTER_PATH);
            movieValues.put(Movies.COLUMN_POSTER_PATH, fullPosterPath);
            movieValues.put(Movies.COLUMN_OVERVIEW, movieData.getString(OVERVIEW));
            movieValues.put(Movies.COLUMN_RELEASE_DATE, movieData.getString(RELEASE_DATE));
            movieValues.put(Movies.COLUMN_MOVIE_ID, movieData.getString(MOVIE_ID));
            movieValues.put(Movies.COLUMN_ORIGINAL_TITLE, movieData.getString(ORIGINAL_TITLE));
            movieValues.put(Movies.COLUMN_VOTE_AVERAGE, movieData.getString(VOTE_AVERAGE));
            movieValues.put(Movies.COLUMN_SORT_ORDER, sortOrder); //sort order to filter movies

            cVVector.add(movieValues);
        }

        int inserted = 0;
        //add values to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(Movies.CONTENT_URI, cvArray);
        }
        //Log.d(LOG_TAG, "FetchMoviesTask Complete. " + inserted + " Inserted");

        //TestTable(Movies.TABLE_NAME, sortOrder);
    }

//    public void TestTable(String tableName, String sortOrder) {
//        /**
//         * String[] args = { "first string", "second@string.com" };
//         Cursor cursor = db.query("TABLE_NAME", null, "name=? AND email=?", args, null);
//         */
//        //   Log.d(LOG_TAG, "In TestTable() sortorder: " + sortOrder);
//        String[] selectionArgs = {sortOrder};
//        MoviesDbHelper db = new MoviesDbHelper(mContext);
//
//        Cursor cursor = db.getReadableDatabase().rawQuery("SELECT " + Movies.COLUMN_SORT_ORDER + " FROM " + tableName, null);
//
//        //   Log.d(LOG_TAG, "In TestTable() Cursor count " + cursor.getCount());
//        String value;
//
//        cursor.moveToFirst();
//        if (cursor.getCount() > 0) {
//            value = cursor.getString(cursor.getColumnIndex(Movies.COLUMN_SORT_ORDER));
//            //   Log.d(LOG_TAG, "In TestTable() Value " + value);
//            cursor.moveToNext();
//        }
//        // Log.d(LOG_TAG, "Close cursor");
//        cursor.close();
//    }

}