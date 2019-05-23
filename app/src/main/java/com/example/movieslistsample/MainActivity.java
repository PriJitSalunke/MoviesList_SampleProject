package com.example.movieslistsample;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.Entity.Movie_Table;
import com.example.dao.Movie_dao;
import com.example.database.AppDatabase;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Movie_Table> movieList_array = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;
    int currentScrollState = 0;
    static int page = 1;
    public String url= "https://api.themoviedb.org/3/movie/popular?page="+page+"&language=en-US&api_key=55352d45ce51d045b5d1acc344a0d5a3";
    private ShimmerFrameLayout mShimmerViewContainer;
    int lastFirstVisiblePosition = 1;
    AppDatabase db;
    LinearLayout ll_rating,ll_date;

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getAppDatabase(this);

        DatabaseInitializer.populateAsync(db);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        ll_rating = findViewById(R.id.ll_rating);
        ll_date = findViewById(R.id.ll_date);

        mAdapter = new MoviesAdapter(movieList_array,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                currentScrollState = newState;
                lastFirstVisiblePosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                if(currentScrollState == SCROLL_STATE_IDLE && lastFirstVisiblePosition == (movieList_array.size()-1)) {

                        /*page++;

                        url= "https://api.themoviedb.org/3/movie/popular?page="+page+"&language=en-US&api_key=55352d45ce51d045b5d1acc344a0d5a3";

                        Log.i("url: ",url);

                        OkHttpHandler okHttpHandler= new OkHttpHandler();
                        okHttpHandler.execute(url);*/

                    getdata();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        ll_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(movieList_array, new Comparator<Movie_Table>() {
                    public int compare(Movie_Table v1, Movie_Table v2) {
                        return v1.getVote_average().compareTo(v2.getVote_average());
                    }
                });

                mAdapter.notifyDataSetChanged();
            }
        });

        ll_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Collections.sort(movieList_array, new Comparator<Movie_Table>() {
                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                    @Override
                    public int compare(Movie_Table o1, Movie_Table o2) {
                        try {
                            return f.parse(o1.getRelease_date()).compareTo(f.parse(o2.getRelease_date()));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });

                mAdapter.notifyDataSetChanged();
            }
        });

        getdata();


    }

    @Override
    protected void onDestroy() {
        AppDatabase.destroyInstance();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    public class OkHttpHandler extends AsyncTask {

        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            mShimmerViewContainer.startShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.i("result", o.toString());
            super.onPostExecute(o);

            //  {"page":1,
            //  "total_results":19849,
            //  "total_pages":993,
            //  "results":[
            //  {
            //  "vote_count":275,
            //  "id":458156,
            //  "video":false,
            //  "vote_average":7.4,
            //  "title":"John Wick: Chapter 3 – Parabellum",
            //  "popularity":563.584,
            //  "poster_path":"\/ziEuG1essDuWuC5lpWUaw1uXY2O.jpg",
            //  "original_language":"en",
            //  "original_title":"John Wick: Chapter 3 – Parabellum",
            //  "genre_ids":[80,28,53],
            //  "backdrop_path":"\/kcga7xuiQVqM9WHfWnAbidxU7hd.jpg",
            //  "adult":false,
            //  "overview":"Super-assassin John Wick returns with a $14 million price tag on his head and an army of bounty-hunting killers on his trail. After killing a member of the shadowy international assassin’s guild, the High Table, John Wick is excommunicado, but the world’s most ruthless hit men and women await his every turn.",
            //  "release_date":"2019-05-15"},
            //  {"vote_count":15,"id":280960,"video":false,"vote_avera


            try {
                Log.i("result", "123" + o);


                if (o == null) {
                    Log.i("Error", "----> Result Blank");
                } else {
                    String result = o.toString();

                    JSONObject obj = new JSONObject(result);

                    obj.getInt("page");
                    obj.getInt("total_results");
                    obj.getInt("total_pages");
                    JSONArray movie_list = obj.getJSONArray("results");

                    for (int i = 0; i < movie_list.length(); i++) {

                        JSONObject j_temp = movie_list.getJSONObject(i);

//                        j_temp.getInt("vote_count");
//                        j_temp.getInt("id");
//                        j_temp.getBoolean("video");
//                        j_temp.getDouble("vote_average");
//                        j_temp.getString("title");
//                        j_temp.getDouble("popularity");
//                        j_temp.getString("poster_path");
//                        j_temp.getString("original_language");
//                        j_temp.getString("original_title");
//                        j_temp.getString("genre_ids");
//                        j_temp.getString("backdrop_path");
//                        j_temp.getBoolean("adult");
//                        j_temp.getString("overview");
//                        j_temp.getString("release_date");

                     /*   Movie movie_obj = new Movie();

                        movie_obj.setVote_count(j_temp.getInt("vote_count"));
                        movie_obj.setId(j_temp.getInt("id"));
                        movie_obj.setVideo(j_temp.getBoolean("video"));
                        movie_obj.setVote_average(j_temp.getDouble("vote_average"));
                        movie_obj.setTitle(j_temp.getString("title"));
                        movie_obj.setPopularity(j_temp.getDouble("popularity"));
                        movie_obj.setPoster_path(j_temp.getString("poster_path"));
                        movie_obj.setOriginal_language(j_temp.getString("original_language"));
                        movie_obj.setOriginal_title(j_temp.getString("original_title"));
                        movie_obj.setGenre_ids(j_temp.getString("genre_ids"));
                        movie_obj.setBackdrop_path(j_temp.getString("backdrop_path"));
                        movie_obj.setAdult(j_temp.getBoolean("adult"));
                        movie_obj.setOverview(j_temp.getString("overview"));
                        movie_obj.setRelease_date(j_temp.getString("release_date"));*/



                        //------------------Room--------------------------//

                        Movie_Table mt = new Movie_Table();

                        mt.setVote_count(j_temp.getInt("vote_count"));
                        mt.setId(j_temp.getInt("id"));
                        mt.setVideo(j_temp.getBoolean("video"));
                        mt.setVote_average(j_temp.getDouble("vote_average"));
                        mt.setTitle(j_temp.getString("title"));
                        mt.setPopularity(j_temp.getDouble("popularity"));
                        mt.setPoster_path(j_temp.getString("poster_path"));
                        mt.setOriginal_language(j_temp.getString("original_language"));
                        mt.setOriginal_title(j_temp.getString("original_title"));
                        mt.setGenre_ids(j_temp.getString("genre_ids"));
                        mt.setBackdrop_path(j_temp.getString("backdrop_path"));
                        mt.setAdult(j_temp.getBoolean("adult"));
                        mt.setOverview(j_temp.getString("overview"));
                        mt.setRelease_date(j_temp.getString("release_date"));

                        db.movieDao().insertAll(mt);
                        movieList_array.add(mt);

                        //--------------------------------------------//

                        mAdapter.notifyDataSetChanged();

                    }

                }
            } catch (JSONException e) {
            }

            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);

        }
        @Override
        protected Object doInBackground(Object[] objects) {
            Request.Builder builder = new Request.Builder();
            builder.url(objects[0].toString());
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public void getdata(){

        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        if (netInfo != null) {
            if (netInfo.isConnected()) {

                movieList_array = new ArrayList<Movie_Table>(db.movieDao().getAll());

                int count = movieList_array.size();
                Log.i("room count 1",count+"");

                int current_page = count/20;
                Log.i("current_page 1",current_page+"");
                //No internet

                mAdapter = new MoviesAdapter(movieList_array,this);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);

                page = current_page+1;

                url= "https://api.themoviedb.org/3/movie/popular?page="+page+"&language=en-US&api_key=55352d45ce51d045b5d1acc344a0d5a3";

                Log.i("url: ",url);


                OkHttpHandler okHttpHandler= new OkHttpHandler();
                okHttpHandler.execute(url);

                // Internet Available
            }else {
                movieList_array.clear();
                movieList_array = new ArrayList<Movie_Table>(db.movieDao().getAll());

                int count = movieList_array.size();
                Log.i("room count 1",count+"");

                int current_page = count/20;
                Log.i("current_page 1",current_page+"");
                //No internet

                mAdapter = new MoviesAdapter(movieList_array,this);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);

                page = current_page+1;

                url= "https://api.themoviedb.org/3/movie/popular?page="+page+"&language=en-US&api_key=55352d45ce51d045b5d1acc344a0d5a3";

                Log.i("url: ",url);


                OkHttpHandler okHttpHandler= new OkHttpHandler();
                okHttpHandler.execute(url);
            }
        } else {
            movieList_array.clear();
            movieList_array = new ArrayList<Movie_Table>(db.movieDao().getAll());

            Log.i("room count",movieList_array.size()+"");

            int count = movieList_array.size();
            int current_page = count/20;
            Log.i("current_page",current_page+"");

            mAdapter = new MoviesAdapter(movieList_array,this);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            //No internet

            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
        }

    }


}


