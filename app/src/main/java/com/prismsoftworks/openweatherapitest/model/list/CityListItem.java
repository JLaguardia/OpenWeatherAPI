package com.prismsoftworks.openweatherapitest.model.list;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;
import com.prismsoftworks.openweatherapitest.model.city.UnitType;
import com.prismsoftworks.openweatherapitest.model.city.Weather;
import com.prismsoftworks.openweatherapitest.model.city.WrapperObj;
import com.prismsoftworks.openweatherapitest.task.PullTask;

import java.util.HashSet;
import java.util.Set;

public class CityListItem {
    private String name;
    private LatLng coordinates;
    private ListItemState state = ListItemState.INSERTED;
    private CityItem cityItem;
    private UnitType chosenUnitType = UnitType.IMPERIAL;

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

    public CityItem getCityItem() {
        if(cityItem == null){
            Set<LatLng> set = new HashSet<>();
            set.add(coordinates);
            String json = PullTask.getInstance().getWeatherCityJson(set, UnitType.IMPERIAL);
            Gson gson = new GsonBuilder().create();
            cityItem = gson.fromJson(json, CityItem.class);
        }

        return cityItem;
    }

    public UnitType getChosenUnitType() {
        return chosenUnitType;
    }

    public void setChosenUnitType(UnitType chosenUnitType) {
        this.chosenUnitType = chosenUnitType;
    }
}
