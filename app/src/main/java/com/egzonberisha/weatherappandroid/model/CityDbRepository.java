package com.egzonberisha.weatherappandroid.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class CityDbRepository {
    private CityDbDao cityDbDao;
    private List<CityDb> listCities;
    private long recordsCount;

    public CityDbRepository(Application application) {
        CityDbDatabase database = CityDbDatabase.getInstance(application);
        cityDbDao = database.cityDbDao();
        listCities = new ArrayList<>();

    }

    public void insert(CityDb cityDb) {
        cityDbDao.insert(cityDb);
    }

    @SuppressLint("CheckResult")
    public void loadCountRecords() {
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

    public void setRecordsCount(Long l) {
        this.recordsCount = l;
    }

    public Flowable<List<String>> getCitiesByCityNameObservabe(String cityName) {

        return cityDbDao.searchForCity(cityName)
                .map(cityDbs -> {
                    List<String> citiesNames = new ArrayList<>();
                    for (CityDb cityDb : cityDbs) {
                        citiesNames.add(cityDb.getName());
                    }
                    return citiesNames;
                });
    }



}
