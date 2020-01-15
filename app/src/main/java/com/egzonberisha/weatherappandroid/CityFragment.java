package com.egzonberisha.weatherappandroid;


import android.annotation.SuppressLint;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import retrofit2.Retrofit;

import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egzonberisha.weatherappandroid.Common.Common;
import com.egzonberisha.weatherappandroid.Model.CityDb;
import com.egzonberisha.weatherappandroid.Model.WeatherResult;
import com.egzonberisha.weatherappandroid.Retrofit.IOpenWeatherMap;
import com.egzonberisha.weatherappandroid.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static java.lang.System.err;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityFragment extends Fragment {
    private List<String> listCities;
    private MaterialSearchBar searchBar;
    private PublishSubject<String> mPublishSubject;
    private TextView mNoResultsTextview;
    private SqliteHelper sqliteHelper;


    ImageView img_weather;
    TextView txt_city_name, txt_humidity, txt_sunrise, txt_sunset, txt_pressure, txt_temperature, txt_description, txt_date_time, txt_wind, txt_geo_coord;
    LinearLayout weather_panel;
    ProgressBar loading;
    CompositeDisposable disposables;
    IOpenWeatherMap mService;

    static CityFragment instance;

    public static CityFragment getInstance() {
        if (instance == null) {
            instance = new CityFragment();
        }
        return instance;
    }

    public CityFragment() {

        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sqliteHelper = new SqliteHelper(context);
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
        loading = itemView.findViewById(R.id.loading);
        searchBar = itemView.findViewById(R.id.searchBar);
        mNoResultsTextview = itemView.findViewById(R.id.mNoResultsTextview);
        disposables = new CompositeDisposable();
        listCities = new ArrayList<>();
        initObservable();
        searchBar.setEnabled(true);


       listenToSearchInput();


//       new LoadCities().execute(); // AsyncTask class to load Cities list

        return itemView;
    }


    //
    @SuppressLint("CheckResult")
    private void initObservable() {
        mPublishSubject = PublishSubject.create();
        mPublishSubject
                .debounce(400, TimeUnit.MILLISECONDS)
                .map(searchString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(List<String> strings) {

                        loading.setVisibility(View.GONE);
                        if(strings.size() >1) {

                            System.out.println("Inside showSearchResult --------->>>>>>> " + strings.size() + " -- " + strings.get(0));
                            long startTime = SystemClock.elapsedRealtime();
                            searchBar.setLastSuggestions(strings);
                            searchBar.showSuggestionsList();
                            searchConfirmed();
                            Log.d("CityFragment", "Time it took:" + (SystemClock.elapsedRealtime() - startTime));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void handleSearchResults(List<String> strings) {
        if (strings.isEmpty()) {
//            showNoSearchResults();
        } else {
            showSearchResults(strings);
        }
    }

    private void showNoSearchResults() {
        mNoResultsTextview.setVisibility(View.VISIBLE);

    }


    private void showSearchResults(List<String> cities) {
        loading.setVisibility(View.GONE);
        if(cities.size() >1){

        System.out.println("Inside showSearchResult --------->>>>>>> "+cities.size()+" -- "+cities.get(0));
        long startTime = SystemClock.elapsedRealtime();
        searchBar.setLastSuggestions(cities);
        Log.d("CityFragment","Time it took:" + (SystemClock.elapsedRealtime() - startTime));

        }
    }

    Function<String, List<String>> searchString = new Function<String, List<String>>() {
        @Override
        public List<String> apply(String s) throws Exception {

            return sqliteHelper.searchForCity(s);
        }

    };

    public void searchConfirmed(){
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {

                @Override
                public void onSearchStateChanged(boolean enabled) {

                }


                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());
                  getView().clearFocus();
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });
    }

//    private class LoadCities extends SimpleAsyncTask<List<CityDb>> {
//
//        @Override
//        protected List<CityDb> doInBackgroundSimple() {
//
//            SqliteHelper sqliteHelper = new SqliteHelper(getContext());
//
//
//            return sqliteHelper.getAllCitites();
//        }
//
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        @Override
//        protected void onSuccess(final List<CityDb> listCity) {
//
//            super.onSuccess(listCity);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                strings = listCity.stream()
//                        .map(object -> Objects.toString(object,null))
//                        .collect(Collectors.toList());
//            }
//            searchBar.setEnabled(true);
//            searchBar.addTextChangeListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    List<String> suggest = new ArrayList<>();
//                    long startTime = SystemClock.elapsedRealtime();
//                    for (CityDb search : listCity) {
//                        if (search.getName().toLowerCase().contains(searchBar.getText().toLowerCase())) {
//                            suggest.add(search.getName());
//                        }
//                    }
//                    searchBar.setLastSuggestions(suggest);
//                    Log.d("CityFragment","Time it took:" + (SystemClock.elapsedRealtime() - startTime));
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
////
//                }
//            });
//
//            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
//
//                @Override
//                public void onSearchStateChanged(boolean enabled) {
//
//                }
//
//
//                @Override
//                public void onSearchConfirmed(CharSequence text) {
//                    getWeatherInformation(text.toString());
//
//                   searchBar.setLastSuggestions(strings);
//                }
//
//                @Override
//                public void onButtonClicked(int buttonCode) {
//
//                }
//            });
//
//            searchBar.setLastSuggestions(strings);
//
//
//        }
//    }


    private void getWeatherInformation(String cityName) {
        disposables.add(mService.getWeatherByCityName(cityName,
                Common.APP_ID,
                "matric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(img_weather);

                        //Load information
                        txt_city_name.setText(weatherResult.getName());
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
                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println("Error ->>>" + throwable.getMessage().toString());
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );

    }

    private void listenToSearchInput(){

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("Inside LIsten toSearch -----------------------");
                mPublishSubject.onNext(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
//
            }
        });
    }

//    private void listenToSearchInput() {
//        searchBar.setEnabled(true);
//            searchBar.addTextChangeListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    List<String> suggest = new ArrayList<>();
//                    long startTime = SystemClock.elapsedRealtime();
//                    for (CityDb search : listCity) {
//                        if (search.getName().toLowerCase().contains(searchBar.getText().toLowerCase())) {
//                            suggest.add(search.getName());
//                        }
//                    }
//                    searchBar.setLastSuggestions(suggest);
//                    Log.d("CityFragment","Time it took:" + (SystemClock.elapsedRealtime() - startTime));
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
////
//                }
//            });
//
//            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
//
//                @Override
//                public void onSearchStateChanged(boolean enabled) {
//
//                }
//
//
//                @Override
//                public void onSearchConfirmed(CharSequence text) {
//                    getWeatherInformation(text.toString());
//
//                   searchBar.setLastSuggestions(strings);
//                }
//
//                @Override
//                public void onButtonClicked(int buttonCode) {
//
//                }
//            });
//
//            searchBar.setLastSuggestions(strings);
//            loading.setVisibility(View.GONE);
//
//
//
//    }


    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        disposables.clear();
        super.onStop();
    }
}
