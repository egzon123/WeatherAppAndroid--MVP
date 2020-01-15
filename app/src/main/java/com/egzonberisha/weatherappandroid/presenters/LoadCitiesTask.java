package com.egzonberisha.weatherappandroid.presenters;

import android.content.Context;

import com.egzonberisha.weatherappandroid.R;
import com.egzonberisha.weatherappandroid.model.CityDb;
import com.egzonberisha.weatherappandroid.model.SqliteHelper;
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
    SqliteHelper sqliteHelper;
    private Context context;

    public LoadCitiesTask(Context context){
        this.context = context;
    }
        @Override
        protected Void doInBackgroundSimple() {
        sqliteHelper = new SqliteHelper(context);
            //check if database exist
            if(sqliteHelper.isEmpty(SqliteHelper.TABLE_CITIES)){



            List<String> listCities = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream inputStream = context.getResources().openRawResource(R.raw.city_list);
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
            for(String s :listCities){
                sqliteHelper.addCity(new CityDb(null,s));
            }
            System.out.println("-------------->>>>>>");

            }
            return null;
        }
    }
