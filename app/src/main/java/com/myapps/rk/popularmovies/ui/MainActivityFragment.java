package com.myapps.rk.popularmovies.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapps.rk.popularmovies.model.MovieData;
import com.myapps.rk.popularmovies.R;
import com.myapps.rk.popularmovies.adapter.MoviesAdapter;
import com.myapps.rk.popularmovies.asynctask.FetchMoviesTask;
import com.myapps.rk.popularmovies.service.MoviesService;
import com.myapps.rk.popularmovies.sync.MoviesSyncAdapter;
import com.myapps.rk.popularmovies.utils.Utility;
import com.myapps.rk.popularmovies.data.MoviesContract.*;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MoviesAdapter moviesAdapter;

    private static final int MOVIES_LOADER = 1;
    private static final String SELECTED_KEY = "selected_key";
    private ArrayList<MovieData> moviesList;

    int mPosition = ListView.INVALID_POSITION;
    boolean firstLoad = false;
    //ContentValues movieDetailValues;

    @Bind(R.id.movies_grid)
    GridView moviesGrid;
    @Bind(R.id.empty_movies_list_info)
    TextView emptyMoviesListInfo;
    @Bind(R.id.empty_movies_list_no_internet)
    TextView emptyMoviesNoInternet;

    private static final String[] MOVIES_COLUMNS = {
            Movies._ID,
            Movies.COLUMN_POSTER_PATH,
            Movies.COLUMN_OVERVIEW,
            Movies.COLUMN_RELEASE_DATE,
            Movies.COLUMN_MOVIE_ID,
            Movies.COLUMN_ORIGINAL_TITLE,
            Movies.COLUMN_VOTE_AVERAGE,
            Movies.COLUMN_SORT_ORDER
    };

    private static final String[] FAVOURITE_MOVIES_COLUMNS = {
            Movies._ID,
            Movies.COLUMN_POSTER_PATH,
            Movies.COLUMN_OVERVIEW,
            Movies.COLUMN_RELEASE_DATE,
            Movies.COLUMN_MOVIE_ID,
            Movies.COLUMN_ORIGINAL_TITLE,
            Movies.COLUMN_VOTE_AVERAGE,
    };

    public MainActivityFragment() {
    }

    //With AsyncTask
//    private void updateMoviesList() {
//        Log.d(LOG_TAG, "In updateMoviesList()");
//
//        FetchMoviesTask fetchMovies = new FetchMoviesTask(getContext());
//        String sortOrder = Utility.getPreferredSorting(getContext());
//        Log.d(LOG_TAG, "Selected sort order from settings is " + sortOrder);
//
//        if (sortOrder.equalsIgnoreCase(getContext().getResources().getString(R.string.pref_sort_most_popular))
//                || sortOrder.equalsIgnoreCase(getContext().getResources().getString(R.string.pref_sort_high_rated))) {
//            fetchMovies.execute(sortOrder);
//        } else if (sortOrder.equalsIgnoreCase(getContext().getResources().getString(R.string.pref_sort_favourite))) {
//            getActivity().getContentResolver().query(FavouriteMovies.buildFavouriteMoviesUri(),
//                    FAVOURITE_MOVIES_COLUMNS, null, null, null);
//        }
//    }

    //With Service
//    private void updateMoviesList() {
//        //  Log.d(LOG_TAG, "In updateMoviesList()");
//
//       // Intent intent = new Intent(getActivity(), MoviesService.class);
//        String sortOrder = Utility.getPreferredSorting(getContext());
//        Log.d(LOG_TAG, "Selected sort order from settings is " + sortOrder);
//
//        Intent alarmIntent = new Intent(getActivity(), MoviesService.AlarmReceiver.class);
//
//        if (sortOrder.equalsIgnoreCase(getContext().getResources().getString(R.string.pref_sort_most_popular))
//                || sortOrder.equalsIgnoreCase(getContext().getResources().getString(R.string.pref_sort_high_rated))) {
//           // intent.putExtra(MoviesService.MOVIE_FILTER_EXTRA, sortOrder);
//           // getActivity().startService(intent);
//
//            alarmIntent.putExtra(MoviesService.MOVIE_FILTER_EXTRA, sortOrder);
//            PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
//            AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);
//
//        } else if (sortOrder.equalsIgnoreCase(getContext().getResources().getString(R.string.pref_sort_favourite))) {
//            getActivity().getContentResolver().query(FavouriteMovies.buildFavouriteMoviesUri(),
//                    FAVOURITE_MOVIES_COLUMNS, null, null, null);
//        }
//    }

    //With Service
    private void updateMoviesList() {
        //Initialize movies sync adapter
        Log.d(LOG_TAG, "In updateMoviesList()");
        MoviesSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("moviesList", moviesList);

        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);

        if (firstLoad == true) {
            if (!Utility.isNetworkConnected(getContext())) {
                Snackbar.make(getView(), "No internet connection!", Snackbar.LENGTH_LONG).show();
                return;
            }

            ContentValues moviesValues = new ContentValues();
            moviesValues.put(FavouriteMovies._ID, "0");
            moviesValues.put(FavouriteMovies.COLUMN_POSTER_PATH, "0");
            moviesValues.put(FavouriteMovies.COLUMN_OVERVIEW, "0");
            moviesValues.put(FavouriteMovies.COLUMN_RELEASE_DATE, "0");
            moviesValues.put(FavouriteMovies.COLUMN_MOVIE_ID, "0");
            moviesValues.put(FavouriteMovies.COLUMN_ORIGINAL_TITLE, "0");
            moviesValues.put(FavouriteMovies.COLUMN_VOTE_AVERAGE, "0");

            getActivity().getContentResolver().insert(FavouriteMovies.buildFavouriteMoviesUri(), moviesValues);
            firstLoad = !firstLoad;
        }

        if (savedInstaceState == null || !savedInstaceState.containsKey("moviesList")) {
            moviesList = new ArrayList<>();
        } else {
            moviesList = savedInstaceState.getParcelableArrayList("moviesList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootview);

        updateMoviesList();
        moviesAdapter = new MoviesAdapter(getContext(), null, 0);
        View emptyView = emptyMoviesListInfo; //rootview.findViewById(R.id.empty_movies_list_info);
        moviesGrid.setEmptyView(emptyView);
        moviesGrid.setAdapter(moviesAdapter);

        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(cursor.getString(cursor.getColumnIndex(Movies.COLUMN_MOVIE_ID)));
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //  Log.d(LOG_TAG, "In onActivityCreated()");
        // initialize loader
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onFilterChanged() {
        //  Log.d(LOG_TAG, "Inside onFilterChanged()");
        updateMoviesList();
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
//        moviesAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Log.d(LOG_TAG, "In onOptionsItemSelected()");
        // int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Log.d(LOG_TAG, "In onStart()");
        //onFilterChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log.d(LOG_TAG, "In onResume()");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //  Log.d(LOG_TAG, "In onCreateLoader()");

        String sortingOrder = Utility.getPreferredSorting(getContext());
        Uri moviesUri = Movies.buildMoviesUri();
        Uri favouriteMoviesUri = FavouriteMovies.buildFavouriteMoviesUri();

        if (sortingOrder.equalsIgnoreCase(getResources().getString(R.string.pref_sort_favourite))) {
            return new CursorLoader(getActivity(),
                    favouriteMoviesUri,
                    FAVOURITE_MOVIES_COLUMNS,
                    null,
                    null,
                    null);
        }
        return new CursorLoader(getActivity(),
                moviesUri,
                MOVIES_COLUMNS,
                Movies.COLUMN_SORT_ORDER + "=?",
                new String[]{sortingOrder},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "In onLoadFinished()");

        if (cursor != null && cursor.getCount() > 0) {
            moviesAdapter.swapCursor(cursor);

            if (mPosition != ListView.INVALID_POSITION) {
                // If we don't need to restart the loader, and there's a desired position to restore
                // to, do so now.
                moviesGrid.smoothScrollToPosition(mPosition);
            }
        } else {
            Log.d(LOG_TAG, "Empty Cursor for movies data");
            boolean networkConnectivity = false;

            moviesAdapter.swapCursor(null);
            networkConnectivity = Utility.isNetworkConnected(getContext());
            if (networkConnectivity == false) {
                emptyMoviesNoInternet.setText(getResources().getString(R.string.empty_movies_no_internet));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //   Log.d(LOG_TAG, "In onLoaderReset()");
        moviesAdapter.swapCursor(null);
    }

    public interface Callback {
        void onItemSelected(String movieID);
    }
}