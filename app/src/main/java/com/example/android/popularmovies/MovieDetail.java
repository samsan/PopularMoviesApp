package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MovieDetail extends AppCompatActivity {

    private TextView moviePlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            moviePlot = (TextView) findViewById(R.id.movie_plot);

            String extras = intent.getStringExtra(Intent.EXTRA_TEXT);
            moviePlot.setText(extras);
        }
    }
}
