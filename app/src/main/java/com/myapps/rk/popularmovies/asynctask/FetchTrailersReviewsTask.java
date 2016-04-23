package com.myapps.rk.popularmovies.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import com.myapps.rk.popularmovies.BuildConfig;
import com.myapps.rk.popularmovies.R;
import com.myapps.rk.popularmovies.data.MoviesContract;
import com.myapps.rk.popularmovies.data.MoviesContract.Movies;
import com.myapps.rk.popularmovies.data.MoviesDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import com.myapps.rk.popularmovies.data.MoviesContract.*;

/**
 * Created by RKs on 4/13/2016.
 * <p/>
 * 1. trailers:
 * http://api.themoviedb.org/3/movie/209112/videos?api_key=
 * 2. reviews:
 * http://api.themoviedb.org/3/movie/209112/reviews?api_key=
 */

public class FetchTrailersReviewsTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchTrailersReviewsTask.class.getSimpleName();

    private final Context mContext;

    public FetchTrailersReviewsTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        Log.d(LOG_TAG, "FetchTrailersReviewsTask doInBackground()");

        if (params.length == 0) {
            return null;
        }

        //API KEY
        final String apiKey = BuildConfig.MOVIEDB_API_KEY;

        // Will contain the raw JSON response as a string.
        String trailersJsonResponse = null;
        String reviewsJsonResponse = null;

        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        String trailers_tag = "videos";
        String reviews_tag = "reviews";

        final String API_KEY = "api_key";
        String movieID = params[0];

        try {

            Uri buildTrailersUri = Uri.parse(BASE_URL).buildUpon()
                     .appendPath(movieID)
                    .appendPath(trailers_tag)
                    .appendQueryParameter(API_KEY, apiKey)
                    .build();
            Log.d(LOG_TAG, "buildTrailersUri " + buildTrailersUri);

            Uri buildReviewsUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(movieID)
                    .appendPath(reviews_tag)
                    .appendQueryParameter(API_KEY, apiKey)
                    .build();
            Log.d(LOG_TAG, "buildReviewsUri " + buildReviewsUri);

            URL trailersUrl = new URL(buildTrailersUri.toString());
            Log.d(LOG_TAG, "trailersUrl " + trailersUrl);
            URL reviewsUrl = new URL(buildReviewsUri.toString());
            Log.d(LOG_TAG, "reviewsUrl " + reviewsUrl);

            //Request/Response Trailers
            HttpRequestResponse hh = new HttpRequestResponse();
            Log.d(LOG_TAG, "http request URL " + trailersUrl);
            trailersJsonResponse = hh.doGetRequest(trailersUrl.toString());
            Log.d(LOG_TAG, "trailersJsonResponse: " + trailersJsonResponse);

            //Request/Response Reviews
            HttpRequestResponse hrr = new HttpRequestResponse();
            Log.d(LOG_TAG, "http request URL " + reviewsUrl);
            reviewsJsonResponse = hrr.doGetRequest(reviewsUrl.toString());
            Log.d(LOG_TAG, "reviewsJsonResponse: " + reviewsJsonResponse);

            //Get data from Json and insert it in table
            getTrailersDataFromJson(trailersJsonResponse, movieID);
            getReviewsDataFromJson(reviewsJsonResponse, movieID);

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

    protected void getTrailersDataFromJson(String trailersJsonResponse, String movieID) throws JSONException {
        Log.d(LOG_TAG, "Inside getTrailersDataFromJson");

        final String TRAILER_ID = "id";
        final String KEY = "key";
        final String NAME = "name";
        final String SITE = "site";
        final String SIZE = "size";

        final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

        JSONObject trailersOutput = new JSONObject(trailersJsonResponse);
        JSONArray trailersArray = trailersOutput.getJSONArray("results");
        Log.d(LOG_TAG, "trailersArray length " + trailersArray.length());

        Vector<ContentValues> cVVector = new Vector<ContentValues>(trailersArray.length());

        for (int i = 0; i < trailersArray.length(); i++) {
            String youttube_url = "";

            JSONObject data = trailersArray.getJSONObject(i);

            ContentValues trailerValues = new ContentValues();
            Log.d(LOG_TAG, "Inside getTrailersDataFromJson KEY " + data.getString(KEY));
            youttube_url = YOUTUBE_BASE_URL + data.getString(KEY);
            Log.d(LOG_TAG, "Inside getTrailersDataFromJson youttube_url " + youttube_url);

            trailerValues.put(Trailers.COLUMN_TRAILER_ID, data.getString(TRAILER_ID));
            trailerValues.put(Trailers.COLUMN_KEY, youttube_url);
            trailerValues.put(Trailers.COLUMN_NAME, data.getString(NAME));
            trailerValues.put(Trailers.COLUMN_SITE, data.getString(SITE));
            trailerValues.put(Trailers.COLUMN_SIZE, data.getString(SIZE));
            //Log.d(LOG_TAG, "Inside getTrailersDataFromJson " + movieID);
            trailerValues.put(Trailers.COLUMN_MOVIE_ID, movieID);

            cVVector.add(trailerValues);
        }

        int inserted = 0;
        // add to database
        if (cVVector.size() > 0) {
           mContext.getContentResolver().delete(Trailers.buildTrailersUriWithMovieId(movieID), null, null);

            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(Trailers.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "Trailers Task Complete. " + inserted + " Inserted");

        //TestTable(Movies.TABLE_NAME, sortOrder);
    }

    protected void getReviewsDataFromJson(String reviewsJsonResponse, String movieID) throws JSONException {
        Log.d(LOG_TAG, "getReviewsDataFromJson");

        final String REVIEW_ID = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";

        JSONObject reviewsOutput = new JSONObject(reviewsJsonResponse);
        JSONArray reviewsArray = reviewsOutput.getJSONArray("results");
        Log.d(LOG_TAG, "reviewsArray length " + reviewsArray.length());

        Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewsArray.length());

        for (int i = 0; i < reviewsArray.length(); i++) {

            JSONObject data = reviewsArray.getJSONObject(i);

            ContentValues reviewsValues = new ContentValues();

            reviewsValues.put(Reviews.COLUMN_REVIEW_ID, data.getString(REVIEW_ID));
            reviewsValues.put(Reviews.COLUMN_AUTHOR, data.getString(AUTHOR));
            reviewsValues.put(Reviews.COLUMN_CONTENT, data.getString(CONTENT));
            reviewsValues.put(Reviews.COLUMN_URL, data.getString(URL));
            reviewsValues.put(Reviews.COLUMN_MOVIE_ID, movieID);

            cVVector.add(reviewsValues);
        }

        int inserted = 0;
        // add to database
        if (cVVector.size() > 0) {
            mContext.getContentResolver().delete(Reviews.buildReviewsUriWithMovieId(movieID), null, null);

            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(Reviews.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "Reviews Task Complete. " + inserted + " Inserted");

        //TestTable(Movies.TABLE_NAME, sortOrder);
    }

    public void TestTable(String tableName, String sortOrder) {
//        /**
//         * String[] args = { "first string", "second@string.com" };
//         Cursor cursor = db.query("TABLE_NAME", null, "name=? AND email=?", args, null);
//         */
//        Log.d(LOG_TAG, "In TestTable() sortorder: " + sortOrder);
//        String[] selectionArgs = {sortOrder};
//        MoviesDbHelper db = new MoviesDbHelper(mContext);
//
//        Cursor cursor = db.getReadableDatabase().rawQuery("SELECT " + Movies.COLUMN_SORT_ORDER + " FROM " + tableName, null);
//
//        Log.d(LOG_TAG, "In TestTable() Cursor count " + cursor.getCount());
//        String value;
//
//        cursor.moveToFirst();
//        if (cursor.getCount() > 0) {
//            value = cursor.getString(cursor.getColumnIndex(Movies.COLUMN_SORT_ORDER));
//            Log.d(LOG_TAG, "In TestTable() Value " + value);
//            cursor.moveToNext();
//        }
//        Log.d(LOG_TAG, "Close cursor");
//        cursor.close();
    }
}