package com.example.android.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by massi on 20/02/2017.
 */


public class MovieDbHelper extends SQLiteOpenHelper{

    public static final String DB_NAME = "movies.db";
    public static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String MOVIES_CREATE_TABLE =
                "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME +
                        "(" + MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL " +
                        MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL );";
        db.execSQL(MOVIES_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (db.needUpgrade(newVersion)){
            db.execSQL("DROP TABLE " + MoviesContract.MovieEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}
