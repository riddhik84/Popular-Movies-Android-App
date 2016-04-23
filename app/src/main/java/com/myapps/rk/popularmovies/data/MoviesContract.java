/**
 * Created by RKs on 4/10/2016.
 */

package com.myapps.rk.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;


public class MoviesContract {
    public final String LOG_TAG = MoviesContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.myapps.rk.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_FAVOURITE_MOVIES = "favouritemovies";

    public static class Movies implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String _ID = "_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_SORT_ORDER = "sort_order";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static Uri buildMoviesUri (long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMoviesUriWithMovieId(String movieID) {
            return CONTENT_URI.buildUpon().appendPath(movieID).build();
        }

        public static Uri buildMoviesUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static String getIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static class FavouriteMovies implements BaseColumns {

        public static final String TABLE_NAME = "favouritemovies";

        public static final String _ID = "_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE_MOVIES).build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE_MOVIES;

        public static Uri buildFavouriteMoviesUri (long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFavouriteMoviesUriWithMovieId(String movieID) {
            return CONTENT_URI.buildUpon().appendPath(movieID).build();
        }

        public static Uri buildFavouriteMoviesUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static String getIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static class Trailers implements BaseColumns {

        public static final String TABLE_NAME = "trailers";

        public static final String _ID = "_id";
        public static final String COLUMN_TRAILER_ID = "trailer_id";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_MOVIE_ID = "id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        public static Uri buildTrailersUri (long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrailersUriWithMovieId(String movieID) {
            Log.d("LOG_TAG", "buildTrailersUriWithMovieId() movieID " + movieID);
            Log.d("LOG_TAG", "buildTrailersUriWithMovieId() Uri " + CONTENT_URI.buildUpon().appendPath(movieID).build());

            return CONTENT_URI.buildUpon().appendPath(movieID).build();
        }

        public static Uri buildTrailersUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static String getIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class Reviews implements BaseColumns {

        public static final String TABLE_NAME = "reviews";

        public static final String _ID = "_id";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_MOVIE_ID = "id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static Uri buildReviewsUri (long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewsUriWithMovieId(String movieID) {
            return CONTENT_URI.buildUpon().appendPath(movieID).build();
        }

        public static Uri buildReviewsUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static String getIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
