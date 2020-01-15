package com.egzonberisha.weatherappandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.egzonberisha.weatherappandroid.Model.City;
import com.egzonberisha.weatherappandroid.Model.CityDb;

import java.util.ArrayList;
import java.util.List;

public class SqliteHelper extends SQLiteOpenHelper {
    //DATABASE NAME
    public static final String DATABASE_NAME = "cityDb";
    //DATABASE VERSION
    public static final int DATABASE_VERSION = 1;
    //TABLE NAME
    public static final String TABLE_CITIES = "cities";
    public static final String KEY_ID = "id";

    //COLUMN user name
    public static final String KEY_NAME = "cityName";

    //SQL for creating users table
    public static final String SQL_TABLE_CITIES = " CREATE TABLE " + TABLE_CITIES
            + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_NAME + " TEXT "
            + " ) ";

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Table when oncreate gets called
        db.execSQL(SQL_TABLE_CITIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop table to create new one if database version updated
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_CITIES);
    }

    public void addCity(CityDb cityDb) {
        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create content values to insert
        ContentValues values = new ContentValues();


        values.put(KEY_NAME, cityDb.getName());
        long todo_id = db.insert(TABLE_CITIES, null, values);

    }

    public List<String> searchForCity(String cityName) {
        if(cityName.isEmpty()){
            return new ArrayList<>();
        }
        List<String> toReturn = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CITIES,// Selecting Table
                new String[]{KEY_ID, KEY_NAME},//Selecting columns want to query
                KEY_NAME + " LIKE ?",
                new String[]{cityName+"%"},
                null, null, null);


        if (cursor.moveToFirst()){
            do{
                CityDb city = new CityDb(cursor.getString(0), cursor.getString(1));
                toReturn.add(city.getName());
                // do what ever you want here
            }while(cursor.moveToNext());
        }
        return toReturn;
        }



    public void printData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CITIES,// Selecting Table
                new String[]{KEY_ID, KEY_NAME},//Selecting columns want to query
                null,null,
                null, null, null);
        System.out.println("Cursor ->  "+cursor.toString());
        if (cursor.moveToFirst()){
            do{
                CityDb city = new CityDb(cursor.getString(0), cursor.getString(1));
                System.out.println("User data =>"+city.toString());
                // do what ever you want here
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    public List<CityDb> getAllCitites(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<CityDb> cityDbList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_CITIES,// Selecting Table
                new String[]{KEY_ID, KEY_NAME},//Selecting columns want to query
                null,null,
                null, null, null);
        System.out.println("Cursor ->  "+cursor.toString());
        if (cursor.moveToFirst()){
            do{
                CityDb city = new CityDb(cursor.getString(0), cursor.getString(1));
                cityDbList.add(city);
                // do what ever you want here
            }while(cursor.moveToNext());
        }
        cursor.close();

        return cityDbList;
    }

    public boolean isEmpty(String TableName){

        SQLiteDatabase database = this.getReadableDatabase();
        int NoOfRows = (int) DatabaseUtils.queryNumEntries(database,TableName);

        if (NoOfRows == 0){
            return true;
        }else {
            return false;
        }
    }

}
