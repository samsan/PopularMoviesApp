package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;

import org.json.JSONException;

import java.io.IOException;

public class  MainActivity extends AppCompatActivity implements MovieListAdapter.MovieOnClickHandler{

    private Spinner sortSpinner;
    private ProgressBar progressBar;
    private RecyclerView recycleMovieList;
    private MovieListAdapter movieListAdapter;
    private TextView errorMessage;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    public void openDetail(String movieData) {
        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra(Intent.EXTRA_TEXT, movieData);
        startActivity(intent);
        // get clicked view holder and send data to intent details
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
                if (id == 0){
                    loadMovieData(NetworkUtils.THE_MOVIE_DB_POPULAR_MOVIES_URL);
                } else if (id == 1){
                    loadMovieData(NetworkUtils.THE_MOVIE_DB_TOP_RATED_MOVIES_URL);
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

        loadMovieData(NetworkUtils.THE_MOVIE_DB_POPULAR_MOVIES_URL);
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
            loadMovieData(NetworkUtils.THE_MOVIE_DB_POPULAR_MOVIES_URL);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMovieData(String sortingUrl){
        if (NetworkUtils.hazInternet(this)) {
            new FetchMoviesTask().execute(sortingUrl);
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

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String[] moviesData = null;
            String sortingUrl = params[0];
            try {
                String res = new NetworkUtils().getResponseFromHttpUrl(sortingUrl);
                moviesData = TheMovieDbJsonUtils.getSimpleMoviesData(MainActivity.this, res);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return moviesData;
        }

        @Override
        protected void onPostExecute(String[] movies) {
            progressBar.setVisibility(View.GONE);
            if (movies != null){
                showMoviesData();
                movieListAdapter.setMoviesData(movies);
            } else {
                showErrorMessage();
            }
        }
    }
}
