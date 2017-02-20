package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by massi on 15/02/2017.
 */

public class TrailersListAdapter extends ArrayAdapter<String> {
    public TrailersListAdapter(Context context, ArrayList<String> trailers) {
        super(context, 0, trailers);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        String trailerData = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_trailer, parent, false);
        }

        final TextView trailerName = (TextView) convertView.findViewById(R.id.trailer_name);
        final ImageButton bShowTrailer = (ImageButton) convertView.findViewById(R.id.b_show_trailer);
        final ImageButton bShareTrailer = (ImageButton) convertView.findViewById(R.id.b_share_trailer);

        try {
            String trailerTitle = TheMovieDbJsonUtils.getStringFromJsonField(trailerData, TheMovieDbJsonUtils.TRAILER_TITLE);
            String trailerYouTubeId = TheMovieDbJsonUtils.getStringFromJsonField(trailerData, TheMovieDbJsonUtils.TRAILER_YT_KEY);
            final Uri trailerYtUri = NetworkUtils.BuildYouTubeUri(trailerYouTubeId);

            trailerName.setText(trailerTitle);

            // launching implicit intent
            bShowTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(trailerYtUri);
                    if (intent.resolveActivity(getContext().getPackageManager()) != null){
                        getContext().startActivity(Intent.createChooser(intent,
                                getContext().getResources().getString(
                                        R.string.choose_app)));
                    }
                }
            });

            bShareTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, trailerYtUri);
                    if (intent.resolveActivity(getContext().getPackageManager()) != null){
                        getContext().startActivity(Intent.createChooser(intent,
                                getContext().getResources().getString(
                                        R.string.choose_app)));
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
