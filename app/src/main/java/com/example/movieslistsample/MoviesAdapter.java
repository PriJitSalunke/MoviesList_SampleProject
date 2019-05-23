package com.example.movieslistsample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.Entity.Movie_Table;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private List<Movie_Table> moviesList;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_movie_title, tv_Ratings, tv_Release_Date;
        ImageView iv_movie_pic;
        LinearLayout ll_movie;

        public MyViewHolder(View view) {
            super(view);
            tv_movie_title = (TextView) view.findViewById(R.id.tv_movie_title);
            tv_Ratings = (TextView) view.findViewById(R.id.tv_Ratings);
            tv_Release_Date = (TextView) view.findViewById(R.id.tv_Release_Date);
            iv_movie_pic = (ImageView) view.findViewById(R.id.iv_movie_pic);
            ll_movie = (LinearLayout) view.findViewById(R.id.ll_movie);
        }
    }


    public MoviesAdapter(List<Movie_Table> moviesList,Context con) {
        this.moviesList = moviesList;
        this.context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Movie_Table movie = moviesList.get(position);
        holder.tv_movie_title.setText(movie.getTitle());
        holder.tv_Ratings.setText(movie.getVote_average()+"");
        holder.tv_Release_Date.setText(movie.getRelease_date()+"");

        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" +movie.getPoster_path()).into(holder.iv_movie_pic);

        holder.ll_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater)
                        v.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.details_layout, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);



                ImageView iv_cancel = popupView.findViewById(R.id.iv_cancel);
                iv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                TextView tv_movie_title = popupView.findViewById(R.id.tv_movie_title);
                TextView tv_Ratings = popupView.findViewById(R.id.tv_Ratings);
                TextView tv_vote_Count = popupView.findViewById(R.id.tv_vote_Count);
                TextView tv_Release_Date = popupView.findViewById(R.id.tv_Release_Date);
                TextView tv_original_lang = popupView.findViewById(R.id.tv_original_lang);
                TextView tv_popularity = popupView.findViewById(R.id.tv_popularity);
                TextView tv_adult = popupView.findViewById(R.id.tv_adult);
                TextView tv_overview = popupView.findViewById(R.id.tv_overview);
                ImageView iv_movie_pic = popupView.findViewById(R.id.iv_movie_pic);

                tv_movie_title.setText(movie.getTitle()+"");
                tv_Ratings.setText(movie.getVote_average()+"");
                tv_vote_Count.setText(movie.getVote_count()+"");
                tv_Release_Date.setText(movie.getRelease_date()+"");
                tv_original_lang.setText(movie.getOriginal_language()+"");
                tv_popularity.setText(movie.getPopularity()+"");
                tv_adult.setText(movie.getAdult()+"");
                tv_overview.setText(movie.getOverview()+"");

                Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" +movie.getPoster_path()).into(iv_movie_pic);

            }
        });
    }

//        http://image.tmdb.org/t/p/w185/ziEuG1essDuWuC5lpWUaw1uXY2O.jpg


    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
