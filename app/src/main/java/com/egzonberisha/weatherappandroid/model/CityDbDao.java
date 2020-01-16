package com.egzonberisha.weatherappandroid.model;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CityDbDao {
    @Insert
    void insert(CityDb cityDb);

    @Query("SELECT * FROM citydb_table")
    List<CityDb> getAllCities();

    @Query("SELECT count(*) FROM citydb_table")
    long countRecords();
}
