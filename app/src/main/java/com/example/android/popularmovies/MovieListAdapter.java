package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

/**
 * Created by massi on 25/01/2017.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListAdapterViewHolder>{

    private String[] moviesData;
    private final MovieOnClickHandler movieClickHandler;
    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();


    public interface MovieOnClickHandler {
        void openDetail(String movieData);
    }

    public MovieListAdapter(MovieOnClickHandler movieOnClickHandler){
        movieClickHandler = movieOnClickHandler;
    }

    @Override
    public MovieListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int layoutForMovieItem = R.layout.recycler_movie_poster;
        View view = layoutInflater.inflate(layoutForMovieItem, parent, false);
        return new MovieListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieListAdapterViewHolder holder, int position) {
        holder.movieData = moviesData[position];
        if (holder.movieData != null) {
            try {
                boolean hdImg = false;
                holder.moviePosterUrl = TheMovieDbJsonUtils.getMoviePosterPath(holder.movieData, hdImg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (holder.moviePosterUrl != null) {
                Picasso.with(holder.itemView.getContext())
                        .load(holder.moviePosterUrl)
                        .into(holder.rv_movie_poster);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (moviesData == null){
            return 0;
        } else {
            return moviesData.length;
        }
    }

    public class MovieListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView rv_movie_poster;
        public String movieData;
        public String moviePosterUrl;

        public MovieListAdapterViewHolder(final View itemView) {
            super(itemView);
            rv_movie_poster = (ImageView) itemView.findViewById(R.id.rv_movie_poster);
            rv_movie_poster.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // giving data for activity detail!
            movieClickHandler.openDetail(movieData);
        }
    }

    public void setMoviesData(String[] data) {
        moviesData = data;
        notifyDataSetChanged();
    }

}

