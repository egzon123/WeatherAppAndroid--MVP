package com.egzonberisha.weatherappandroid.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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

    @SuppressLint("CheckResult")
    public void loadCountRecords(){
//        try {
//            recordsCount = new AyncTaskCountData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return recordsCount;
     Observable.fromCallable(new Callable<Long>() {

         @Override
         public Long call() throws Exception {
             return cityDbDao.countRecords();
         }
     }).subscribe(aLong -> setRecordsCount(aLong));
    }

    public long getRecordsCount() {
        loadCountRecords();
        return recordsCount;
    }

    public void setRecordsCount(Long l){
        this.recordsCount = l;
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

//    private class AyncTaskCountData extends AsyncTask<Void,Void,Long>{
//
//        @Override
//        protected Long doInBackground(Void... voids) {
//            return cityDbDao.countRecords();
//        }
//
//        @Override
//        protected void onPostExecute(Long aLong) {
//            super.onPostExecute(aLong);
//            recordsCount = aLong;
//        }
//    }


}
