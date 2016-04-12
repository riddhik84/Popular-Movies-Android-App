package com.myapps.rk.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.myapps.rk.popularmovies.data.MoviesContract.*;

/**
 * Created by RKs on 4/10/2016.
 */
public class MoviesProvider extends ContentProvider {

    private static final String LOG_TAG = MoviesProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = getUriMatcher();
    private MoviesDbHelper moviesDbHelper;

    private static final int POPULAR_MOVIES = 100;
    private static final int POPULAR_MOVIES_WITH_ID = 101;
    private static final int HIGHLY_RATED_MOVIES = 200;
    private static final int HIGHLY_RATED_MOVIES_WITH_ID = 201;
    private static final int FAVOURITE_MOVIES = 300;
    private static final int FAVOURITE_MOVIES_MOVIES_WITH_ID = 301;

    @Override
    public boolean onCreate() {

        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    private static UriMatcher getUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        //popular movies
        matcher.addURI(authority, PopularMovies.TABLE_NAME, POPULAR_MOVIES);
        matcher.addURI(authority, PopularMovies.TABLE_NAME + "/#", POPULAR_MOVIES_WITH_ID);
        //highly rated movies
        matcher.addURI(authority, HighlyRatedMovies.TABLE_NAME, HIGHLY_RATED_MOVIES);
        matcher.addURI(authority, HighlyRatedMovies.TABLE_NAME + "/#", HIGHLY_RATED_MOVIES_WITH_ID);
        //Favourite movies
        matcher.addURI(authority, FavouriteMovies.TABLE_NAME, FAVOURITE_MOVIES);
        matcher.addURI(authority, FavouriteMovies.TABLE_NAME + "/#", FAVOURITE_MOVIES_MOVIES_WITH_ID);

        return matcher;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch(match){
            case POPULAR_MOVIES: {
                return PopularMovies.CONTENT_DIR_TYPE;
            }
            case POPULAR_MOVIES_WITH_ID: {
                return PopularMovies.CONTENT_ITEM_TYPE;
            }
            case HIGHLY_RATED_MOVIES: {
                return HighlyRatedMovies.CONTENT_DIR_TYPE;
            }
            case HIGHLY_RATED_MOVIES_WITH_ID: {
                return HighlyRatedMovies.CONTENT_ITEM_TYPE;
            }
            case FAVOURITE_MOVIES: {
                return FavouriteMovies.CONTENT_DIR_TYPE;
            }
            case FAVOURITE_MOVIES_MOVIES_WITH_ID:{
                return FavouriteMovies.CONTENT_ITEM_TYPE;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri:" +uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch(sUriMatcher.match(uri)){
            case POPULAR_MOVIES:{
               cursor=  moviesDbHelper.getReadableDatabase().query(
                        PopularMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return cursor;
            }
            case POPULAR_MOVIES_WITH_ID:{
               cursor = moviesDbHelper.getReadableDatabase().query(
                        PopularMovies.TABLE_NAME,
                        projection,
                        PopularMovies._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                return cursor;
            }
            case HIGHLY_RATED_MOVIES:{
                cursor=  moviesDbHelper.getReadableDatabase().query(
                        HighlyRatedMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return cursor;
            }
            case HIGHLY_RATED_MOVIES_WITH_ID:{
                cursor = moviesDbHelper.getReadableDatabase().query(
                        HighlyRatedMovies.TABLE_NAME,
                        projection,
                        HighlyRatedMovies._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                return cursor;
            }
            case FAVOURITE_MOVIES:{
                cursor=  moviesDbHelper.getReadableDatabase().query(
                        FavouriteMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                return cursor;
            }
            case FAVOURITE_MOVIES_MOVIES_WITH_ID:{
                cursor = moviesDbHelper.getReadableDatabase().query(
                        FavouriteMovies.TABLE_NAME,
                        projection,
                        FavouriteMovies._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                return cursor;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
