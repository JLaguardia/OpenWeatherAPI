package com.prismsoftworks.openweatherapitest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.prismsoftworks.openweatherapitest.MainActivity;
import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.service.CityListService;
import com.prismsoftworks.openweatherapitest.task.PullTask;

import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;

public class CityItemInfoAdapter implements GoogleMap.InfoWindowAdapter{
    private Set<CityListItem> mCities; //pull task to get this
    private Context mContext;
    private View mView;

    public CityItemInfoAdapter(Context context, Set<CityListItem> cities){
        this.mContext = context;
        this.mCities = cities;
        mView = LayoutInflater.from(mContext).inflate(R.layout.city_infowindow_layout, null);
    }

    private void populateInfoWindowList(LatLng coord){
        for(CityListItem saved : mCities){
            if(saved != null && saved.getCoordinates().equals(coord)) {
                ((TextView) mView.findViewById(R.id.lblInfoLocationName)).setText(
                        saved.getCityItem().getName());
                ((TextView) mView.findViewById(R.id.lblInfoLocationNickname)).setText(
                        saved.getName());
                ((TextView) mView.findViewById(R.id.lblInfoTemperature)).setText(
                        CityListService.getInstance().getTemperatureString(saved));
                String iconCode = saved.getCityItem().getWeather()[0].getIcon();
                Bitmap ico = CityListService.getInstance().getCachedIcon(iconCode);
                if(ico == null){
                    ico = PullTask.getInstance().getWeatherIconBitmap(iconCode);
                    CityListService.getInstance().registerIcon(iconCode, ico);
                }

                ((ImageView) mView.findViewById(R.id.imgInfoIcon)).setImageBitmap(ico);
                return;
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        populateInfoWindowList(marker.getPosition());
        return mView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        populateInfoWindowList(marker.getPosition());
        return mView;
    }
}
