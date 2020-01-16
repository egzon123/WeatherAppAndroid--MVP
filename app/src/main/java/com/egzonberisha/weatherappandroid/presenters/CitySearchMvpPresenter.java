package com.egzonberisha.weatherappandroid.presenters;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.egzonberisha.weatherappandroid.common.Common;
import com.egzonberisha.weatherappandroid.model.CityDbRepository;
import com.egzonberisha.weatherappandroid.retrofit.IOpenWeatherMap;
import com.egzonberisha.weatherappandroid.retrofit.RetrofitClient;
import com.egzonberisha.weatherappandroid.views.CitySearchMvpView;
import com.egzonberisha.weatherappandroid.views.TodayWeatherView;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import retrofit2.Retrofit;

public class CitySearchMvpPresenter extends MvpBasePresenter<CitySearchMvpView> {
    private CityDbRepository cityDbRepository;
    private String cityNameToSearch;

    public void initRepository(CityDbRepository cityDbRepository) {
        {
            this.cityDbRepository = cityDbRepository;
        }

    }

    public void setCityNameToSearch(String cityName){
        cityNameToSearch = cityName;
    }


    public void loadWeatherInformation(final boolean pullToRefresh, CompositeDisposable compositeDisposable, IOpenWeatherMap mService) {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
        compositeDisposable.add(mService.getWeatherByCityName(cityNameToSearch,
                Common.APP_ID,
                "matric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherResult -> {

                    ifViewAttached(view -> {
                        System.out.println(weatherResult.toString()+"=======Inside loadWeather result");
                        view.setData(weatherResult);
                        view.showContent();
                    });
                }, throwable -> {
                    ifViewAttached(view -> {
                        view.showError(throwable.getCause(), pullToRefresh);
                    });
                }));
    }
}
