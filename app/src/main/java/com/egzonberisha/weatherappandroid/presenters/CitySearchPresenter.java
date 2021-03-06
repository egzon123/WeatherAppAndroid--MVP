package com.egzonberisha.weatherappandroid.presenters;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.util.Log;

import com.egzonberisha.weatherappandroid.WeatherApp;
import com.egzonberisha.weatherappandroid.common.Common;
import com.egzonberisha.weatherappandroid.model.CityDbRepository;
import com.egzonberisha.weatherappandroid.retrofit.IOpenWeatherMap;
import com.egzonberisha.weatherappandroid.retrofit.RetrofitClient;
import com.egzonberisha.weatherappandroid.views.CitySearchMvpView;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import retrofit2.Retrofit;

public class CitySearchPresenter extends MvpBasePresenter<CitySearchMvpView> {
    private String cityNameToSearch;

    private CompositeDisposable disposables;
    private PublishSubject<String> mPublishSubject;
    private IOpenWeatherMap mService;
    private CityDbRepository cityDbRepository;

    public void initCityPresenter() {
        disposables = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
        cityDbRepository = new CityDbRepository(WeatherApp.getInstance());
    }

    public void setCityNameToSearch(String cityName) {
        cityNameToSearch = cityName;
    }


    public void loadWeatherInformation(final boolean pullToRefresh) {
        disposables.add(mService.getWeatherByCityName(cityNameToSearch,
                Common.APP_ID,
                "matric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherResult -> {
                    ifViewAttached(view -> {
                        System.out.println(weatherResult.toString() + "=======Inside loadWeather result");
                        view.setData(weatherResult);
                        view.showContent();
                    });
                }, throwable -> {
                    ifViewAttached(view -> {
                        view.showError(throwable.getCause(), pullToRefresh);
                    });
                }));
    }

    @SuppressLint("CheckResult")
    public void initObservable() {
        mPublishSubject = PublishSubject.create();
        disposables.add(
                mPublishSubject.debounce(400, TimeUnit.MILLISECONDS)
                        .toFlowable(BackpressureStrategy.DROP)
                        .filter(charSecuence -> charSecuence.length() > 3)
                        .flatMap(s -> cityDbRepository.getCitiesByCityNameObservabe(s))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(strings -> {
                            System.out.println("-->>Inside onNext init--->> " + strings.toString());
                            if (strings.size() >= 1) {

                                ifViewAttached(view -> {
                                    view.loadingVisibilityGone();
                                    System.out.println("Inside showSearchResult --------->>>>>>> " + strings.size() + " -- " + strings.get(0));
                                    long startTime = SystemClock.elapsedRealtime();
                                    view.setSuggestions(strings);

                                    Log.d("CityFragment", "Time it took:" + (SystemClock.elapsedRealtime() - startTime));
                                });

                            }
                        }, throwable -> {

                        })
        );

    }


    public void onTextChangedListener(String text) {
        mPublishSubject.onNext(text);
    }

    public void clearDisposable() {
        disposables.clear();
    }
}
