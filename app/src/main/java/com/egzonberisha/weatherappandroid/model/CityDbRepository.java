package com.egzonberisha.weatherappandroid.model;

import android.app.Application;
import android.os.AsyncTask;

public class CityDbRepository {
    private CityDbDao cityDbDao;

    public CityDbRepository(Application application){
        CityDbDatabase database = CityDbDatabase.getInstance(application);
        cityDbDao = database.cityDbDao();

    }

    public void insert(CityDb cityDb){
        insertCityDb(cityDb);
    }

    public long countRecords(){
       return getCountRecords();
    }

    private long getCountRecords() {
        final long[] count = {0};
        new AsyncTask<Void,Void,Long>(){

            @Override
            protected Long doInBackground(Void... voids) {
               return cityDbDao.countRecords();

            }

            @Override
            protected void onPostExecute(Long aLong) {
                super.onPostExecute(aLong);
                count[0] = aLong;
            }
        }.execute();
      return count[0];
    }

    public void insertCityDb(CityDb cityDb) {
        new AsyncTask<CityDb,Void,Void>(){
            @Override
            protected Void doInBackground(CityDb... cityDbs) {
                cityDbDao.insert(cityDbs[0]);
                return null;
            }
        }.execute(cityDb);
    }
}
