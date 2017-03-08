package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by massi on 03/03/2017.
 */

public class PopularMoviesProvider extends ContentProvider {

    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIE_DETAILS = 101;
    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private PopularMoviesDbHelper mPopularMoviesDbHelper;


    private static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopularMoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, PopularMoviesContract.PATH_MOVIE, CODE_MOVIES);
        uriMatcher.addURI(authority, PopularMoviesContract.PATH_MOVIE + "/#", CODE_MOVIE_DETAILS);
        return uriMatcher;

    }


    @Override
    public boolean onCreate() {
        mPopularMoviesDbHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mPopularMoviesDbHelper.getReadableDatabase();
        Cursor cursor;

        int uriMatched = mUriMatcher.match(uri);

        switch (uriMatched){
            case CODE_MOVIES:
                cursor = db.query(PopularMoviesContract.PopularMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CODE_MOVIE_DETAILS:
                cursor = db.query(PopularMoviesContract.PopularMoviesEntry.TABLE_NAME,
                        projection,
                        PopularMoviesContract.PopularMoviesEntry.COLUMN_ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            default:
                throw new UnsupportedOperationException("action does not exists!");
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mPopularMoviesDbHelper.getWritableDatabase();
        int mUriMatched = mUriMatcher.match(uri);
        Uri outUri;
        switch (mUriMatched){
            case CODE_MOVIES:
                long id = db.insert(PopularMoviesContract.PopularMoviesEntry.TABLE_NAME,
                        null,
                        values);
                if (id != -1){
                    outUri = ContentUris.withAppendedId(uri, id);
                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    throw new android.database.SQLException("cannot perform insert");
                }
                break;
            default:
                throw new UnsupportedOperationException("action does not exist!");
        }
        return outUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mPopularMoviesDbHelper.getWritableDatabase();
        int mUriMatched = mUriMatcher.match(uri);
        int mRowDeleted = 0;
        switch (mUriMatched){
            case CODE_MOVIE_DETAILS:
                String movie_id = uri.getPathSegments().get(1);
                selection = "id=?";
                selectionArgs = new String[]{movie_id};
                mRowDeleted = db.delete(PopularMoviesContract.PopularMoviesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new UnsupportedOperationException("action does not exist!");
        }
        return mRowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
