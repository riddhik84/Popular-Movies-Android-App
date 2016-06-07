package com.myapps.rk.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by RKs on 5/26/2016.
 */
public class MoviesSyncService extends Service {

    public static final String LOG_TAG = MoviesSyncService.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();
    private static MoviesSyncAdapter sMoviesSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "Inside onCreate MoviesSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMoviesSyncAdapter == null)
                Log.d(LOG_TAG, "Inside if sMoviesSyncAdapter == null");
                sMoviesSyncAdapter = new MoviesSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMoviesSyncAdapter.getSyncAdapterBinder();
    }
}
