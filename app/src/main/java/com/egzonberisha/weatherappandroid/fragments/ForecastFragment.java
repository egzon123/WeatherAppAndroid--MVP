package com.egzonberisha.weatherappandroid.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Retrofit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.egzonberisha.weatherappandroid.R;
import com.egzonberisha.weatherappandroid.adapters.WeatherForecastAdapter;
import com.egzonberisha.weatherappandroid.model.WeatherForecastResult;
import com.egzonberisha.weatherappandroid.presenters.WeatherForecastPresenter;
import com.egzonberisha.weatherappandroid.retrofit.IOpenWeatherMap;
import com.egzonberisha.weatherappandroid.retrofit.RetrofitClient;
import com.egzonberisha.weatherappandroid.views.WeatherForecastView;
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends MvpLceFragment<SwipeRefreshLayout, WeatherForecastResult, WeatherForecastView, WeatherForecastPresenter>
        implements WeatherForecastView, SwipeRefreshLayout.OnRefreshListener {

    TextView txt_city_name;
    TextView txt_geo_coord;
    RecyclerView recycler_forecast;

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        return e.getCause().toString();
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        contentView.setRefreshing(false);
        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public WeatherForecastPresenter createPresenter() {
        return new WeatherForecastPresenter();
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
        recycler_forecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return itemView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentView.setOnRefreshListener(this);
        loadData(false);
    }


    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.init();
        presenter.loadWeatherForecastInfo(pullToRefresh);
    }

    @Override
    public void setData(WeatherForecastResult weatherForecastResult) {
        txt_city_name.setText(new StringBuilder(weatherForecastResult.city.getName()));
        txt_geo_coord.setText(new StringBuilder(weatherForecastResult.city.getCoord().toString()));
        WeatherForecastAdapter weatherForecastAdapter = new WeatherForecastAdapter(getContext(), weatherForecastResult);
        recycler_forecast.setAdapter(weatherForecastAdapter);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        contentView.setRefreshing(pullToRefresh);
    }
}
