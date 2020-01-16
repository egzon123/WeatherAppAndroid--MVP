package com.egzonberisha.weatherappandroid.model;

import android.app.Application;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CityDbRepository {
    private CityDbDao cityDbDao;
    private List<CityDb> listCities;
    private long recordsCount;

    public CityDbRepository(Application application){
        CityDbDatabase database = CityDbDatabase.getInstance(application);
        cityDbDao = database.cityDbDao();
        listCities = new ArrayList<>();

    }

    public void insert(CityDb cityDb){
        insertCityDb(cityDb);
    }

    public long countRecords(){
       return recordsCount;
    }

    public List<String> getCitiesByCityName(String cityName){
        List<String> toReturn = new ArrayList<>();
       new AsyncTaskGetCities().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,cityName);
        for(CityDb city:listCities){

            toReturn.add(city.getName());
        }
        System.out.println("===ToReturn==="+toReturn.size());
       return toReturn;
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

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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


    private class AsyncTaskGetCities extends AsyncTask<String,Void,List<CityDb>>{

        @Override
        protected List<CityDb> doInBackground(String... strings) {
            return  cityDbDao.searchForCity(strings[0]);
        }

        @Override
        protected void onPostExecute(List<CityDb> cityDbs) {
            super.onPostExecute(cityDbs);
            listCities = cityDbs;
            System.out.println("InsideOnPostExc ==========>>>"+listCities.size());
        }

    }

    private class AyncTaskCountData extends AsyncTask<Void,Void,Long>{

        @Override
        protected Long doInBackground(Void... voids) {
            return cityDbDao.countRecords();
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            recordsCount = aLong;
        }
    }


}
