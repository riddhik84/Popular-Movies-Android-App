package com.myapps.rk.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

   @Bind(R.id.movies_grid)
   GridView moviesGrid;

    PopularMoviesAdapter moviesAdapter;

  /*  PopularMovies[] dummyData = {
            new PopularMovies("Baby", R.drawable.baby),
            new PopularMovies("Badlapur", R.drawable.badlapur),
            new PopularMovies("Bajrangi Bhaijaan", R.drawable.bajrangi_bhaijaan_poster),
            new PopularMovies("Dilwale", R.drawable.dilwale),
            new PopularMovies("Drishyam", R.drawable.drishyam),
            new PopularMovies("Massan", R.drawable.massan),
            new PopularMovies("NH10", R.drawable.nh10),
            new PopularMovies("Piku", R.drawable.piku),
            new PopularMovies("Roy", R.drawable.roy),
            new PopularMovies("Talvar", R.drawable.talvar),
            new PopularMovies("Tamasha", R.drawable.tamasha)
    }; */

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootview = inflater.inflate(R.layout.fragment_main, container, false);
       ButterKnife.bind(this, rootview);

       //List<PopularMovies> dummyList = new ArrayList<PopularMovies>(Arrays.asList(dummyData));
       //Log.d("", "List size....... ****" +dummyList.size());
       //moviesAdapter = new PopularMoviesAdapter(getActivity(), dummyList);

        moviesAdapter = new PopularMoviesAdapter(getActivity(), new ArrayList<PopularMovies>());
        moviesGrid.setAdapter(moviesAdapter);

        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra("MovieThumbnail", moviesAdapter.getItem(i).moviePoster);
                intent.putExtra("MovieName", moviesAdapter.getItem(i).movieName);
                intent.putExtra("MovieReleaseDate", moviesAdapter.getItem(i).movieReleaseDate);
                intent.putExtra("MovieRatings", moviesAdapter.getItem(i).movieRating);
                intent.putExtra("MovieOverview", moviesAdapter.getItem(i).movieOverview);
                startActivity(intent);
            }
        });

        return rootview;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void updateMoviesList() {
        FetchMoviesTask fetchMovies = new FetchMoviesTask();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        //Log.d(LOG_TAG, "Selected sort order from settings is...***********" +sortOrder);
        fetchMovies.execute(sortOrder);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.d(LOG_TAG, "******* Inside onStart()");
        updateMoviesList();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, PopularMovies[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        public FetchMoviesTask() {
            super();
        }

        /*
        sort by popularity:
        http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=
        sort by highly rated:
        http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key
        */

        @Override
        protected PopularMovies[] doInBackground(String... params) {

            PopularMovies[] moviesData = null;
            //API KEY
            final String apiKey = BuildConfig.MOVIEDB_API_KEY;

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonString = null;

            final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String QUERY_PARAM = "sort_by";
            final String API_KEY = "api_key";

            String queryParamValue = "popularity.desc";

            try {

                if(params[0].equals("mostpopular"))
                {
                    queryParamValue = "popularity.desc";
                } else if (params[0].equals("highlyrated"))
                {
                    queryParamValue = "vote_average.desc";
                }

                Uri buildUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, queryParamValue)
                        .appendQueryParameter(API_KEY, apiKey)
                        .build();

                URL url = new URL(buildUri.toString());
                //Log.d(LOG_TAG, "******* Build URL = " + buildUri.toString());

                // Create the request to MoviesDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonString = buffer.toString();
                //Log.d(LOG_TAG, "****** Movies JSON string = " + moviesJsonString);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movies data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMoviesFromJson(moviesJsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(PopularMovies[] result) {
            if (result != null) {
                moviesAdapter.clear();
                for (PopularMovies movie : result) {
                   moviesAdapter.add(movie);
                }
            }
        }

        protected PopularMovies[] getMoviesFromJson(String movieJsonString) throws JSONException
        {
            PopularMovies[] popularMoviesDb;
            final String posterBaseURL = "http://image.tmdb.org/t/p/";
            final String posterImageType="w185";

            JSONObject moviesOutput = new JSONObject(movieJsonString);
            JSONArray results = moviesOutput.getJSONArray("results");
            //Log.d(LOG_TAG, "****** JSON result length.. " +results.length());

            popularMoviesDb = new PopularMovies[results.length()];

            for(int i = 0; i < results.length(); i++)
            {
                JSONObject movieData = results.getJSONObject(i);
                //Log.d(LOG_TAG, "Fetched movie name......******* " +movieData.getString("original_title"));
                //Log.d(LOG_TAG, "Fetched movie Poster path......******* " +movieData.getString("poster_path"));
                //Log.d(LOG_TAG, "Fetched movie overview......******* " +movieData.getString("overview"));
                //Log.d(LOG_TAG, "Fetched movie overview......******* " +movieData.getString("vote_average"));
                //Log.d(LOG_TAG, "Fetched movie overview......******* " +movieData.getString("release_date"));

                popularMoviesDb[i] = new PopularMovies((movieData.getString("original_title")),
                        posterBaseURL + posterImageType + movieData.getString("poster_path"),
                        movieData.getString("overview"),
                        movieData.getString("vote_average"),
                        movieData.getString("release_date"));
            }

            //Log.d(LOG_TAG, "****** tempPopularMoviesDb length.. " +popularMoviesDb.length);
            return popularMoviesDb;
        }
    }
}