package com.example.wiffle_adeel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class List1 {
    //
    @SerializedName("dt")
    @Expose
    private Integer dt;

    @SerializedName("main")
    @Expose
    private infoAPI main;
    @SerializedName("weather")
    @Expose
    private java.util.List<Weather> weather = null;
//    @SerializedName("clouds")
//    @Expose
//    private Clouds clouds;
//    @SerializedName("wind")
//    @Expose
//    private Wind wind;
//    @SerializedName("visibility")
//    @Expose
//    private Integer visibility;
//    @SerializedName("pop")
//    @Expose
//    private Integer pop;
//    @SerializedName("sys")
//    @Expose
//    private Sys sys;
//    @SerializedName("dt_txt")
//    @Expose
//    private String dtTxt;


    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public infoAPI getMain() {
        return main;
    }

    public void setMain(infoAPI main) {
        this.main = main;
    }

    public java.util.List<Weather> getWeather() {
        return weather;
    }
//
//    public void setWeather(java.util.List<Weather> weather) {
//        this.weather = weather;
//    }
//
//    public Clouds getClouds() {
//        return clouds;
//    }
//
//    public void setClouds(Clouds clouds) {
//        this.clouds = clouds;
//    }
//
//    public Wind getWind() {
//        return wind;
//    }
//
//    public void setWind(Wind wind) {
//        this.wind = wind;
//    }
//
//    public Integer getVisibility() {
//        return visibility;
//    }
//
//    public void setVisibility(Integer visibility) {
//        this.visibility = visibility;
//    }
//
//    public Integer getPop() {
//        return pop;
//    }
//
//    public void setPop(Integer pop) {
//        this.pop = pop;
//    }
//
//    public Sys getSys() {
//        return sys;
//    }
//
//    public void setSys(Sys sys) {
//        this.sys = sys;
//    }
//
//    public String getDtTxt() {
//        return dtTxt;
//    }
//
//    public void setDtTxt(String dtTxt) {
//        this.dtTxt = dtTxt;
//    }



}
