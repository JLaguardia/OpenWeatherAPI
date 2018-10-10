package com.prismsoftworks.openweatherapitest.model.list;

import com.google.android.gms.maps.model.LatLng;

public class CityListItem {
    private String name;
    private LatLng coordinates;

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
}
