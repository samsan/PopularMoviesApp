package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;

public class MovieDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]>{

    private static final String LOG_TAG = MovieDetail.class.getSimpleName();
    private TextView errorMessage;
    private TextView imgErrorMessage;
    private Button showReviewsButton;
    private static String movieReviews;
    private static String movieTrailers;
    private static final String MOVIE_ID = "movie_id";
    private static final int TRAILERS_REVIEWS_LOADER = 42;
    private Button showMovieTrailers;
    private ImageButton addToFavorite;

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
            showReviewsButton = (Button) findViewById(R.id.b_show_reviews);
            errorMessage = (TextView) findViewById(R.id.error_message);
            imgErrorMessage = (TextView) findViewById(R.id.img_error_message);
            showMovieTrailers = (Button) findViewById(R.id.b_show_trailers);
            addToFavorite = (ImageButton) findViewById(R.id.b_add_favorite);

            String movieData = intent.getStringExtra(Intent.EXTRA_TEXT);

            try {
                String movieId = TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.MOVIE_ID);
                movieTitle.setText(TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.TITLE));
                movieRelDate.append(" " + TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.RELEASE_DATE));
                movieAvgVote.append(" " + TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.VOTE_AVERAGE));

                // get trailers and reviews
                if (NetworkUtils.hazInternet(this)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(MOVIE_ID, movieId);
                    getSupportLoaderManager().restartLoader(TRAILERS_REVIEWS_LOADER, bundle, this);
                } else {
                    showErrorMessage();
                    Log.i(LOG_TAG, "No internet!");
                }

                // working on poster data
                moviePlot.setText(TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.PLOT));
                boolean hdImg = true;
                Picasso.with(this)
                        .load(TheMovieDbJsonUtils.getMoviePosterPath(movieData, hdImg))
                        .into(moviePoster, new Callback() {
                            @Override
                            public void onSuccess() {
                                imgErrorMessage.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                Log.e(LOG_TAG, "No image available :(");
                                imgErrorMessage.setVisibility(View.VISIBLE);
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

    public void showReviews(View view) {
        Intent intent = new Intent(this, UsersReviews.class);
        intent.putExtra(Intent.EXTRA_TEXT, movieReviews);
        startActivity(intent);
    }

    public void showTrailers(View view) {
        Intent intent = new Intent(this, MovieTrailers.class);
        intent.putExtra(Intent.EXTRA_TEXT, movieTrailers);
        startActivity(intent);
    }

    public void addToFavorites(View view) {
        Toast toast = Toast.makeText(this, "FAV", Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                } else {
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                String trailersURL = NetworkUtils.getTrailersURL(args.getString(MOVIE_ID));
                String reviewsURL = NetworkUtils.getReviewsURL(args.getString(MOVIE_ID));
                try {
                    String trailersData = NetworkUtils.getResponseFromHttpUrl(trailersURL);
                    String reviewsData = NetworkUtils.getResponseFromHttpUrl(reviewsURL);
                    return new String[]{trailersData, reviewsData};
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String[] data) {
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
            /* data[0] trailers, data[1] reviews */
        if (data[0] != null){
            movieTrailers = data[0];
            showMovieTrailers.setVisibility(View.VISIBLE);

        } else {
            showMovieTrailers.setVisibility(View.INVISIBLE);
        }
        // reviews data retrieved: show button to read them
        if (data[1] != null){
            movieReviews = data[1];
            showReviewsButton.setVisibility(View.VISIBLE);
        } else {
            showReviewsButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        // nothing to do
    }
}
