package com.myapps.rk.popularmovies.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.myapps.rk.popularmovies.R;
import com.myapps.rk.popularmovies.utils.Utility;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback {

    public final String LOG_TAG = MainActivity.class.getSimpleName();

    private final String DETAIL_FRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;
    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSortOrder = Utility.getPreferredLocation(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_details_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new MovieDetailActivityFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //Show settings screen
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortOrder = Utility.getPreferredLocation(this);

        // update the movie details in our second pane using the fragment manager
        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            MainActivityFragment ff = (MainActivityFragment) getSupportFragmentManager().
                    findFragmentById(R.id.fragment_main);
            if (null != ff) {
                ff.onFilterChanged();
            }
            mSortOrder = sortOrder;
        }
    }

    @Override
    public void onItemSelected(String movieId) {
        //  Log.d(LOG_TAG, "In onItemSelected() mTwoPane: " + mTwoPane);

        if (Utility.isNetworkConnected(this) == true) {

            if (mTwoPane) {
                Bundle args = new Bundle();
                args.putString(Intent.EXTRA_TEXT, movieId);

                MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
                fragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, fragment, DETAIL_FRAGMENT_TAG)
                        .commit();
            } else {
                Log.d(LOG_TAG, "In onItemSelected() mTwoPane: " + mTwoPane);

                Intent intent = new Intent(this, MovieDetailActivity.class)
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_TEXT, movieId);
                startActivity(intent);
            }
        } else {
            Snackbar.make(getCurrentFocus(), "No internet connection!", Snackbar.LENGTH_SHORT).show();
        }
    }
}
