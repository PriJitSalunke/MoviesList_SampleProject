package com.example.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.Entity.Movie_Table;

import java.util.List;

@Dao
public interface Movie_dao {
    @Query("SELECT * FROM Movie_Data")
    List<Movie_Table> getAll();

    @Query("SELECT * FROM Movie_Data where title LIKE  :title AND id LIKE :id")
    Movie_Table findByName(String title, int id);

    @Query("SELECT COUNT(*) from Movie_Data")
    int countUsers();

    @Insert
    void insertAll(Movie_Table... users);

    @Delete
    void delete(Movie_Table user);
}
