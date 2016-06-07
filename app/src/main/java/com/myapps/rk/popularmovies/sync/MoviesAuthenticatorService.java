package com.myapps.rk.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by RKs on 5/25/2016.
 */
public class MoviesAuthenticatorService extends Service {

    MoviesAuthenticator mMoviesAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mMoviesAuthenticator = new MoviesAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMoviesAuthenticator.getIBinder();
    }
}
