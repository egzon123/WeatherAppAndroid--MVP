package com.egzonberisha.weatherappandroid.model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.egzonberisha.weatherappandroid.WeatherApp;

@Database(entities = {CityDb.class},version = 1 ,exportSchema = false)
public abstract class CityDbDatabase extends RoomDatabase{
    private static CityDbDatabase instance;

    public abstract CityDbDao cityDbDao();

    public static synchronized CityDbDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(WeatherApp.getInstance(),CityDbDatabase.class,"cityDb_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }


}
