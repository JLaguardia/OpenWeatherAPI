package com.prismsoftworks.openweatherapitest.service;

import com.prismsoftworks.openweatherapitest.adapter.CityListAdapter;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CityListService {
    private static CityListService instance;
    private List<CityListItem> list;
    private CityListAdapter adapter;

    public static CityListService getInstance(){
        if(instance == null){
            instance = new CityListService();
        }

        return instance;
    }

    private CityListService(){
        list = new ArrayList<>();
    }

    public List<CityListItem> getList() {
        return list;
    }

    public void addItems(CityListItem... items){
        list.addAll(Arrays.asList(items));
        adapter.notifyDataSetChanged();
    }

    public CityListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(CityListAdapter adapter) {
        this.adapter = adapter;
        this.adapter.setItemList(list);
    }
}
