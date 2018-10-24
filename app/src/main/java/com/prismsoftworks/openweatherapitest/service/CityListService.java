package com.prismsoftworks.openweatherapitest.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prismsoftworks.openweatherapitest.MainActivity;
import com.prismsoftworks.openweatherapitest.adapter.CityListAdapter;
import com.prismsoftworks.openweatherapitest.fragments.MapFragment;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;
import com.prismsoftworks.openweatherapitest.model.city.UnitType;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.model.list.ListItemState;
import com.prismsoftworks.openweatherapitest.task.PullTask;
import com.prismsoftworks.openweatherapitest.task.TaskCallback;

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
    private int sentinel = 0;


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

        if ((list.size() == 0) || (list.size() == 1 && list.get(0) == null)) {
            if (list.size() == 0) {
                list.add(null);
            }

            mAdapter = null;
            ((MainActivity) mContext).clearRecycler();
        }

        if (mMapFrag != null) {
            ((MainActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMapFrag.refreshMap(list.get(list.size() - 1));
                }
            });
        }

        boolean nullFound = false;
        for (CityListItem city : list) {
            if (city != null) {
                city.setState(ListItemState.LOADED);
                if(city.getCityItem() == null){
                    nullFound = true;
                    Set<LatLng> coords = new HashSet<>();
                    coords.add(city.getCoordinates());
                    PullTask.getInstance().getWeatherCityJson(coords, mChosenUnitType, itemCallback(city));
                }
            }
        }

        if(!nullFound) {
            refreshAdapterOnUi();
        }

        invalidatePreferences();
    }

    private void refreshAdapterOnUi(){
        if(mContext == null){
            return;
        }

        ((MainActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)mContext).clearRecycler();
                getAdapter().updateActiveList();
                getAdapter().notifyItemRangeChanged(0, list.size());
                getAdapter().notifyDataSetChanged();
            }
        });
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

            citiescsv.append(escapifyStr(item.getName()));
            citiescsv.append(",");
            citiescsv.append(String.valueOf(item.getCoordinates().latitude));
            citiescsv.append(",");
            citiescsv.append(String.valueOf(item.getCoordinates().longitude));
        }

        mPrefs.edit().putString(MainActivity.CITIES_KEY, citiescsv.toString()).apply();
        mPrefs.edit().putString(MainActivity.UNITS_KEY, mChosenUnitType.name()).apply();
    }

    private String escapifyStr(String str){
        StringBuilder res = new StringBuilder();
        for(char c : str.toCharArray()){
            if(((int)c) == 44 || ((int)c) == 59){
                res.append("\\");
            }
            res.append(c);
        }

        return res.toString();
    }

    public CityListAdapter getAdapter() {
        if(mAdapter == null){
            mAdapter = new CityListAdapter();
            mAdapter.setItemList(list);
        }

        return mAdapter;
    }

    public Bitmap getCachedIcon(String key, TaskCallback cb){
        sentinel++;
        if(sentinel > 100){
            sentinel = 0;
            Log.e("god class", "time out, returning null");
            return null;
        }
        Bitmap res =  cachedIcons.get(key);
        if(res == null && !cachedIcons.containsKey(key)){
            cachedIcons.put(key, null);
            PullTask.getInstance().getWeatherIconBitmap(key, bmpCallback(key, cb));
        } else if(res != null){
            return res;
        }

        return getCachedIcon(key, cb);
    }

    private TaskCallback itemCallback(final CityListItem item){
        return new TaskCallback() {
            @Override
            public void callback(Bundle response) {
                String json = response.getString(PullTask.JSON_KEY);
                Gson gson = new GsonBuilder().create();
                item.setCityItem(gson.fromJson(json, CityItem.class));
                refreshAdapterOnUi();
            }
        };
    }

    private TaskCallback bmpCallback(final String key, final TaskCallback cb){
        return new TaskCallback() {
            @Override
            public void callback(Bundle response) {
                byte[] bytes = response.getByteArray(PullTask.IMG_KEY);
                if(bytes != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if(bmp != null){
                        cachedIcons.put(key, bmp);
                        cb.callback(response);
                    }
                }
            }
        };
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
