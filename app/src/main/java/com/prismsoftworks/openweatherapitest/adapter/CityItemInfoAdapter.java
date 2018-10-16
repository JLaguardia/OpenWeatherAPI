package com.prismsoftworks.openweatherapitest.adapter;

import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.prismsoftworks.openweatherapitest.MainActivity;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.task.PullTask;

import java.util.List;
import java.util.Set;

public class CityItemInfoAdapter implements GoogleMap.InfoWindowAdapter{
    private List<CityItem> cityList; //pull task to get this
    private Context mContext;
    private Set<LatLng> mCoords;

    public CityItemInfoAdapter(Context context, Set<LatLng> coords){
        this.mContext = context;
        this.mCoords = coords;
        populateInfoWindowList();
    }

    public void populateInfoWindowList(){
//        cityList = PullTask.getInstance().executeForCoordinates(mCoords);

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public void bookmarkNewCity(CityListItem city){
        mCoords.add(city.getCoordinates());

    }
}
