package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by massi on 03/03/2017.
 */

public class PopularMoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movies";

    public final static class PopularMoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";

    }
}
