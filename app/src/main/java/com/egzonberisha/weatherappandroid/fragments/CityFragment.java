package com.egzonberisha.weatherappandroid.fragments;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egzonberisha.weatherappandroid.R;
import com.egzonberisha.weatherappandroid.common.Common;
import com.egzonberisha.weatherappandroid.model.CityDbRepository;
import com.egzonberisha.weatherappandroid.model.WeatherResult;
import com.egzonberisha.weatherappandroid.presenters.CitySearchPresenter;
import com.egzonberisha.weatherappandroid.views.CitySearchMvpView;
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceFragment;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityFragment extends MvpLceFragment<SwipeRefreshLayout, WeatherResult,CitySearchMvpView , CitySearchPresenter>
        implements CitySearchMvpView, SwipeRefreshLayout.OnRefreshListener {


    private MaterialSearchBar searchBar;

    ImageView img_weather;
    TextView txt_city_name, txt_humidity, txt_sunrise, txt_sunset, txt_pressure, txt_temperature, txt_description, txt_date_time, txt_wind, txt_geo_coord;
    LinearLayout weather_panel;
    ProgressBar loading;

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @Override
    public CitySearchPresenter createPresenter() {
        return new CitySearchPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View itemView = inflater.inflate(R.layout.fragment_city, container, false);

        img_weather = itemView.findViewById(R.id.img_weather);
        txt_city_name = itemView.findViewById(R.id.txt_city_name);
        txt_humidity = itemView.findViewById(R.id.txt_humidity);
        txt_sunrise = itemView.findViewById(R.id.txt_sunrise);
        txt_sunset = itemView.findViewById(R.id.txt_sunset);
        txt_pressure = itemView.findViewById(R.id.txt_pressure);
        txt_temperature = itemView.findViewById(R.id.text_temperature);
        txt_description = itemView.findViewById(R.id.txt_description);
        txt_date_time = itemView.findViewById(R.id.txt_date_time);
        txt_wind = itemView.findViewById(R.id.txt_wind);
        txt_geo_coord = itemView.findViewById(R.id.txt_geo_coord);

        weather_panel = itemView.findViewById(R.id.weather_panel);
        loading = itemView.findViewById(R.id.loadingView);
        searchBar = itemView.findViewById(R.id.searchBar);

         presenter.initCityPresenter();

        searchBar.setEnabled(true);
        presenter.initObservable();

        return itemView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentView.setOnRefreshListener(this);
        listenToSearchInput();

    }


    public void searchConfirmed(){
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {

                @Override
                public void onSearchStateChanged(boolean enabled) {

                }


                @Override
                public void onSearchConfirmed(CharSequence text) {
                    presenter.setCityNameToSearch(text.toString());
                  loadData(false);

                  getView().clearFocus();
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });
    }


    private void listenToSearchInput(){

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("Inside LIsten toSearch ----------------------- "+charSequence);

                presenter.onTextChangedListener(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
//
            }
        });
    }



    @Override
    public void onRefresh() {
    loadData(true);
    }

    @Override
    public void setData(WeatherResult weatherResult) {
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherResult.getWeather().get(0).getIcon())
                .append(".png").toString()).into(img_weather);

        //Load information
        txt_city_name.setText(weatherResult.getName());
        System.out.println(weatherResult.getName()+"  ------------------>>>>> SetData");
        txt_wind.setText("Speed:" + weatherResult.getWind().getSpeed() + "\n Deg:" + weatherResult.getWind().getDeg());
        txt_description.setText(new StringBuilder("Weather in ")
                .append(weatherResult.getName()).toString());
        txt_temperature.setText(new StringBuilder(String.valueOf((int) (weatherResult.getMain().getTemp() - 273))).append("°C").toString());
        txt_date_time.setText(Common.convertUnixToDate(weatherResult.getDt()));
        txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
        txt_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
        txt_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
        txt_sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
        txt_geo_coord.setText(new StringBuilder().append(weatherResult.getCoord().toString()).toString());

        //Display panel
        weather_panel.setVisibility(getView().VISIBLE);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadWeatherInformation(pullToRefresh);
    }


    @Override
    public void loadingVisibilityGone() {
        loading.setVisibility(View.GONE);
    }

    @Override
    public void setSuggestions(List<String> suggestions) {
        searchBar.setLastSuggestions(suggestions);
        searchBar.showSuggestionsList();
        searchConfirmed();
    }


    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        contentView.setRefreshing(false);
        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.clearDisposable();
    }
}
