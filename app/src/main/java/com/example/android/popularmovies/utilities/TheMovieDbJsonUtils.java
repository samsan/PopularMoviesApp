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
    private final String OVERVIEW = "overview";
    private final String RELEASE_DATE = "release_date";
    private final String TITLE = "title";
    private final String VOTE_AVERAGE = "vote_average";

    public static String[] getSimpleMoviesData(Context context, String rawData) throws JSONException {
        final String LOG_TAG = TheMovieDbJsonUtils.class.getSimpleName();
        final String RESULTS = "results";

        // in case of error json has this param
        final String STATUS_CODE = "status_code";
        String[] parsedWeatherData;

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
        parsedWeatherData = new String[moviesJSONArray.length()];

        for (int i=0; i<moviesJSONArray.length(); i++){
            JSONObject movieJSONObject = moviesJSONArray.getJSONObject(i);
            parsedWeatherData[i] = movieJSONObject.toString();
        }
        return parsedWeatherData;
    }

    public static String getMoviePosterPath(String movieData) throws JSONException {
        final String POSTER_PATH = "poster_path";
        JSONObject movieJSONObject = new JSONObject(movieData);
        String moviePosterUrl = NetworkUtils.BuildPosterUrl(
                movieJSONObject.getString(POSTER_PATH)).toString();
        return moviePosterUrl;
    }

}
