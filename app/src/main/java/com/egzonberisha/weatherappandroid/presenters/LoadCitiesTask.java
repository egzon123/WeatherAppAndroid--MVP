package com.egzonberisha.weatherappandroid.presenters;

import android.app.Application;
import android.content.Context;

import com.egzonberisha.weatherappandroid.R;
import com.egzonberisha.weatherappandroid.WeatherApp;
import com.egzonberisha.weatherappandroid.model.CityDb;
import com.egzonberisha.weatherappandroid.model.CityDbRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class LoadCitiesTask extends SimpleAsyncTask<Void> {
    private CityDbRepository cityDbRepository;

    @Override
    protected Void doInBackgroundSimple() {
        cityDbRepository = new CityDbRepository(WeatherApp.getInstance());
        System.out.println("<<<----- LoadCityTask  count = " + cityDbRepository.countRecords());
        //check if database exist
        if (cityDbRepository.countRecords() < 1) {
            List<String> listCities = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream inputStream = WeatherApp.getInstance().getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
                InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
//                    System.out.println("----------->>>>>>>>>>>>> "+line);
                    listCities = new Gson().fromJson(builder.toString(), new TypeToken<List<String>>() {
                    }.getType());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("-------------->>>>>>");
            for (String s : listCities) {
                cityDbRepository.insert(new CityDb(s));
            }
            System.out.println("-------------->>>>>>");

        }
        return null;
    }
}
