package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.example.android.popularmovies.SensibleData.THE_MOVIE_DB_API_KEY;

/**
 * Created by massi on 26/01/2017.
 */

public class NetworkUtils {

    private static final String API_KEY = THE_MOVIE_DB_API_KEY;

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    private static final String YOUTUBE_VIDEO_PARAMETER = "v";

    public static final String THE_MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie";

    public static final String THE_MOVIE_DB_POPULAR_MOVIES_URL = THE_MOVIE_DB_BASE_URL + "/popular";
    public static final String THE_MOVIE_DB_TOP_RATED_MOVIES_URL = THE_MOVIE_DB_BASE_URL + "/top_rated";
    public static final String THE_MOVIE_DB_MOVIE_TRAILERS_PATH = "/videos";

    public static final String THE_MOVIE_DB_MOVIE_REVIEWS_PATH = "/reviews";
    private static final String THE_MOVIE_DB_POSTER_BASE_URL = "https://image.tmdb.org/t/p";
    private static final String POSTER_URL_RECOMMENDED_SIZE = THE_MOVIE_DB_POSTER_BASE_URL + "/w185";
    private static final String POSTER_URL_SIZE_BIG = THE_MOVIE_DB_POSTER_BASE_URL + "/w780";

    private static final String APY_KEY_PARAM = "api_key";

    public NetworkUtils(){
    }

    public static Uri BuildYouTubeUri(String videoId){
        Uri buildUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_PARAMETER, videoId)
                .build();
        return buildUri;
    }

    private static URL BuildUrl(String queryURL){
        Uri buildUri = Uri.parse(queryURL).buildUpon()
                .appendQueryParameter(APY_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL BuildPosterUrl(String posterPath, boolean hd){
        String posterUrl = POSTER_URL_RECOMMENDED_SIZE;
        if (hd) {
            posterUrl = POSTER_URL_SIZE_BIG;
        }

        Uri buildUri = Uri.parse(posterUrl).buildUpon()
                .appendPath(posterPath.replace("/", ""))
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(String queryURL) throws IOException {
        URL url = BuildUrl(queryURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(5000);
        InputStream inputStream = httpURLConnection.getInputStream();
        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("\\A");

        try {
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            httpURLConnection.disconnect();
        }
    }

    public static boolean hazInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static String getTrailersURL(String movieId){
        return THE_MOVIE_DB_BASE_URL + "/" + movieId + THE_MOVIE_DB_MOVIE_TRAILERS_PATH;
    }

    public static String getReviewsURL(String movieId){
        return THE_MOVIE_DB_BASE_URL + "/" + movieId + THE_MOVIE_DB_MOVIE_REVIEWS_PATH;
    }

    public static String getMovieDetailsURL(String movieId){
        return THE_MOVIE_DB_BASE_URL + "/" + movieId;
    }

}
