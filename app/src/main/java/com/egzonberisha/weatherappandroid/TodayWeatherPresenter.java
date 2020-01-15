package com.egzonberisha.weatherappandroid;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Toast;

import com.egzonberisha.weatherappandroid.Common.Common;
import com.egzonberisha.weatherappandroid.Model.Weather;
import com.egzonberisha.weatherappandroid.Model.WeatherResult;
import com.egzonberisha.weatherappandroid.Retrofit.IOpenWeatherMap;
import com.egzonberisha.weatherappandroid.Retrofit.RetrofitClient;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TodayWeatherPresenter extends MvpBasePresenter<TodayWeatherView> {

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;


    public void loadWeatherInformation(final boolean pullToRefresh) {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "matric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherResult -> {

                    ifViewAttached(view -> {
                        view.setData(weatherResult);
                        view.showContent();
                    });
                }, throwable -> {
                    ifViewAttached(view -> {
                        view.showError(throwable.getCause(),pullToRefresh);
                    });
                }));

    }

}
