package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.android.popularmovies.adapters.TrailersListAdapter;
import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

public class MovieTrailers extends AppCompatActivity {

    private ListView movieTrailers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailers);

        movieTrailers = (ListView) findViewById(R.id.movie_trailers);

        if (getIntent().hasExtra(Intent.EXTRA_TEXT)){
            String trailersRawData = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            String[] trailers = null;
            try {
                trailers = TheMovieDbJsonUtils.getMoviesSimpleData(this, trailersRawData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (trailers != null){
                ArrayList<String> lTrailers = new ArrayList<>(Arrays.asList(trailers));
                TrailersListAdapter trailersListAdapter = new TrailersListAdapter(this, lTrailers);
                movieTrailers.setAdapter(trailersListAdapter);
                movieTrailers.setVisibility(View.VISIBLE);
            }
        }
    }
}
