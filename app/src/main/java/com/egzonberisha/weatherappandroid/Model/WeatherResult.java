package com.egzonberisha.weatherappandroid.Model;

import java.util.ArrayList;

public class WeatherResult {
    Coord coord;
    ArrayList< Weather > weather = new ArrayList< >();
    private String base;
    Main main;
    Wind wind;
    Clouds clouds;
    private long dt;
    Sys sys;
    private float id;
    private String name;
    private float cod;

    public ArrayList<Weather> getWeather() {
        return weather;
    }

    public void setWeather(ArrayList<Weather> weather) {
        this.weather = weather;
    }
    // Getter Methods

    public Coord getCoord() {
        return coord;
    }

    public String getBase() {
        return base;
    }

    public Main getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public long getDt() {
        return dt;
    }

    public Sys getSys() {
        return sys;
    }

    public float getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getCod() {
        return cod;
    }

    // Setter Methods

    public void setCoord(Coord coordObject) {
        this.coord = coordObject;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void setWind(Wind windObject) {
        this.wind = windObject;
    }

    public void setClouds(Clouds cloudsObject) {
        this.clouds = cloudsObject;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public void setSys(Sys sysObject) {
        this.sys = sysObject;
    }

    public void setId(float id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCod(float cod) {
        this.cod = cod;
    }
}
