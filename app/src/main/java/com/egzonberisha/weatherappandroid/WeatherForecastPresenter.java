package com.egzonberisha.weatherappandroid;

import android.util.Log;

import com.egzonberisha.weatherappandroid.Common.Common;
import com.egzonberisha.weatherappandroid.Model.WeatherForecastResult;
import com.egzonberisha.weatherappandroid.Retrofit.IOpenWeatherMap;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class WeatherForecastPresenter  extends MvpBasePresenter<WeatherForecastView> {

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    public void loadWeatherForecastInfo(boolean pullToRefresh){
        compositeDisposable.add(mService.getForecastWeatherByLatLng(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "matric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        ifViewAttached(view -> {
                            view.setData(weatherForecastResult);
                            view.showContent();
                        });
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("Error -> ",throwable.getMessage());
                    }
                })
        );
    }

}
