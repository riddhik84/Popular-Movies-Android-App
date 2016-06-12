package com.myapps.rk.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.myapps.rk.popularmovies.BuildConfig;
import com.myapps.rk.popularmovies.R;
import com.myapps.rk.popularmovies.asynctask.HttpRequestResponse;
import com.myapps.rk.popularmovies.data.MoviesContract;
import com.myapps.rk.popularmovies.ui.MainActivity;
import com.myapps.rk.popularmovies.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URL;
import java.util.Vector;

/**
 * Created by RKs on 5/26/2016.
 */
public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    ContentResolver mContentResolver;

    private static final String[] NOTIFY_MOVIES_PROJECTION = new String[]{
            MoviesContract.Movies.COLUMN_MOVIE_ID,
            MoviesContract.Movies.COLUMN_ORIGINAL_TITLE,
            MoviesContract.Movies.COLUMN_VOTE_AVERAGE,
            MoviesContract.Movies.COLUMN_RELEASE_DATE,
    };
    // these indices must match the projection
    private static final int INDEX_COLUMN_MOVIE_ID = 0;
    private static final int INDEX_COLUMN_ORIGINAL_TITLE = 1;
    private static final int INDEX_COLUMN_VOTE_AVERAGE = 2;
    private static final int INDEX_COLUMN_RELEASE_DATE = 3;

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int MOVIES_NOTIFICATION_ID = 3004;

    //Annotations
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_OK, STATUS_SERVER_DOWN, STATUS_SERVER_INVALID, STATUS_UNKNOWN})
    public @interface ServerStatus {}

    public static final int STATUS_OK = 0;
    public static final int STATUS_SERVER_DOWN = 1;
    public static final int STATUS_SERVER_INVALID = 2;
    public static final int STATUS_UNKNOWN = 3;


    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient,
                              SyncResult syncResult) {
        Log.d(LOG_TAG, "In onPerformSync() - Starting Sync");

        String params = Utility.getPreferredSorting(getContext());
        Log.d(LOG_TAG, "current sorting param : " + params);

        if (params.length() == 0) {
            return;
        }

        //API KEY
        final String apiKey = BuildConfig.MOVIEDB_API_KEY;

        //Will contain the raw JSON response as a string.
        String moviesJsonResponse = null;

        final String MOVIES_BASE_URL = getContext().getString(R.string.movies_base_url);
        //final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        //final String MOVIES_BASE_URL = "http://google.com/";
        //final String MOVIES_BASE_URL = "http://google.com/ping?";
        final String QUERY_PARAM = "sort_by";
        final String API_KEY = "api_key";

        String queryParamValue = "popularity.desc";
        String sortOrder = params;

        try {
            if (params.equalsIgnoreCase(getContext().getResources().getString(R.string.pref_sort_favourite))) {
                return; //param[0] will not be favourite
            } else {
                //Fetch movies data
                if (params.equalsIgnoreCase(getContext().getResources().getString(R.string.pref_sort_most_popular))) {
                    queryParamValue = "popularity.desc";
                } else if (params.equals(getContext().getResources().getString(R.string.pref_sort_high_rated))) {
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

                if(moviesJsonResponse.length() == 0){
                    setServerStatus(getContext(), STATUS_SERVER_DOWN);
                } else {
                    //Get data from Json and insert it in table
                    setServerStatus(getContext(), STATUS_OK);
                    getMoviesFromJson(moviesJsonResponse, sortOrder);
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException ", e);
            setServerStatus(getContext(), STATUS_SERVER_DOWN);
            return;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Exception", e);
            setServerStatus(getContext(), STATUS_SERVER_INVALID);
            return;
        } finally {

        }
        return;
    }

    protected void getMoviesFromJson(String movieJsonString, String sortOrder) throws JSONException {
        //   Log.d(LOG_TAG, "getMoviesFromJson() sortOrder " + sortOrder);

        final String OWM_MESSAGE_CODE = "cod";

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

        if(moviesOutput.has(OWM_MESSAGE_CODE)){
            int error_404 = moviesOutput.getInt(OWM_MESSAGE_CODE);
            Log.e(LOG_TAG, "Movie data not recognized " + error_404);
            return;
        }
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
            inserted = getContext().getContentResolver().bulkInsert(MoviesContract.Movies.CONTENT_URI, cvArray);
        }

        //Notification
        notifyNewPopularMovie();

        //Log.d(LOG_TAG, "FetchMoviesTask Complete. " + inserted + " Inserted");
        //TestTable(Movies.TABLE_NAME, sortOrder);
    }

    public static void syncImmediately(Context context) {
        Log.d(LOG_TAG, "Inside syncImmediately()");

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.provider_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        Log.d(LOG_TAG, "Inside getSyncAccount()");
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {
            Log.d(LOG_TAG, "account password is null");
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        //onAccountCreated(newAccount, context);
        return newAccount;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Log.d(LOG_TAG, "Inside configurePeriodicSync()");

        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.d(LOG_TAG, "Inside onAccountCreated()");
        /*
         * Since we've created an account
         */
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        Log.d(LOG_TAG, "Inside initializeSyncAdapter()");
        getSyncAccount(context);
    }

    // Generate New movie notification
    private void notifyNewPopularMovie() {
        Context context = getContext();

        SharedPreferences prefss = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefss.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));
        Log.d(LOG_TAG, "displayNotifications: " +displayNotifications);
        if (displayNotifications) {

            //checking the last update and notify if it' the first of the day
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
                // Last sync was more than 1 day ago, let's send a notification with the new info.
                String sortingQuery = Utility.getPreferredSorting(context);

                Uri movieUri = MoviesContract.Movies.buildMoviesUri();

                // we'll query our contentProvider, as always
                Cursor cursor = context.getContentResolver().query(
                        movieUri,
                        NOTIFY_MOVIES_PROJECTION,
                        MoviesContract.Movies.COLUMN_SORT_ORDER + "=?",
                        new String[]{"mostpopular"},
                        null);

                if (cursor.moveToFirst()) {
                    Log.d(LOG_TAG, "cursor.moveToFirst() ");

                    int movieID = cursor.getInt(INDEX_COLUMN_MOVIE_ID);
                    String movieTitle = cursor.getString(INDEX_COLUMN_ORIGINAL_TITLE);
                    String movieVotes = cursor.getString(INDEX_COLUMN_VOTE_AVERAGE);
                    String movieReleaseDt = cursor.getString(INDEX_COLUMN_RELEASE_DATE);

                    String notificationTitle = context.getString(R.string.app_name);

                    // Define the text of the movie.
                    String contentText = String.format(context.getString(R.string.format_notification),
                            movieTitle,
                            movieVotes,
                            movieReleaseDt);

                    //build your notification here.
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setSmallIcon(R.drawable.movie_notification)
                                    .setContentTitle(notificationTitle)
                                    .setContentText(contentText);
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(getContext(), MainActivity.class);
                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(MainActivity.class);
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(MOVIES_NOTIFICATION_ID, mBuilder.build());

                    //refreshing last sync
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.commit();
                } else {
                    Log.d(LOG_TAG, "NOT cursor.moveToFirst() ");
                }
            }

        }
    }

    static private void setServerStatus(Context c, @ServerStatus int serverStat){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_server_status_key), serverStat);
        spe.commit();
    }
}
