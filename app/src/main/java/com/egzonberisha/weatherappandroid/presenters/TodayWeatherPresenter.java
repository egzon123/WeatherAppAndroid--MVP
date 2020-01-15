package com.egzonberisha.weatherappandroid.presenters;

import com.egzonberisha.weatherappandroid.common.Common;
import com.egzonberisha.weatherappandroid.retrofit.IOpenWeatherMap;
import com.egzonberisha.weatherappandroid.retrofit.RetrofitClient;
import com.egzonberisha.weatherappandroid.views.TodayWeatherView;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TodayWeatherPresenter extends MvpBasePresenter<TodayWeatherView> {


    public void loadWeatherInformation(final boolean pullToRefresh,CompositeDisposable compositeDisposable,IOpenWeatherMap mService) {
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
