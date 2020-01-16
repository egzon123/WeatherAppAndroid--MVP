package com.egzonberisha.weatherappandroid.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "citydb_table")
public class CityDb {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    public CityDb(String name){
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public  String toString() {
        return name;
    }
}
