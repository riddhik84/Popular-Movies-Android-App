package com.myapps.rk.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.mock.MockDialogInterface;
import android.util.Log;

import com.myapps.rk.popularmovies.data.MoviesContract.*;

/**
 * Created by RKs on 4/10/2016.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    public final String LOG_TAG = MoviesDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "popularmovies.db";
    private static final int DATABASE_VERSION = 9;

    public MoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TABLE_MOVIES = "CREATE TABLE " + Movies.TABLE_NAME +
                " (" +  Movies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Movies.COLUMN_POSTER_PATH + " TEXT, " +
                Movies.COLUMN_OVERVIEW + " TEXT," +
                Movies.COLUMN_RELEASE_DATE + " TEXT, " +
                Movies.COLUMN_MOVIE_ID + " TEXT, " +
                Movies.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                Movies.COLUMN_VOTE_AVERAGE + " TEXT, " +
                Movies.COLUMN_SORT_ORDER + " TEXT, " +
                "UNIQUE (" + Movies.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE );";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_MOVIES);

        final String SQL_CREATE_TABLE_FAVOURITE_MOVIES = "CREATE TABLE " + FavouriteMovies.TABLE_NAME +
                " (" +  FavouriteMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavouriteMovies.COLUMN_POSTER_PATH + " TEXT, " +
                FavouriteMovies.COLUMN_OVERVIEW + " TEXT," +
                FavouriteMovies.COLUMN_RELEASE_DATE + " TEXT, " +
                FavouriteMovies.COLUMN_MOVIE_ID + " TEXT, " +
                FavouriteMovies.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                FavouriteMovies.COLUMN_VOTE_AVERAGE + " TEXT, " +
                "UNIQUE (" + FavouriteMovies.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE );";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_FAVOURITE_MOVIES);

        final String SQL_CREATE_TABLE_TRAILERS = "CREATE TABLE " + Trailers.TABLE_NAME +
                " (" +  Trailers._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Trailers.COLUMN_TRAILER_ID + " TEXT, " +
                Trailers.COLUMN_KEY + " TEXT, " +
                Trailers.COLUMN_NAME + " TEXT, " +
                Trailers.COLUMN_SITE + " TEXT, " +
                Trailers.COLUMN_SIZE + " TEXT, " +
                Trailers.COLUMN_MOVIE_ID + " TEXT " +
                ");";
     //   Log.d(LOG_TAG, "Create table trailers " +SQL_CREATE_TABLE_TRAILERS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_TRAILERS);

        final String SQL_CREATE_TABLE_REVIEWS = "CREATE TABLE " + Reviews.TABLE_NAME +
                " (" +  Reviews._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Reviews.COLUMN_REVIEW_ID + " TEXT, " +
                Reviews.COLUMN_AUTHOR + " TEXT, " +
                Reviews.COLUMN_CONTENT + " TEXT, " +
                Reviews.COLUMN_URL + " TEXT, " +
                Reviews.COLUMN_MOVIE_ID + " TEXT " +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_REVIEWS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(LOG_TAG, "Database upgrade from version " + i + " to version " + i1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Movies.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteMovies.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Trailers.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Reviews.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
