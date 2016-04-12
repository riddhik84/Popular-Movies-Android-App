package com.myapps.rk.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.myapps.rk.popularmovies.data.MoviesContract.*;

/**
 * Created by RKs on 4/10/2016.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    public final String LOG_TAG = MoviesDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Create PopularMovies table
        final String SQL_CREATE_TABLE_POPULAR_MOVIES = "CREATE TABLE " + PopularMovies.TABLE_NAME +
                " (" +  PopularMovies._ID + " INTEGER PRIMARY KEY, " +
                PopularMovies.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                PopularMovies.COLUMN_POSTER_PATH + " TEXT, " +
                PopularMovies.COLUMN_OVERVIEW + " TEXT," +
                PopularMovies.COLUMN_VOTE_AVERAGE + " TEXT, " +
                PopularMovies.COLUMN_RELEASE_DATE + " TEXT " +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_POPULAR_MOVIES);

        //Create HighlyRatedMovies table
        final String SQL_CREATE_TABLE_HIGHLY_RATED_MOVIES = "CREATE TABLE " + HighlyRatedMovies.TABLE_NAME +
                " (" +  HighlyRatedMovies._ID + " INTEGER PRIMARY KEY, " +
                HighlyRatedMovies.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                HighlyRatedMovies.COLUMN_POSTER_PATH + " TEXT, " +
                HighlyRatedMovies.COLUMN_OVERVIEW + " TEXT," +
                HighlyRatedMovies.COLUMN_VOTE_AVERAGE + " TEXT, " +
                HighlyRatedMovies.COLUMN_RELEASE_DATE + " TEXT " +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_HIGHLY_RATED_MOVIES);

        //Create FavouriteMovies table
        final String SQL_CREATE_TABLE_FAVOURITE_MOVIES = "CREATE TABLE " + FavouriteMovies.TABLE_NAME +
                " (" +  FavouriteMovies._ID + " INTEGER PRIMARY KEY, " +
                FavouriteMovies.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                FavouriteMovies.COLUMN_POSTER_PATH + " TEXT, " +
                FavouriteMovies.COLUMN_OVERVIEW + " TEXT," +
                FavouriteMovies.COLUMN_VOTE_AVERAGE + " TEXT, " +
                FavouriteMovies.COLUMN_RELEASE_DATE + " TEXT " +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_FAVOURITE_MOVIES);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(LOG_TAG, "Database upgrade from version " + i + " to version " + i1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMovies.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HighlyRatedMovies.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HighlyRatedMovies.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
