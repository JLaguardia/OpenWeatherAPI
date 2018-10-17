package com.prismsoftworks.openweatherapitest.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.prismsoftworks.openweatherapitest.MainActivity;
import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.adapter.CityListAdapter;
import com.prismsoftworks.openweatherapitest.fragments.MapFragment;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;
import com.prismsoftworks.openweatherapitest.model.city.UnitType;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.model.list.ListItemState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CityListService {
    private static CityListService instance;
    private static final String PREF_NAME = "prefs";
    private static final String CITIES_KEY = "storedCities";
    private List<CityListItem> list;
    private CityListAdapter adapter;
    private Context mContext;
    private SharedPreferences mPrefs;
    private MapFragment mMapFrag;
    private Map<String, Bitmap> cachedIcons = new HashMap<>();

    public static CityListService getInstance(){
        if(instance == null){
            instance = new CityListService();
        }

        return instance;
    }

    private CityListService(){
        list = new ArrayList<>();
    }

    public void registerContext(Context context){
        this.mContext = context;
        mPrefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void registerMapFragment(MapFragment fragment){
        this.mMapFrag = fragment;
    }

    public void showDetails(CityListItem city){
        ((MainActivity)mContext).showCityDetails(city);
    }

    public void clearContext(){
        this.mContext = null;
        mPrefs = null;
        mMapFrag = null;
        list.clear();
    }

    public void moveCamera(LatLng coord){
        mMapFrag.moveCamera(coord);
    }

    public List<CityListItem> getList() {
        return list;
    }

    public void addItems(CityListItem... items){
        List<CityListItem> toAdd = new ArrayList<>();
        List<CityListItem> toRemove = new ArrayList<>();
        for(CityListItem item : items){
            if(item == null){
                if(list.size() == 0) {
                    toAdd.add(null);
                } else {
                    toRemove.add(null);
                }
                break;
            }

            for(CityListItem saved : list){
                if(saved == null){
                    toRemove.add(null);
                }

                if(saved != null && item.getCoordinates().equals(saved.getCoordinates())){
                    item.setState(ListItemState.UPDATED);
                }
            }

            switch (item.getState()){
                case INSERTED:
                    toAdd.add(item);
                    break;
                case UPDATED:
                    toAdd.add(item);
                    toRemove.add(item);
                    break;
            }
        }

        list.removeAll(toRemove);
        list.addAll(toAdd);

        if(list.size() == 1 && list.get(0) == null){
            ((MainActivity)mContext).clearRecycler();
        }

        if(mMapFrag != null) {
            mMapFrag.setCities(new HashSet<>(list)).refreshMap();
        }
        getAdapter().notifyItemRangeChanged(0, list.size());
        getAdapter().notifyDataSetChanged();
    }

    public Set<CityListItem> bookmarkCity(CityListItem city){
        addItems(city);
        invalidatePreferences();

        return new HashSet<>(list);
    }

    private void invalidatePreferences(){
        StringBuilder citiescsv = new StringBuilder();
        for(CityListItem item : list){
            if(item == null){
                continue;
            }

            if(citiescsv.length() > 0) {
                citiescsv.append(";");
            }

            citiescsv.append(item.getName());
            citiescsv.append(",");
            citiescsv.append(String.valueOf(item.getCoordinates().latitude));
            citiescsv.append(",");
            citiescsv.append(String.valueOf(item.getCoordinates().longitude));
        }

        mPrefs.edit().putString(CITIES_KEY, citiescsv.toString()).apply();
    }

    public void deleteCity(CityListItem city){
        list.remove(city);
        mMapFrag.setCities(new HashSet<>(list)).refreshMap();
        Log.e("CITY LIST", "size is " + list.size());
        if(list.size() == 0){
            CityListItem[] arr = {null};
            addItems(arr);
        }

        invalidatePreferences();
    }

    public CityListAdapter getAdapter() {
        if(adapter == null){
            adapter = new CityListAdapter();
            adapter.setItemList(list);
        }

        return adapter;
    }

    public Bitmap getCachedIcon(String key){
        return cachedIcons.get(key);
    }

    public void registerIcon(String key, Bitmap bmp){
        cachedIcons.put(key, bmp);
    }

    public String getTemperatureString(CityListItem item){
        switch (item.getChosenUnitType()){
            case KELVIN:
                return item.getCityItem().getTemperature().getTemperature() + "°K";
            case IMPERIAL:
                return item.getCityItem().getTemperature().getTemperature() + "°F";
            case METRIC:
                return item.getCityItem().getTemperature().getTemperature() + "°C";
        }

        return item.getCityItem().getTemperature().getTemperature() + "°?";
    }

}
