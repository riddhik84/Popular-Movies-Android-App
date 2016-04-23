package com.myapps.rk.popularmovies.ui;

import android.content.Intent;
import android.graphics.Movie;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.myapps.rk.popularmovies.R;


public class MovieDetailActivity extends ActionBarActivity {

    public static String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    String movieId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            movieId = extras.getString(Intent.EXTRA_TEXT);
        }
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putString(Intent.EXTRA_TEXT, movieId);
            Log.d(LOG_TAG, "Bundle MovieID " + args.getString(Intent.EXTRA_TEXT));

            Log.d(LOG_TAG, "onCreate() MovieID " +movieId);
            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
