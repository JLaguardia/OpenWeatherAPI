package com.prismsoftworks.openweatherapitest.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.prismsoftworks.openweatherapitest.adapter.CityListAdapter;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CityListService {
    private static CityListService instance;
    private static final String PREF_NAME = "prefs";
    private static final String CITIES_KEY = "storedCities";
    private List<CityListItem> list;
    private CityListAdapter adapter;
    private Context mContext;
    private SharedPreferences mPrefs;

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

    public List<CityListItem> getList() {
        return list;
    }

    public void addItems(CityListItem... items){
        list.addAll(Arrays.asList(items));
        getAdapter().notifyDataSetChanged();
    }

    public void bookmarkCity(CityListItem city){
        addItems(city);
        invalidatePreferences();
    }

    private void invalidatePreferences(){
        StringBuilder citiescsv = new StringBuilder();
        for(CityListItem item : list){
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
        getAdapter().notifyDataSetChanged();
        invalidatePreferences();
    }

    public CityListAdapter getAdapter() {
        if(adapter == null){
            adapter = new CityListAdapter();
            adapter.setItemList(list);
        }

        return adapter;
    }

//    public void bookmarkCity(CityListItem){
//
//    }


    public void setAdapter(CityListAdapter adapter) {
        this.adapter = adapter;
        this.adapter.setItemList(list);
    }
}
