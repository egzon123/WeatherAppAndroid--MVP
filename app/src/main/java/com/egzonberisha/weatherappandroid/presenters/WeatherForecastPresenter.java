package com.egzonberisha.weatherappandroid.presenters;

import android.util.Log;

import com.egzonberisha.weatherappandroid.common.Common;
import com.egzonberisha.weatherappandroid.model.WeatherForecastResult;
import com.egzonberisha.weatherappandroid.retrofit.IOpenWeatherMap;
import com.egzonberisha.weatherappandroid.retrofit.RetrofitClient;
import com.egzonberisha.weatherappandroid.views.WeatherForecastView;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class WeatherForecastPresenter  extends MvpBasePresenter<WeatherForecastView> {

   private CompositeDisposable compositeDisposable;
   private IOpenWeatherMap mService;

   public void init(){
       compositeDisposable = new CompositeDisposable();
       Retrofit retrofit = RetrofitClient.getInstance();
       mService = retrofit.create(IOpenWeatherMap.class);
   }

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
