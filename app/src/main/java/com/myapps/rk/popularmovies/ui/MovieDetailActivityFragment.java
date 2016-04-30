package com.myapps.rk.popularmovies.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapps.rk.popularmovies.R;
import com.myapps.rk.popularmovies.adapter.ReviewsAdapter;
import com.myapps.rk.popularmovies.adapter.TrailersAdapter;
import com.myapps.rk.popularmovies.asynctask.FetchTrailersReviewsTask;
import com.myapps.rk.popularmovies.data.MoviesContract.*;
import com.myapps.rk.popularmovies.utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private static final int MOVIE_DETAIL_LOADER = 0;
    private static final int MOVIE_TRAILERS_LOADER = 1;
    private static final int MOVIE_REVIEWS_LOADER = 2;
    private static final int FAVOURITE_MOVIE_LOADER = 3;

    boolean favorited = false;
    String mSortOrder;

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

    private static final String[] TRAILERS_COLUMNS = {
            Trailers._ID,
            Trailers.COLUMN_TRAILER_ID,
            Trailers.COLUMN_KEY,
            Trailers.COLUMN_NAME,
            Trailers.COLUMN_SIZE,
            Trailers.COLUMN_SIZE,
            Trailers.COLUMN_MOVIE_ID
    };

    private static final String[] REVIEWS_COLUMNS = {
            Reviews._ID,
            Reviews.COLUMN_REVIEW_ID,
            Reviews.COLUMN_AUTHOR,
            Reviews.COLUMN_CONTENT,
            Reviews.COLUMN_URL,
            Reviews.COLUMN_MOVIE_ID
    };

    @Bind(R.id.movie_name_header)
    TextView movie_name_header;
    @Bind(R.id.movie_image)
    ImageView movie_image_thumbnail;
    @Bind(R.id.movie_ratings)
    TextView movie_ratings;
    @Bind(R.id.movie_overview)
    TextView movie_overview;
    @Bind(R.id.movie_release_date)
    TextView movie_release_date;
    @Bind(R.id.reviews_header)
    TextView movie_reviews_header;
    @Bind(R.id.trailers_header)
    TextView movie_trailers_header;

    ImageButton favButton;
    ListView trailersListView;
    TrailersAdapter mTrailersAdapter;
    ListView reviewsListView;
    ReviewsAdapter mReviewsAdapter;

    ContentValues movieDetailValues;
    String movieId;

    public MovieDetailActivityFragment() {
        setHasOptionsMenu(true);
        movieDetailValues = new ContentValues();
    }

    private void updateTrailersReviewsList() {
        FetchTrailersReviewsTask fetchTask = new FetchTrailersReviewsTask(getActivity());
        fetchTask.execute(movieId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);

        View rootview = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootview);

        mSortOrder = Utility.getPreferredLocation(getContext());

        Bundle args = getArguments();
        if (args != null) {
            movieId = args.getString(Intent.EXTRA_TEXT);
            Log.d(LOG_TAG, "getArguments() MovieID " + movieId);
        }

        trailersListView = (ListView) rootview.findViewById(R.id.trailers_list_view);
        mTrailersAdapter = new TrailersAdapter(getContext(), null, 0, MOVIE_TRAILERS_LOADER);
        trailersListView.setAdapter(mTrailersAdapter);

        reviewsListView = (ListView) rootview.findViewById(R.id.reviews_list_view);
        mReviewsAdapter = new ReviewsAdapter(getContext(), null, 0, MOVIE_REVIEWS_LOADER);
        reviewsListView.setAdapter(mReviewsAdapter);

        if (Utility.isNetworkConnected(getActivity())) {
            if (movieId != null) {
                //    Log.d(LOG_TAG, "isNetworkConnected() MovieID: " + movieId);
                updateTrailersReviewsList();
            } else {
                //   Log.e(LOG_TAG, "movieId is null");
            }
        } else {
            Snackbar.make(getView(), "No internet connection!", Snackbar.LENGTH_LONG).show();
        }

        favButton = (ImageButton) rootview.findViewById(R.id.mark_fav);
        return rootview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(MOVIE_TRAILERS_LOADER, null, this);
        getLoaderManager().initLoader(MOVIE_REVIEWS_LOADER, null, this);
        getLoaderManager().initLoader(FAVOURITE_MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onFilterChanged() {
        //    Log.d(LOG_TAG, "In DetailF onFilterChanged()");
        String sortOrder = Utility.getPreferredLocation(getContext());
        if (sortOrder != mSortOrder) {
            updateTrailersReviewsList();
            getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);
            getLoaderManager().restartLoader(MOVIE_TRAILERS_LOADER, null, this);
            getLoaderManager().restartLoader(MOVIE_REVIEWS_LOADER, null, this);
            getLoaderManager().restartLoader(FAVOURITE_MOVIE_LOADER, null, this);
            //moviesAdapter.notifyDataSetChanged();
        } else {
            //    Log.d(LOG_TAG, "No change in filter");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // onFilterChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        //   Log.d(LOG_TAG, "In DetailF onCreateLoader()");

        if (movieId != null) {

            String sortingOrder = Utility.getPreferredLocation(getActivity());

            //Uri moviesUri = Movies.buildMoviesUriWithMovieId(movieId);
            Uri moviesUri = Movies.buildMoviesUri();
            Uri trailersUri = Trailers.buildTrailersUri();
            Uri reviewsUri = Reviews.buildReviewsUri();
            Uri favouriteMoviesUri = FavouriteMovies.buildFavouriteMoviesUri();

//            Log.d(LOG_TAG, "oncreateLoader(): " + moviesUri);
//            Log.d(LOG_TAG, "oncreateLoader(): " + trailersUri);
//            Log.d(LOG_TAG, "oncreateLoader(): " + reviewsUri);
//            Log.d(LOG_TAG, "oncreateLoader(): " + favouriteMoviesUri);

            switch (loaderID) {
                case MOVIE_DETAIL_LOADER: {
                    if (sortingOrder.equalsIgnoreCase(getResources().getString(R.string.pref_sort_favourite))) {
                        return new CursorLoader(getActivity(),
                                favouriteMoviesUri,
                                FAVOURITE_MOVIES_COLUMNS,
                                FavouriteMovies.COLUMN_MOVIE_ID + " = ?",
                                new String[]{movieId},
                                null);
                    }
                    //   Log.d(LOG_TAG, "Inside onCreateLoader() MOVIE_DETAIL_LOADER " + movieId);
                    return new CursorLoader(getActivity(),
                            moviesUri,
                            MOVIES_COLUMNS,
                            Movies.COLUMN_MOVIE_ID + "=?",
                            new String[]{movieId},
                            null);

                }
                case MOVIE_TRAILERS_LOADER: {
                    //   Log.d(LOG_TAG, "Inside onCreateLoader() MOVIE_TRAILERS_LOADER " + movieId);

                    return new CursorLoader(getActivity(),
                            trailersUri,
                            TRAILERS_COLUMNS,
                            Trailers.COLUMN_MOVIE_ID + "=?",
                            new String[]{movieId},
                            null);
                }
                case MOVIE_REVIEWS_LOADER: {
                    //   Log.d(LOG_TAG, "Inside onCreateLoader() MOVIE_REVIEWS_LOADER " + movieId);

                    return new CursorLoader(getActivity(),
                            reviewsUri,
                            REVIEWS_COLUMNS,
                            Reviews.COLUMN_MOVIE_ID + "=?",
                            new String[]{movieId},
                            null);
                }
                case FAVOURITE_MOVIE_LOADER: {
                    //   Log.d(LOG_TAG, "Inside onCreateLoader() FAVOURITE_MOVIE_LOADER " + movieId);

                    return new CursorLoader(getActivity(),
                            favouriteMoviesUri,
                            FAVOURITE_MOVIES_COLUMNS,
                            FavouriteMovies.COLUMN_MOVIE_ID + "=?",
                            new String[]{movieId},
                            null);
                }
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        //   Log.d(LOG_TAG, "Fragment onLoadFinished() ");

        String sortOrder = Utility.getPreferredLocation(getActivity());
        String movieName = "";
        String movieImage = "";
        String movieReleaseDate = "";
        String movieRatings = "";
        String movieOverview = "";

        switch (loader.getId()) {
            case MOVIE_DETAIL_LOADER: {
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    //Log.d(LOG_TAG, "In movie detail loader " + movieName);
                    movieName = cursor.getString(cursor.getColumnIndex(Movies.COLUMN_ORIGINAL_TITLE));
                    movieImage = cursor.getString(cursor.getColumnIndex(Movies.COLUMN_POSTER_PATH));
                    movieReleaseDate = cursor.getString(cursor.getColumnIndex(Movies.COLUMN_RELEASE_DATE));
                    movieRatings = cursor.getString(cursor.getColumnIndex(Movies.COLUMN_VOTE_AVERAGE));
                    movieOverview = cursor.getString(cursor.getColumnIndex(Movies.COLUMN_OVERVIEW));

//                    Log.d(LOG_TAG, "In movie detail loader " + movieName);
//                    Log.d(LOG_TAG, "In movie detail loader " + movieImage);

                    movie_name_header.setText(movieName);
                    Picasso.with(getContext()).
                            load(movieImage).
                            placeholder(R.drawable.placeholder).
                            error(R.drawable.error).
                            noFade().
                            //fit().
                                    resize(650, 650).
                            //centerCrop().
                                    into(movie_image_thumbnail);
                    movie_image_thumbnail.setContentDescription("Thumbnail of " +movieName);

                    movie_release_date.setText("Release Date: " + movieReleaseDate);
                    movie_ratings.setText("Movie Rating: " + movieRatings + "/10");
                    movie_overview.setText("Overview: \n" + movieOverview);
                }
                break;
            }
            case MOVIE_TRAILERS_LOADER: {
                //   Log.d(LOG_TAG, "Inside onLoadFinished MOVIE_TRAILERS_LOADER " + movieId);
                if (cursor != null && cursor.getCount() > 0) {
                    //  Log.d(LOG_TAG, "Inside onLoadFinished MOVIE_TRAILERS_LOADER cursor > 0 " + movieId);
                    //  Log.d(LOG_TAG, "Inside onLoadFinished MOVIE_TRAILERS_LOADER trailers count " + cursor.getCount());

                    cursor.moveToFirst();
                    do {
                        movie_trailers_header.setVisibility(View.VISIBLE);
                        mTrailersAdapter.swapCursor(cursor);
                        //       Log.d(LOG_TAG, "URL: " + cursor.getString(cursor.getColumnIndex(Trailers.COLUMN_KEY)));
                    }
                    while (cursor.moveToNext());

                    trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            String url = mTrailersAdapter.getCursor().getString(mTrailersAdapter.getCursor().getColumnIndex(Trailers.COLUMN_KEY));
                            //       Log.d(LOG_TAG, "URL at clicked position: " + url);

                            //Uri uri = Uri.parse("http://www.youtube.com");
                            Uri uri = Uri.parse(url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                }
                break;
            }
            case MOVIE_REVIEWS_LOADER: {
                //   Log.d(LOG_TAG, "Inside onLoadFinished MOVIE_TRAILERS_LOADER " + movieId);
                if (cursor != null && cursor.getCount() > 0) {
                    //   Log.d(LOG_TAG, "Inside onLoadFinished MOVIE_REVIEWS_LOADER cursor > 0 " + movieId);
                    //  Log.d(LOG_TAG, "Inside onLoadFinished MOVIE_REVIEWS_LOADER cursor.getCount() " + cursor.getCount());

                    cursor.moveToFirst();
                    do {
                        movie_reviews_header.setVisibility(View.VISIBLE);
                        mReviewsAdapter.swapCursor(cursor);
                        //    Log.d(LOG_TAG, "Review: " + cursor.getString(cursor.getColumnIndex(Reviews.COLUMN_CONTENT)));
                    }
                    while (cursor.moveToNext());
                }
                break;
            }
            case FAVOURITE_MOVIE_LOADER: {

                //   Log.d(LOG_TAG, "Inside onLoadFinished FAVOURITE_MOVIE_LOADER " + movieId);
                if (cursor.getCount() > 0 && cursor != null) {
                    favButton.setImageDrawable(getResources().getDrawable(R.drawable.fav_selected));
                    favorited = true;
                } else {
                    favButton.setImageDrawable(getResources().getDrawable(R.drawable.fav_not_selected));
                    favorited = false;
                }

                favButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (favorited == true) {
                            favorited = false;
                            //    Log.d(LOG_TAG, "Remove from favourite");
                            int i = getActivity().getContentResolver().delete(FavouriteMovies.buildFavouriteMoviesUri(),
                                    FavouriteMovies.COLUMN_MOVIE_ID + "=?",
                                    new String[]{movieId}
                            );
                            if (i > 0) {
                                Toast.makeText(getContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
                            }

                        } else if (favorited == false) {
                            favorited = true;
                            ContentValues values = new ContentValues();
                            Cursor cursorFav = getActivity().getContentResolver().query(Movies.buildMoviesUri(), FAVOURITE_MOVIES_COLUMNS,
                                    Movies.COLUMN_MOVIE_ID + "=?", new String[]{movieId}, null);
                            if (cursorFav.getCount() > 0) {
                                cursorFav.moveToFirst();
                                values.put(FavouriteMovies.COLUMN_POSTER_PATH, cursorFav.getString(cursorFav.getColumnIndex(Movies.COLUMN_POSTER_PATH)));
                                values.put(FavouriteMovies.COLUMN_OVERVIEW, cursorFav.getString(cursorFav.getColumnIndex(Movies.COLUMN_OVERVIEW)));
                                values.put(FavouriteMovies.COLUMN_RELEASE_DATE, cursorFav.getString(cursorFav.getColumnIndex(Movies.COLUMN_RELEASE_DATE)));
                                values.put(FavouriteMovies.COLUMN_MOVIE_ID, cursorFav.getString(cursorFav.getColumnIndex(Movies.COLUMN_MOVIE_ID)));
                                values.put(FavouriteMovies.COLUMN_ORIGINAL_TITLE, cursorFav.getString(cursorFav.getColumnIndex(Movies.COLUMN_ORIGINAL_TITLE)));
                                values.put(FavouriteMovies.COLUMN_VOTE_AVERAGE, cursorFav.getString(cursorFav.getColumnIndex(Movies.COLUMN_VOTE_AVERAGE)));

                                getActivity().getContentResolver().insert(FavouriteMovies.buildFavouriteMoviesUri(), values);

                                Toast.makeText(getContext(), "Added to Favourites", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case MOVIE_DETAIL_LOADER: {

            }
            case MOVIE_TRAILERS_LOADER: {
                mTrailersAdapter.swapCursor(null);
            }
            case MOVIE_REVIEWS_LOADER: {
                mReviewsAdapter.swapCursor(null);
            }
            case FAVOURITE_MOVIE_LOADER: {

            }
        }
    }
}
