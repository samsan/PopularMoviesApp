package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.MoviesContract;

/**
 * Created by massi on 03/03/2017.
 */

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "movies.db";
    private static final int DB_VERSION = 1;


    public PopularMoviesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME
                + " ( "
                + MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + "UNIQUE (" + MoviesContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE "
                + " ); ";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
     }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
