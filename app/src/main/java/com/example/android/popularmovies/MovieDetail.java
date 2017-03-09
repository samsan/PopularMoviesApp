package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.PopularMoviesContract;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;

public class MovieDetail extends AppCompatActivity{

    private static final String LOG_TAG = MovieDetail.class.getSimpleName();
    private TextView errorMessage;
    private TextView imgErrorMessage;
    private Button showReviewsButton;
    private static String movieReviews;
    private static String movieTrailers;
    private static final String MOVIE_ID = "movie_id";
    private Button showMovieTrailers;
    private ImageButton mFavorite;
    private String movieId;
    private String movieTitle;

    private static boolean isMovieFavorite;

    public static final String[] MOVIE_DETAIL_PROJECTION = {
            PopularMoviesContract.PopularMoviesEntry.COLUMN_ID,
            PopularMoviesContract.PopularMoviesEntry.COLUMN_TITLE,
    };

    private static final int TRAILERS_REVIEWS_LOADER = 41;

    private static final int IS_FAVORITE_LOADER = 31;
    private LoaderManager.LoaderCallbacks<String[]> trailerReviewsLoader;

    private LoaderManager.LoaderCallbacks<Cursor> favoritesLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView twMovieRelDate;
        ImageView iwMoviePoster;
        TextView twMovieAvgVote;
        TextView twMoviePlot;
        TextView twMovieTitle;

        setContentView(R.layout.activity_movie_detail);

        trailerReviewsLoader = new LoaderManager.LoaderCallbacks<String[]>() {
            @Override
            public Loader<String[]> onCreateLoader(int id, final Bundle args) {
                return new AsyncTaskLoader<String[]>(getBaseContext()) {

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
                try {
                    movieTrailers = data[0];
                    movieReviews = data[1];

                    if (TheMovieDbJsonUtils.isJsonArrayEmpty(movieTrailers, "results")){
                        showMovieTrailers.setVisibility(View.VISIBLE);
                    } else {
                        showMovieTrailers.setVisibility(View.INVISIBLE);
                    }

                    if (TheMovieDbJsonUtils.isJsonArrayEmpty(movieReviews, "results")){
                        showReviewsButton.setVisibility(View.VISIBLE);
                    } else {
                        showReviewsButton.setVisibility(View.INVISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoaderReset(Loader<String[]> loader) {
                // nothing to do
            }
        };

        favoritesLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
                switch (loaderId){
                    case IS_FAVORITE_LOADER:
                        if (args != null){
                            String mId = args.getString(MOVIE_ID, null);
                            if (mId != null){
                                Uri movieUri = Uri.withAppendedPath(
                                        PopularMoviesContract.PopularMoviesEntry.CONTENT_URI,
                                        mId);
                                String[] selectionArgs = {mId};
                                return new CursorLoader(getBaseContext(),
                                        movieUri,
                                        MOVIE_DETAIL_PROJECTION,
                                        null, selectionArgs, null);
                            }
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException("Action does not exist " + loaderId);
                }
                return null;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                int loaderId = loader.getId();
                boolean isCursorValid = false;
                if (data != null && data.moveToFirst()){
                    isCursorValid = true;
                }

                if (isCursorValid){
                    switch (loaderId){
                        case IS_FAVORITE_LOADER:
                            // movie in mFavorite
                            favIconChangeToFavorite();
                            isMovieFavorite = true;
                            break;
                    }
                } else {
                    switch (loaderId){
                        case IS_FAVORITE_LOADER:
                            // movie not in mFavorite
                            favIconChangeToUnfavorite();
                            isMovieFavorite = false;
                            break;
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                // do nothing
            }
        };

        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            twMovieTitle = (TextView) findViewById(R.id.movie_title);
            twMovieRelDate = (TextView) findViewById(R.id.movie_release_date);
            iwMoviePoster = (ImageView) findViewById(R.id.movie_poster);
            twMovieAvgVote = (TextView) findViewById(R.id.movie_vote_avg);
            twMoviePlot = (TextView) findViewById(R.id.movie_plot);
            showReviewsButton = (Button) findViewById(R.id.b_show_reviews);
            errorMessage = (TextView) findViewById(R.id.error_message);
            imgErrorMessage = (TextView) findViewById(R.id.img_error_message);
            showMovieTrailers = (Button) findViewById(R.id.b_show_trailers);
            mFavorite = (ImageButton) findViewById(R.id.b_add_favorite);

            String movieData = intent.getStringExtra(Intent.EXTRA_TEXT);

            try {
                movieId = TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.MOVIE_ID);
                movieTitle = TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.TITLE);
                twMovieTitle.setText(movieTitle);
                twMovieRelDate.append(" " + TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.RELEASE_DATE));
                twMovieAvgVote.append(" " + TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.VOTE_AVERAGE));

                // get trailers and reviews
                if (NetworkUtils.hazInternet(this)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(MOVIE_ID, movieId);
                    getSupportLoaderManager().restartLoader(TRAILERS_REVIEWS_LOADER, bundle, trailerReviewsLoader);
                } else {
                    showErrorMessage();
                    Log.i(LOG_TAG, "No internet!");
                }

                // working on poster data
                twMoviePlot.setText(TheMovieDbJsonUtils.getStringFromJsonField(movieData, TheMovieDbJsonUtils.PLOT));
                boolean hdImg = true;
                Picasso.with(this)
                        .load(TheMovieDbJsonUtils.getMoviePosterPath(movieData, hdImg))
                        .into(iwMoviePoster, new Callback() {
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
        restartFavoriteLoader();
    }

    private void restartFavoriteLoader(){
        // find out if movie in favorites
        Bundle bundle = new Bundle();
        bundle.putString(MOVIE_ID, movieId);
        getSupportLoaderManager().restartLoader(IS_FAVORITE_LOADER, bundle, favoritesLoader);
    }

    private void favIconChangeToFavorite(){
        mFavorite.setImageResource(R.drawable.ic_star_black_24dp);
    }

    private void favIconChangeToUnfavorite(){
        mFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
    }

    private void showErrorMessage() {
        errorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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

    public void actionFavorite(View view) {
        if (isMovieFavorite){
            getContentResolver().delete(
                    Uri.withAppendedPath(PopularMoviesContract.PopularMoviesEntry.CONTENT_URI, movieId),
                    null, null);
            favIconChangeToUnfavorite();
            Toast.makeText(this, "deleted from favorites", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, movieTitle);
            getContentResolver().insert(PopularMoviesContract.PopularMoviesEntry.CONTENT_URI, contentValues);
            favIconChangeToFavorite();
            Toast.makeText(this, "added to favorites", Toast.LENGTH_SHORT).show();
        }
        restartFavoriteLoader();
    }
}
