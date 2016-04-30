package com.myapps.rk.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Movie;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.myapps.rk.popularmovies.data.MoviesContract.*;

/**
 * Created by Riddhi Kakadia on 4/10/2016.
 */
public class MoviesProvider extends ContentProvider {

    private static final String LOG_TAG = MoviesProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = getUriMatcher();
    private MoviesDbHelper moviesDbHelper;

    private static final int MOVIES = 100;
    private static final int MOVIES_WITH_ID = 101;
    private static final int FAVOURITE_MOVIES = 200;
    private static final int FAVOURITE_MOVIES_WITH_ID = 201;
    private static final int TRAILERS = 300;
    private static final int TRAILERS_WITH_ID = 301;
    private static final int REVIEWS = 400;
    private static final int REVIEWS_WITH_ID = 401;


    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    private static UriMatcher getUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        //Movies
        matcher.addURI(authority, Movies.TABLE_NAME, MOVIES);
        matcher.addURI(authority, Movies.TABLE_NAME + "/*", MOVIES_WITH_ID);
        //Favourite movies
        matcher.addURI(authority, FavouriteMovies.TABLE_NAME, FAVOURITE_MOVIES);
        matcher.addURI(authority, FavouriteMovies.TABLE_NAME + "/*", FAVOURITE_MOVIES_WITH_ID);
        //Trailers movies
        matcher.addURI(authority, Trailers.TABLE_NAME, TRAILERS);
        matcher.addURI(authority, Trailers.TABLE_NAME + "/*", TRAILERS_WITH_ID);
        //Reviews movies
        matcher.addURI(authority, Reviews.TABLE_NAME, REVIEWS);
        matcher.addURI(authority, Reviews.TABLE_NAME + "/*", REVIEWS_WITH_ID);

        return matcher;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES: {
                return Movies.CONTENT_DIR_TYPE;
            }
            case MOVIES_WITH_ID: {
                return Movies.CONTENT_ITEM_TYPE;
            }
            case FAVOURITE_MOVIES: {
                return FavouriteMovies.CONTENT_DIR_TYPE;
            }
            case FAVOURITE_MOVIES_WITH_ID: {
                return FavouriteMovies.CONTENT_ITEM_TYPE;
            }
            case TRAILERS: {
                return Trailers.CONTENT_DIR_TYPE;
            }
            case TRAILERS_WITH_ID: {
                return Trailers.CONTENT_ITEM_TYPE;
            }
            case REVIEWS: {
                return Reviews.CONTENT_DIR_TYPE;
            }
            case REVIEWS_WITH_ID: {
                return Reviews.CONTENT_ITEM_TYPE;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {

            case MOVIES: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        Movies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIES_WITH_ID: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        Movies.TABLE_NAME,
                        projection,
                        FavouriteMovies._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVOURITE_MOVIES: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        FavouriteMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVOURITE_MOVIES_WITH_ID: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        FavouriteMovies.TABLE_NAME,
                        projection,
                        FavouriteMovies._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRAILERS: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        Trailers.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRAILERS_WITH_ID: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        Trailers.TABLE_NAME,
                        projection,
                        Trailers._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEWS: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        Reviews.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEWS_WITH_ID: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        Reviews.TABLE_NAME,
                        projection,
                        Reviews._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        if(contentValues == null){
            return null;
        }
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        Uri returnUri;
        long id = 0;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                id = db.insert(Movies.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = Movies.buildMoviesUri(id);
                } else {
                    throw new UnsupportedOperationException();
                }
                break;
            case FAVOURITE_MOVIES:
                id = db.insert(FavouriteMovies.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = FavouriteMovies.buildFavouriteMoviesUri(id);
                } else {
                    throw new UnsupportedOperationException();
                }
                break;
            case TRAILERS:
                id = db.insert(Trailers.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = Trailers.buildTrailersUri(id);
                } else {
                    throw new UnsupportedOperationException();
                }
                break;
            case REVIEWS:
                id = db.insert(Reviews.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = Reviews.buildReviewsUri(id);
                } else {
                    throw new UnsupportedOperationException();
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;

        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(Movies.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_WITH_ID:
                rowsDeleted = db.delete(Movies.TABLE_NAME, Movies._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case FAVOURITE_MOVIES:
                rowsDeleted = db.delete(FavouriteMovies.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVOURITE_MOVIES_WITH_ID:
                rowsDeleted = db.delete(FavouriteMovies.TABLE_NAME, FavouriteMovies._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case TRAILERS:
                rowsDeleted = db.delete(Trailers.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS_WITH_ID:
                rowsDeleted = db.delete(Trailers.TABLE_NAME, Trailers.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case REVIEWS:
                rowsDeleted = db.delete(Reviews.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS_WITH_ID:
                rowsDeleted = db.delete(Reviews.TABLE_NAME, Reviews.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Invalid Uri to delete " + match);
        }

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        int match = sUriMatcher.match(uri);

        if (contentValues == null) {
            throw new IllegalArgumentException("null ContentValues");
        }

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(
                        Movies.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case MOVIES_WITH_ID:
                rowsUpdated = db.update(
                        Movies.TABLE_NAME,
                        contentValues,
                        Movies._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case FAVOURITE_MOVIES:
                rowsUpdated = db.update(
                        FavouriteMovies.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case FAVOURITE_MOVIES_WITH_ID:
                rowsUpdated = db.update(
                        FavouriteMovies.TABLE_NAME,
                        contentValues,
                        FavouriteMovies._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case TRAILERS:
                rowsUpdated = db.update(
                        Trailers.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case TRAILERS_WITH_ID:
                rowsUpdated = db.update(
                        Trailers.TABLE_NAME,
                        contentValues,
                        Trailers._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case REVIEWS:
                rowsUpdated = db.update(
                        Reviews.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case REVIEWS_WITH_ID:
                rowsUpdated = db.update(
                        Reviews.TABLE_NAME,
                        contentValues,
                        Reviews._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
        }
        if(rowsUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {
            case MOVIES: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Movies.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case FAVOURITE_MOVIES: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FavouriteMovies.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case TRAILERS: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Trailers.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case REVIEWS: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Reviews.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            default:
                return super.bulkInsert(uri, values);
        }
        if(returnCount > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnCount;
    }
}
