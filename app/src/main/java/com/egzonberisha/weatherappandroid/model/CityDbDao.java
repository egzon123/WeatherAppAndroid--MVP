package com.egzonberisha.weatherappandroid.model;

import java.util.List;

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
    Long countRecords();

    @Query("SELECT * FROM citydb_table where name LIKE :cityName || '%'")
    List<CityDb> searchForCity(String cityName);
}
