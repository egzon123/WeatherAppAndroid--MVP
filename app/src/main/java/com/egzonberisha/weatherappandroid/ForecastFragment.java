package com.egzonberisha.weatherappandroid;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.egzonberisha.weatherappandroid.Adapter.WeatherForecastAdapter;
import com.egzonberisha.weatherappandroid.Common.Common;
import com.egzonberisha.weatherappandroid.Model.WeatherForecastResult;
import com.egzonberisha.weatherappandroid.Model.WeatherResult;
import com.egzonberisha.weatherappandroid.Retrofit.IOpenWeatherMap;
import com.egzonberisha.weatherappandroid.Retrofit.RetrofitClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {
    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    TextView txt_city_name,txt_geo_coord;
    RecyclerView recycler_forecast;

    static ForecastFragment instance;

    public static ForecastFragment getInstance(){
        if(instance == null){
            instance = new ForecastFragment();

        }
        return instance;
    }

    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit  retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_forecast, container, false);

        txt_city_name = itemView.findViewById(R.id.txt_city_name);
        txt_geo_coord = itemView.findViewById(R.id.txt_geo_coord);
        recycler_forecast = itemView.findViewById(R.id.recycler_forecast);
        recycler_forecast.setHasFixedSize(true);
        recycler_forecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        
        getForecastWeatherInformation();
        return itemView;
    }

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

    private void getForecastWeatherInformation() {
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
                        displayForecastWeather(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                            Log.d("Error -> ",throwable.getMessage());
                    }
                })
        );
    }

    private void displayForecastWeather(WeatherForecastResult weatherForecastResult) {
        txt_city_name.setText(new StringBuilder(weatherForecastResult.city.getName()));
        txt_geo_coord.setText(new StringBuilder(weatherForecastResult.city.getCoord().toString()));
        WeatherForecastAdapter weatherForecastAdapter = new WeatherForecastAdapter(getContext(),weatherForecastResult);
        recycler_forecast.setAdapter(weatherForecastAdapter);

    }


}
