package com.egzonberisha.weatherappandroid;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egzonberisha.weatherappandroid.Common.Common;
import com.egzonberisha.weatherappandroid.Model.WeatherResult;
import com.egzonberisha.weatherappandroid.Retrofit.IOpenWeatherMap;
import com.egzonberisha.weatherappandroid.Retrofit.RetrofitClient;
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceFragment;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends MvpLceFragment<SwipeRefreshLayout, WeatherResult, TodayWeatherView, TodayWeatherPresenter>
        implements TodayWeatherView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.img_weather)
    ImageView img_weather;
    @BindView(R.id.txt_city_name)
    TextView txt_city_name;
    @BindView(R.id.txt_humidity)
    TextView txt_humidity;
    @BindView(R.id.txt_sunrise)
    TextView txt_sunrise;
    @BindView(R.id.txt_sunset)
    TextView txt_sunset;
    @BindView(R.id.txt_pressure)
    TextView txt_pressure;
    @BindView(R.id.text_temperature)
    TextView txt_temperature;
    @BindView(R.id.txt_description)
    TextView txt_description;
    @BindView(R.id.txt_date_time)
    TextView txt_date_time;
    @BindView(R.id.txt_wind)
    TextView txt_wind;
    @BindView(R.id.txt_geo_coord)
    TextView txt_geo_coord;
    @BindView(R.id.weather_panel)
    LinearLayout weather_panel;
    @BindView(R.id.loading)
    ProgressBar loading;
    CompositeDisposable compositeDisposable;

    IOpenWeatherMap mService;

    static TodayWeatherFragment instance;

    public static TodayWeatherFragment getInstance() {
        if (instance == null) {
            instance = new TodayWeatherFragment();
        }
        return instance;
    }

    public TodayWeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_today, container, false);
        ButterKnife.bind(this, itemView);
//        contentView.setOnRefreshListener(this);
        loadData(false);
        return itemView;


    }

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }



    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }


    @Override
    public void onRefresh() {
        loadData(true);
    }
    @Override
    public TodayWeatherPresenter createPresenter() {
        return new TodayWeatherPresenter();
    }

    @Override public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }


    @Override public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        contentView.setRefreshing(false);
    }

    @Override public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        contentView.setRefreshing(pullToRefresh);
    }
    @Override
    public void setData(WeatherResult weatherResult) {
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherResult.getWeather().get(0).getIcon())
                .append(".png").toString()).into(img_weather);

        //Load information
        txt_city_name.setText(weatherResult.getName());
        txt_wind.setText("Speed:"+weatherResult.getWind().getSpeed()+"\n Deg:"+weatherResult.getWind().getDeg());
        txt_description.setText(new StringBuilder("Weather in ")
                .append(weatherResult.getName()).toString());
        txt_temperature.setText(new StringBuilder(String.valueOf((int)(weatherResult.getMain().getTemp()-273))).append("°C").toString());
        txt_date_time.setText(Common.convertUnixToDate(weatherResult.getDt()));
        txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
        txt_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
        txt_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
        txt_sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
        txt_geo_coord.setText(new StringBuilder().append(weatherResult.getCoord().toString()).toString());

        //Display panel
        weather_panel.setVisibility(getView().VISIBLE);
        loading.setVisibility(View.GONE);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
       presenter.loadWeatherInformation(pullToRefresh);
    }
}
