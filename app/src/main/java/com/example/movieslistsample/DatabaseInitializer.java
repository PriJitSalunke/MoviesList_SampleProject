package com.example.movieslistsample;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.Entity.Movie_Table;
import com.example.database.AppDatabase;

import java.util.List;

public class DatabaseInitializer {   private static final String TAG = DatabaseInitializer.class.getName();

    public static void populateAsync(@NonNull final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final AppDatabase db) {
        populateWithTestData(db);
    }

    private static Movie_Table addMovie(final AppDatabase db, Movie_Table user) {
        db.movieDao().insertAll(user);
        return user;
    }

    private static void populateWithTestData(AppDatabase db) {
  /*      Movie_Table user = new Movie_Table();
        user.setTitle("Dilwale");
        user.setId(101);
        user.setOriginal_title("karan johar");
        addMovie(db, user);*/

        List<Movie_Table> movieList = db.movieDao().getAll();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + movieList.size());
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }

    }
}