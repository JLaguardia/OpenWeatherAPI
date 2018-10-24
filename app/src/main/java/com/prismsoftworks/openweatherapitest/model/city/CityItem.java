package com.prismsoftworks.openweatherapitest.model.city;

import com.google.gson.annotations.SerializedName;

public class CityItem {
    @SerializedName("cod")
    public String code;

    @SerializedName("message")
    public String message;

    @SerializedName("coord")
    private Coordinates coordinates;

    @SerializedName("weather")
    private Weather[] weather;

    @SerializedName("dt")
    private String date;

    @SerializedName("dt_txt")
    private String dateStr;

    @SerializedName("main")
    private Temperature temperature;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("clouds")
    private Cloud clouds = new Cloud();

    @SerializedName("rain")
    private Rain rain = new Rain();

    @SerializedName("sys")
    private Intl intl;

    private CityItem[] forecast = new CityItem[5];

    private class Intl{
        @SerializedName("country")
        private String country;
        public String getCountry() {
            return country;
        }
        public void setCountry(String country) {
            this.country = country;
        }

    }

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public String getIntl() {
        return intl.getCountry();
    }

    public void setIntl(Intl intl) {
        this.intl = intl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cloud getClouds() {
        return clouds;
    }

    public void setClouds(Cloud clouds) {
        this.clouds = clouds;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public CityItem[] getForecast() {
        return forecast;
    }

    public void setForecast(CityItem[] forecast) {
        this.forecast = forecast;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage(){
        return "Code: " + code + " Message: " + message;
    }
}
