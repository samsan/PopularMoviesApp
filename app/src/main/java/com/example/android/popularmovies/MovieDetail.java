package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

public class MovieDetail extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetail.class.getSimpleName();
    private TextView errorMessage;
    private TextView img_error_message;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


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
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            movieTitle = (TextView) findViewById(R.id.movie_title);
            movieRelDate = (TextView) findViewById(R.id.movie_release_date);
            moviePoster = (ImageView) findViewById(R.id.movie_poster);
            movieAvgVote = (TextView) findViewById(R.id.movie_vote_avg);
            moviePlot = (TextView) findViewById(R.id.movie_plot);

            errorMessage = (TextView) findViewById(R.id.error_message);
            img_error_message = (TextView) findViewById(R.id.img_error_message);

            String movieData = intent.getStringExtra(Intent.EXTRA_TEXT);

            try {
                movieTitle.setText(TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.TITLE));
                movieRelDate.append(" " + TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.RELEASE_DATE));
                movieAvgVote.append(" " + TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.VOTE_AVERAGE));


                // working on poster data
                moviePlot.setText(TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.PLOT));
                boolean hdImg = true;
                Picasso.with(this)
                        .load(TheMovieDbJsonUtils.getMoviePosterPath(movieData, hdImg))
                        .into(moviePoster, new Callback() {
                            @Override
                            public void onSuccess() {
                                img_error_message.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                Log.e(LOG_TAG, "No image available :(");
                                img_error_message.setVisibility(View.VISIBLE);
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, e.toString());
                showErrorMessage();
            }
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void showErrorMessage() {
        errorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MovieDetail Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
