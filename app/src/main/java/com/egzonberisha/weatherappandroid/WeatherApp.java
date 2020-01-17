package com.egzonberisha.weatherappandroid;

import android.app.Application;

public class WeatherApp extends Application {
    private static WeatherApp instance;

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("Inside onCreate Weather App");
        instance = this;

    }

    public static WeatherApp getInstance(){
        return instance;
    }
}
