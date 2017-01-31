package com.example.android.popularmovies;

import android.content.Intent;
import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.w3c.dom.Text;

public class MovieDetail extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetail.class.getSimpleName();
    TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView movieTitle;
        TextView movieRelDate;
        ImageView moviePoster;
        TextView movieAvgVote;
        TextView moviePlot;

        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            movieTitle = (TextView) findViewById(R.id.movie_title);
            movieRelDate = (TextView) findViewById(R.id.movie_release_date);
            moviePoster = (ImageView) findViewById(R.id.movie_poster);
            movieAvgVote = (TextView) findViewById(R.id.movie_vote_avg);
            moviePlot = (TextView) findViewById(R.id.movie_plot);

            errorMessage = (TextView) findViewById(R.id.error_message);

            String movieData = intent.getStringExtra(Intent.EXTRA_TEXT);

            try {
                movieTitle.setText(TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.TITLE));
                movieRelDate.append(" " + TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.RELEASE_DATE));
                movieAvgVote.append(" " + TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.VOTE_AVERAGE));
                moviePlot.setText(TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.PLOT));
                boolean hdImg = true;
                Picasso.with(this).load(TheMovieDbJsonUtils.getMoviePosterPath(movieData, hdImg)).into(moviePoster);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, e.toString());
                showErrorMessage();
            }
        }
    }

    private void showErrorMessage(){
        errorMessage.setVisibility(View.VISIBLE);
    }
}
