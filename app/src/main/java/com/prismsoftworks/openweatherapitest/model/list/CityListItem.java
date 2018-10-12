package com.prismsoftworks.openweatherapitest.model.list;

import com.google.android.gms.maps.model.LatLng;
import com.prismsoftworks.openweatherapitest.model.city.Weather;

public class CityListItem {
    private String name;
    private LatLng coordinates;
    private Weather currentWeather;
    private ListItemState state = ListItemState.INSERTED;

    public CityListItem(){}

    public CityListItem(String name, LatLng coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public ListItemState getState() {
        return state;
    }

    public void setState(ListItemState state) {
        this.state = state;
    }

    public Weather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(Weather currentWeather) {
        this.currentWeather = currentWeather;
    }
}
