package com.egzonberisha.weatherappandroid.model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CityDb.class},version = 1 ,exportSchema = false)
public abstract class CityDbDatabase extends RoomDatabase{
    private static CityDbDatabase instance;

    public abstract CityDbDao cityDbDao();

    public static synchronized CityDbDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),CityDbDatabase.class,"cityDb_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback  roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private CityDbDao cityDbDao;

        private PopulateDbAsyncTask(CityDbDatabase db) {
            cityDbDao = db.cityDbDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            noteDao.insert(new Note("Title 1","Description1",1));


            return null;
        }
    }
}
