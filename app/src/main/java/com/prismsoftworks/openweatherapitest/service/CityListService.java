package com.prismsoftworks.openweatherapitest.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.prismsoftworks.openweatherapitest.MainActivity;
import com.prismsoftworks.openweatherapitest.adapter.CityListAdapter;
import com.prismsoftworks.openweatherapitest.fragments.MapFragment;
import com.prismsoftworks.openweatherapitest.model.city.UnitType;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.model.list.ListItemState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CityListService {
    private static CityListService instance;
    private static final String PREF_NAME = "prefs";
    private String weatherApiKey = "";
    private List<CityListItem> list;
    private CityListAdapter mAdapter;
    private Context mContext;
    private SharedPreferences mPrefs;
    private MapFragment mMapFrag;
    private Map<String, Bitmap> cachedIcons = new HashMap<>();
    private UnitType mChosenUnitType = UnitType.IMPERIAL;


    public static synchronized CityListService getInstance(){
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

    public Set<CityListItem> getCities(){
        return new HashSet<>(list);
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

                if(saved != null && item.getCoordinates().equals(saved.getCoordinates()) &&
                        item.getState() != ListItemState.DELETED){
                    item.setState(ListItemState.UPDATED);
                }
            }

            switch (item.getState()){
                case INSERTED:
                    item.setChosenUnitType(mChosenUnitType);
                    toAdd.add(item);
                    break;
                case UPDATED:
                    toAdd.add(item);
                    toRemove.add(item);
                    break;
                case DELETED:
                    toRemove.add(item);
                    break;
            }
        }

        list.removeAll(toRemove);
        list.addAll(toAdd);

        if((list.size() == 0) || (list.size() == 1 && list.get(0) == null)){
            if(list.size() == 0){
                list.add(null);
            }

            mAdapter = null;
            ((MainActivity)mContext).clearRecycler();
        }

        if(mMapFrag != null) {
            mMapFrag.refreshMap(list.get(list.size()-1));
        }

        for(CityListItem city : list){
            if(city != null){
                city.setState(ListItemState.LOADED);
            }
        }

        getAdapter().updateActiveList();
        getAdapter().notifyItemRangeChanged(0, list.size());
        getAdapter().notifyDataSetChanged();
        invalidatePreferences();
    }

    public Set<CityListItem> bookmarkCity(CityListItem city){
        addItems(city);
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

        mPrefs.edit().putString(MainActivity.CITIES_KEY, citiescsv.toString()).apply();
        mPrefs.edit().putString(MainActivity.UNITS_KEY, mChosenUnitType.name()).apply();
    }

    public void deleteCity(CityListItem city){
        list.remove(city);
        if(list.size() == 0){

            CityListItem[] arr = {null};
            addItems(arr);
        }
        getAdapter().notifyDataSetChanged();
        mMapFrag.refreshMap(null);
//        mMapFrag.setCities(new HashSet<>(list)).refreshMap(null);

        invalidatePreferences();
    }

    public CityListAdapter getAdapter() {
        if(mAdapter == null){
            mAdapter = new CityListAdapter();
            mAdapter.setItemList(list);
        }

        return mAdapter;
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

    public String getLengthMeasureString(CityListItem item, String value){
        switch (item.getChosenUnitType()){
            case KELVIN:
                return value + "mm";
            case IMPERIAL:
                return value + "in";
            case METRIC:
                return value + "mm";
            default:
                return value + "ukn";
        }
    }

    public String getSpeedString(CityListItem item, String value){
        switch (item.getChosenUnitType()){
            case KELVIN:
                return value + "kph";
            case IMPERIAL:
                return value + "mph";
            case METRIC:
                return value + "kph";
            default:
                return value + "ukn";
        }
    }

    public void clearBookmarks(){
        list.clear();
        CityListItem[] arr = {null};
        addItems(arr);
    }

    public void setUnits(UnitType chosenUnitType){
        if(chosenUnitType != mChosenUnitType) {
            mChosenUnitType = chosenUnitType;
            for (CityListItem item : list) {
                if (item != null) {
                    item.setChosenUnitType(mChosenUnitType);
                    item.setState(ListItemState.UPDATED);
                    item.clearCityItem();
                }
            }

            addItems(list.toArray(new CityListItem[] {}));
        }
    }


    public String getWeatherApiKey() {
        return weatherApiKey;
    }

    public void setWeatherApiKey(String weatherApiKey) {
        this.weatherApiKey = weatherApiKey;
    }

}
