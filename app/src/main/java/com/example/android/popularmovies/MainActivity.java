package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.adapters.MovieListAdapter;
import com.example.android.popularmovies.data.PopularMoviesContract;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.android.popularmovies.utilities.NetworkUtils.getMovieDetailsURL;

public class  MainActivity extends AppCompatActivity implements
        MovieListAdapter.MovieOnClickHandler,
        LoaderManager.LoaderCallbacks<String[]>{

    private Spinner sortSpinner;
    private ProgressBar progressBar;
    private RecyclerView recycleMovieList;
    private MovieListAdapter movieListAdapter;
    private TextView errorMessage;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String MOVIES_URL_QUERY = "MOVIES_URL_QUERY";
    private static final int MOVIES_LOADER = 42;
    private static final int FAV_MOVIES_LOADER = 43;

    private static LoaderManager.LoaderCallbacks<String[]> favoritesLoader;

    public static final String[] MOVIE_DETAIL_PROJECTION = {
            PopularMoviesContract.PopularMoviesEntry.COLUMN_ID,
            PopularMoviesContract.PopularMoviesEntry.COLUMN_TITLE,
    };

    private static final int FAV_MOVIE_ID = 0;

    private static final int SPINNER_POPULAR_MOVIES_INDEX = 0;
    private static final int SPINNER_TOP_RATED_MOVIES_INDEX = 1;
    private static final int SPINNER_FAVORITE_MOVIES_INDEX = 2;

    @Override
    public void openDetail(String movieData) {
        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra(Intent.EXTRA_TEXT, movieData);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        errorMessage = (TextView) findViewById(R.id.error_message);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.sort_types_array,
                R.layout.support_simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast toast = Toast.makeText(
                        MainActivity.this,
                        sortSpinner.getSelectedItem().toString(),
                        Toast.LENGTH_SHORT);
                toast.show();
                if (id == SPINNER_POPULAR_MOVIES_INDEX){
                    loadMovieData(NetworkUtils.THE_MOVIE_DB_POPULAR_MOVIES_URL);
                } else if (id == SPINNER_TOP_RATED_MOVIES_INDEX){
                    loadMovieData(NetworkUtils.THE_MOVIE_DB_TOP_RATED_MOVIES_URL);
                } else if (id == SPINNER_FAVORITE_MOVIES_INDEX) {
                    loadFavoriteMoviesData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recycleMovieList = (RecyclerView) findViewById(R.id.recycle_movie_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, calculateNoOfColumns(getBaseContext()));

        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recycleMovieList.setLayoutManager(gridLayoutManager);
        recycleMovieList.setHasFixedSize(true);
        movieListAdapter = new MovieListAdapter(this);
        recycleMovieList.setAdapter(movieListAdapter);

        // loadMovieData(NetworkUtils.THE_MOVIE_DB_POPULAR_MOVIES_URL);
    }

    public static int calculateNoOfColumns(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 160);
        return noOfColumns;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(MainActivity.this);
        menuInflater.inflate(R.menu.popularmovies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_refresh) {
            int selectedItem = sortSpinner.getSelectedItemPosition();
            switch (selectedItem){
                case SPINNER_POPULAR_MOVIES_INDEX:
                    loadMovieData(NetworkUtils.THE_MOVIE_DB_POPULAR_MOVIES_URL);
                    break;
                case SPINNER_TOP_RATED_MOVIES_INDEX:
                    loadMovieData(NetworkUtils.THE_MOVIE_DB_TOP_RATED_MOVIES_URL);
                    break;
                case SPINNER_FAVORITE_MOVIES_INDEX:
                    loadFavoriteMoviesData();
                    break;
                default:
                    throw new RuntimeException("Cannot perform action :(");
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFavoriteMoviesData(){
        favoritesLoader = new LoaderManager.LoaderCallbacks<String[]>() {
            @Override
            public Loader<String[]> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<String[]>(getBaseContext()) {
                    @Override
                    protected void onStartLoading() {
                        progressBar.setVisibility(View.VISIBLE);
                        forceLoad();
                    }

                    @Override
                    public String[] loadInBackground() {
                        ArrayList<String> favoriteMovies = new ArrayList<>();
                        Cursor cursor = getContentResolver().query(
                                PopularMoviesContract.PopularMoviesEntry.CONTENT_URI,
                                MOVIE_DETAIL_PROJECTION,
                                null,
                                null,
                                null
                        );

                        if (cursor != null){
                            while (cursor.moveToNext()){
                                try {
                                    favoriteMovies.add(NetworkUtils.getResponseFromHttpUrl(getMovieDetailsURL(cursor.getString(FAV_MOVIE_ID))));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            cursor.close();

                        } else {
                            // no favorites
                            return null;
                        }

                        return favoriteMovies.toArray(new String[favoriteMovies.size()]);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<String[]> loader, String[] data) {
                progressBar.setVisibility(View.GONE);
                if (data != null){
                    showMoviesData();
                    movieListAdapter.setMoviesData(data);
                } else {
                    showErrorMessage();
                }
            }

            @Override
            public void onLoaderReset(Loader<String[]> loader) {

            }
        };
        getSupportLoaderManager().restartLoader(FAV_MOVIES_LOADER, null, favoritesLoader);
    }


    private void loadMovieData(String sortingUrl){
        if (NetworkUtils.hazInternet(this)) {
            Bundle bundle = new Bundle();
            bundle.putString(MOVIES_URL_QUERY, sortingUrl);
            getSupportLoaderManager().restartLoader(MOVIES_LOADER, bundle, this);
        } else {
            Log.i(LOG_TAG, "no connection!");
            showErrorMessage();
        }
    }

    private void showMoviesData(){
        recycleMovieList.setVisibility(View.VISIBLE);
        sortSpinner.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.GONE);
    }

    private void showErrorMessage(){
        recycleMovieList.setVisibility(View.INVISIBLE);
        sortSpinner.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            private String[] mCachedata = null;
            @Override
            protected void onStartLoading() {
                if (args == null){
                    // no actions to perform
                    return;
                }
                if (mCachedata != null){
                    // No new spinner selection and data already stored: no new download :)
                    deliverResult(mCachedata);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }

            }

            @Override
            public String[] loadInBackground() {
                String[] moviesData;
                String sortingUrl = args.getString(MOVIES_URL_QUERY);
                try {
                    String res = new NetworkUtils().getResponseFromHttpUrl(sortingUrl);
                    moviesData = TheMovieDbJsonUtils.getMoviesSimpleData(MainActivity.this, res);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    return null;
                }
                return moviesData;
            }

            @Override
            public void deliverResult(String[] data) {
                mCachedata = data;
                super.deliverResult(data);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] movies) {
        progressBar.setVisibility(View.GONE);
        if (movies != null){
            showMoviesData();
            movieListAdapter.setMoviesData(movies);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        // nothing to do
    }
}
