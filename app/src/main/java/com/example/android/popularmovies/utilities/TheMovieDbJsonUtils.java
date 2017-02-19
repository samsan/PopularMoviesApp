package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by massi on 27/01/2017.
 */

public class TheMovieDbJsonUtils {
    public static final String MOVIE_ID = "id";
    public static final String PLOT = "overview";
    public static final String RELEASE_DATE = "release_date";
    public static final String TITLE = "title";
    public static final String TRAILER_TITLE = "name";
    public static final String TRAILER_YT_KEY = "key";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String REVIEW_AUTHOR = "author";
    public static final String REVIEW_CONTENT = "content";


    public static String[] getMoviesSimpleData(Context context, String rawData) throws JSONException {
        final String LOG_TAG = TheMovieDbJsonUtils.class.getSimpleName();
        final String RESULTS = "results";

        // in case of error json has this param
        final String STATUS_CODE = "status_code";
        String[] parsedMoviesData;

        JSONObject jsonObject = new JSONObject(rawData);

        if (jsonObject.has(STATUS_CODE)) {
            int status_code = jsonObject.getInt(STATUS_CODE);
            switch (status_code) {
                // error 401
                case 36:
                    Log.e(LOG_TAG, "This token hasn't been granted write permission by the user.");
                    return null;
                // error 404
                case 34:
                    Log.e(LOG_TAG, "The resource you requested could not be found.");
                    return null;
                // error 500
                case 11:
                    Log.e(LOG_TAG, "Internal error: Something went wrong, contact TMDb.");
                    return null;
            }
        }

        JSONArray moviesJSONArray = jsonObject.getJSONArray(RESULTS);
        parsedMoviesData = new String[moviesJSONArray.length()];

        for (int i=0; i<moviesJSONArray.length(); i++){
            JSONObject movieJSONObject = moviesJSONArray.getJSONObject(i);
            parsedMoviesData[i] = movieJSONObject.toString();
        }
        return parsedMoviesData;
    }

    public static String getMoviePosterPath(String movieData, boolean hd) throws JSONException {
        final String POSTER_PATH = "poster_path";
        JSONObject movieJSONObject = new JSONObject(movieData);
        String moviePosterUrl = NetworkUtils.BuildPosterUrl(
                movieJSONObject.getString(POSTER_PATH), hd).toString();
        return moviePosterUrl;
    }

    public static String getStringFromJsonField(String movieData, String field) throws JSONException {
        JSONObject movieJSONObject = new JSONObject(movieData);
        if (movieJSONObject.has(field)) {
            return movieJSONObject.getString(field);
        }
        return null;
    }
}
