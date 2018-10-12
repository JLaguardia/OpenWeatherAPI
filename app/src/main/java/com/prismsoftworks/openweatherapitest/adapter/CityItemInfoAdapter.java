package com.prismsoftworks.openweatherapitest.adapter;

import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;

import java.util.List;

public class CityItemInfoAdapter implements GoogleMap.InfoWindowAdapter{
    private List<CityItem> cityList;
    private Context mContext;

    public CityItemInfoAdapter(Context context){
        this.mContext = context;
    }

    public void populateInfoWindoList(){
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
