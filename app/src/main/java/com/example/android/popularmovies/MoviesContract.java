package com.example.android.popularmovies;

import android.provider.BaseColumns;

/**
 * Created by massi on 20/02/2017.
 */

public class MoviesContract {

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_MOVIE_TITLE = "title";
    }
}
