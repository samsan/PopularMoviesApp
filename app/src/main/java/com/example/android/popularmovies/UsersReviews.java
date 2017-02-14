package com.example.android.popularmovies;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;

import org.json.JSONException;

import java.io.IOException;

public class UsersReviews extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[][]>{

    private static final String LOG_TAG = UsersReviews.class.getSimpleName();
    private static final String MOVIE_ID = "movie_id";
    private static final int TRAILERS_REVIEWS_LOADER = 42;
    private ListView reviews_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_users_reviews);
        reviews_list = (ListView) findViewById(R.id.reviews_list);

        if (getIntent().hasExtra(Intent.EXTRA_TEXT)){
            // working on trailers and reviews data
            String movie_id = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            Bundle bundle = new Bundle();
            bundle.putString(MOVIE_ID, movie_id);
            getSupportLoaderManager().restartLoader(TRAILERS_REVIEWS_LOADER, bundle, this);
        }
    }

    @Override
    public Loader<String[][]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[][]>(this) {

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                } else {
                    forceLoad();
                }
            }

            @Override
            public String[][] loadInBackground() {
                String movieID = args.getString(MOVIE_ID);
                String trailersURL = NetworkUtils.THE_MOVIE_DB_BASE_URL + "/" + movieID + NetworkUtils.THE_MOVIE_DB_MOVIE_TRAILERS_PATH;
                String reviewsURL = NetworkUtils.THE_MOVIE_DB_BASE_URL + "/" + movieID + NetworkUtils.THE_MOVIE_DB_MOVIE_REVIEWS_PATH;
                try {
                    String[] trailersData = TheMovieDbJsonUtils.getSimpleMoviesData(UsersReviews.this,
                            NetworkUtils.getResponseFromHttpUrl(trailersURL));
                    String[] reviewsData = TheMovieDbJsonUtils.getSimpleMoviesData(UsersReviews.this,
                            NetworkUtils.getResponseFromHttpUrl(reviewsURL));
                    return new String[][]{trailersData, reviewsData};
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String[][] data) {
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[][]> loader, String[][] data) {
        /* data[0] trailers, data[1] reviews */
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                R.layout.review_item,
                R.id.review_id,
                data[1]);
        reviews_list.setAdapter(arrayAdapter);

    }

    @Override
    public void onLoaderReset(Loader<String[][]> loader) {
        // nothing to do
    }
}
