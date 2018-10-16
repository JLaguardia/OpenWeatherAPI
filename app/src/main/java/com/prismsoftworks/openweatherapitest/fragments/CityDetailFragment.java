package com.prismsoftworks.openweatherapitest.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;

public class CityDetailFragment extends Fragment {

    private CityListItem cityItem;

    public CityDetailFragment setCityItemFromListItem(CityListItem item){
        cityItem = item;

        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.city_details_fragment, container, false);
        mapValues(v);
        return v;
    }

    private void mapValues(View v){
        CollapsingToolbarLayout tbl = v.findViewById(R.id.main_collapsing);
        TextView cityLabel = v.findViewById(R.id.lblCityName);
        TextView temp = v.findViewById(R.id.tv_temperature);
        TextView humid = v.findViewById(R.id.tv_humidity);
        TextView rainLvl = v.findViewById(R.id.tv_rain_level);
        TextView clouds = v.findViewById(R.id.tv_cloudiness);
        TextView windSpd = v.findViewById(R.id.tv_wind_speed);
        TextView windDir = v.findViewById(R.id.tv_wind_direction);
        tbl.setTitle(cityItem.getName());
        cityLabel.setText(cityItem.getCityItem().getName());
        temp.setText(String.valueOf(cityItem.getCityItem().getTemperature().getTemperature()));
        humid.setText(String.valueOf(cityItem.getCityItem().getTemperature().getHumidity()));
        rainLvl.setText(String.valueOf(cityItem.getCityItem().getRain().getThreeHour()));
        clouds.setText(String.valueOf(cityItem.getCityItem().getClouds().getCloudLevel()));
        windSpd.setText(String.valueOf(cityItem.getCityItem().getWind().getSpeed()));
        windDir.setText(String.valueOf(cityItem.getCityItem().getWind().getDeg()));


    }
}
