package com.egzonberisha.weatherappandroid.views;

import com.egzonberisha.weatherappandroid.model.WeatherResult;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView;

import java.util.List;

public interface CitySearchMvpView extends MvpLceView<WeatherResult> {
    void loadingVisibilityGone();
    void setSuggestions(List<String> suggestions);


}
