package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;

import org.json.JSONException;

import java.io.IOException;

public class UsersReviews extends AppCompatActivity{

    private static final String LOG_TAG = UsersReviews.class.getSimpleName();
    private ListView reviews_list;
    private TextView error_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_users_reviews);
        reviews_list = (ListView) findViewById(R.id.reviews_list);
        error_message = (TextView) findViewById(R.id.error_message);

        if (getIntent().hasExtra(Intent.EXTRA_TEXT)){
            // working on trailers and reviews data
            String movie_reviews = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            String[] reviews = null;
            try {
                reviews = TheMovieDbJsonUtils.getMoviesSimpleData(this, movie_reviews);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (reviews != null){
                String[] formattedReviews = new String[reviews.length];
                for (int i = 0; i< reviews.length; i++){
                    try {
                        String author = TheMovieDbJsonUtils.getStringFromJsonField(reviews[i], TheMovieDbJsonUtils.REVIEW_AUTHOR);
                        String review = TheMovieDbJsonUtils.getStringFromJsonField(reviews[i], TheMovieDbJsonUtils.REVIEW_CONTENT);
                        formattedReviews[i] = author + "\n\n" + review;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w(LOG_TAG, "A review has been lost");
                    }
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.review_item, R.id.review_id, formattedReviews);
                reviews_list.setAdapter(arrayAdapter);
            } else {
                error_message.setText(R.string.no_data);
            }
        }
    }
}
