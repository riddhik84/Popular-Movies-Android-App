package com.myapps.rk.popularmovies.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.myapps.rk.popularmovies.BuildConfig;
import com.myapps.rk.popularmovies.R;
import com.myapps.rk.popularmovies.asynctask.HttpRequestResponse;
import com.myapps.rk.popularmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

/**
 * Created by RKs on 5/18/2016.
 */
public class MoviesService extends IntentService {
    final String LOG_TAG = MoviesService.class.getSimpleName();
    public static final String MOVIE_FILTER_EXTRA = "mfe";

    public MoviesService() {
        super("MoviesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String params = intent.getStringExtra(MOVIE_FILTER_EXTRA);
        Log.d(LOG_TAG, "MOVIE_FILTER_EXTRA: " +params);

        if (params.length() == 0) {
            return;
        }

        //API KEY
        final String apiKey = BuildConfig.MOVIEDB_API_KEY;

        //Will contain the raw JSON response as a string.
        String moviesJsonResponse = null;

        final String MOVIES_BASE_URL = this.getString(R.string.movies_base_url);
        final String QUERY_PARAM = "sort_by";
        final String API_KEY = "api_key";

        String queryParamValue = "popularity.desc";
        String sortOrder = params;

        try {
            if (params.equalsIgnoreCase(this.getResources().getString(R.string.pref_sort_favourite))) {
                return; //param[0] will not be favourite
            } else {
                //Fetch movies data
                if (params.equalsIgnoreCase(this.getResources().getString(R.string.pref_sort_most_popular))) {
                    queryParamValue = "popularity.desc";
                } else if (params.equals(this.getResources().getString(R.string.pref_sort_high_rated))) {
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
            return;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Exception", e);
            return;
        } finally {

        }
        return;
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
            movieValues.put(MoviesContract.Movies.COLUMN_POSTER_PATH, fullPosterPath);
            movieValues.put(MoviesContract.Movies.COLUMN_OVERVIEW, movieData.getString(OVERVIEW));
            movieValues.put(MoviesContract.Movies.COLUMN_RELEASE_DATE, movieData.getString(RELEASE_DATE));
            movieValues.put(MoviesContract.Movies.COLUMN_MOVIE_ID, movieData.getString(MOVIE_ID));
            movieValues.put(MoviesContract.Movies.COLUMN_ORIGINAL_TITLE, movieData.getString(ORIGINAL_TITLE));
            movieValues.put(MoviesContract.Movies.COLUMN_VOTE_AVERAGE, movieData.getString(VOTE_AVERAGE));
            movieValues.put(MoviesContract.Movies.COLUMN_SORT_ORDER, sortOrder); //sort order to filter movies

            cVVector.add(movieValues);
        }

        int inserted = 0;
        //add values to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = this.getContentResolver().bulkInsert(MoviesContract.Movies.CONTENT_URI, cvArray);
        }
        //Log.d(LOG_TAG, "FetchMoviesTask Complete. " + inserted + " Inserted");

        //TestTable(Movies.TABLE_NAME, sortOrder);
    }

    public static class AlarmReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, MoviesService.class);
            sendIntent.putExtra(MoviesService.MOVIE_FILTER_EXTRA, intent.getStringExtra(MoviesService.MOVIE_FILTER_EXTRA));
            context.startService(sendIntent);
        }
    }
}
