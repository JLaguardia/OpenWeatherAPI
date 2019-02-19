package com.prismsoftworks.openweatherapitest.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prismsoftworks.openweatherapitest.MainActivity;
import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.adapter.ForecastAdapter;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;
import com.prismsoftworks.openweatherapitest.model.city.WrapperObj;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.service.CityListService;
import com.prismsoftworks.openweatherapitest.task.PullTask;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CityDetailFragment extends Fragment {
    private CityListItem cityItem;
    private String BUNDLE_KEY = "cityItem";

    public CityDetailFragment setCityItemFromListItem(CityListItem item){
        cityItem = item;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null){
            cityItem = (CityListItem) savedInstanceState.getSerializable(BUNDLE_KEY);
        }

        final View v = inflater.inflate(R.layout.city_details_fragment, container, false);
        mapValues(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(BUNDLE_KEY, cityItem);
        super.onSaveInstanceState(outState);
    }

    public CityListItem getCityItem(){
        return cityItem;
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
        temp.setText(CityListService.getInstance().getTemperatureString(cityItem));
        humid.setText(String.valueOf(cityItem.getCityItem().getTemperature().getHumidity()));
        rainLvl.setText(CityListService.getInstance().getLengthMeasureString(cityItem,
                cityItem.getCityItem().getRain().getThreeHour()));
        clouds.setText(String.valueOf(cityItem.getCityItem().getClouds().getCloudLevel()));
        windSpd.setText(CityListService.getInstance().getSpeedString(cityItem, String.valueOf(
                cityItem.getCityItem().getWind().getSpeed())));
        windDir.setText(cityItem.getCityItem().getWind().getDeg() + "°");

        if(cityItem.getCityItem().getForecast() == null || cityItem.getCityItem().getForecast().length == 0) {
            try {
                pullForecastData(v);
            } catch(Exception ex){
                ((MainActivity)getContext()).displayNetworkError(cityItem);
                cityItem.getCityItem().setForecast(new CityItem[]{null});
            }
        } else {
            if (cityItem.getCityItem().getForecast().length > 0) {
                setupRecycler(v);
            }
        }

//        if(replaceView && getView() != null) {
//            ViewGroup parent = (ViewGroup) getView();
//            parent.removeAllViews();
//            parent.addView(v);
//        }
    }

    private void setupRecycler(View parent){
        Log.i("detail frag", "setting up recycler");
        RecyclerView rec = parent.findViewById(R.id.forecastRecycler);
        rec.setHasFixedSize(true);
        ForecastAdapter adapter = new ForecastAdapter(cityItem);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        rec.setLayoutManager(llm);
        rec.setAdapter(adapter);
    }

    private void pullForecastData(final View v){
//        String json = PullTask.getInstance().getForecastCityJson(cityItem.getCoordinates(), cityItem.getChosenUnitType());
        Observable.just(cityItem).map(new Func1<CityListItem, WrapperObj>() {
            @Override
            public WrapperObj call(CityListItem item) {
                String json = PullTask.getInstance().getForecastCityJson(item.getCoordinates(),
                        item.getChosenUnitType());
                Gson gson = new GsonBuilder().create();
                return gson.fromJson(json, WrapperObj.class);
            }
        })
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WrapperObj>() {
                    @Override
                    public void onCompleted() {
                        Log.i("Detail Frag", "dl complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Detail Frag", e.getMessage());
                        ((MainActivity)getContext()).displayNetworkError(cityItem);
                        cityItem.getCityItem().setForecast(new CityItem[]{null});
                    }

                    @Override
                    public void onNext(WrapperObj blob) {
                        List<CityItem> list = new ArrayList<>();
                        list.add(blob.list[0]);
                        long millisInDay = 86400 * 1000;
                        long prevDate = new Date(Long.parseLong(list.get(0).getDate()) * 1000).getTime();
                        for(CityItem item : blob.list){
                            Date dateObj = new Date(Long.parseLong(item.getDate()) * 1000);
                            if((list.indexOf(item) == 0) || (dateObj.getTime() >= prevDate + millisInDay)) {
                                prevDate = dateObj.getTime();
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(dateObj);
                                int weekday = cal.get(Calendar.DAY_OF_WEEK);
                                int month = cal.get(Calendar.MONTH);
                                int day = cal.get(Calendar.DAY_OF_MONTH);
                                int year = cal.get(Calendar.YEAR);
                                int hr = cal.get(Calendar.HOUR);
                                int min = cal.get(Calendar.MINUTE);
                                String dateStr = getDateString(weekday, month, day, year) + " - " +
                                        (hr < 10 ? "0" + hr : hr) + ":" + (min < 10 ? "0" + min : min);
                                item.setDateStr(dateStr);
                                if(list.indexOf(item) < 0) {
                                    list.add(item);
                                }
                            }
                        }

                        cityItem.getCityItem().setForecast(list.toArray(new CityItem[]{}));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupRecycler(v);
                            }
                        });
                    }
                });

//        Gson gson = new GsonBuilder().create();
//        WrapperObj blob = gson.fromJson(json, WrapperObj.class);
//        List<CityItem> list = new ArrayList<>();
//        list.add(blob.list[0]);
//        long millisInDay = 86400 * 1000;
//        long prevDate = new Date(Long.parseLong(list.get(0).getDate()) * 1000).getTime();
//        for(CityItem item : blob.list){
//            Date dateObj = new Date(Long.parseLong(item.getDate()) * 1000);
//            if((list.indexOf(item) == 0) || (dateObj.getTime() >= prevDate + millisInDay)) {
//                prevDate = dateObj.getTime();
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(dateObj);
//                int weekday = cal.get(Calendar.DAY_OF_WEEK);
//                int month = cal.get(Calendar.MONTH);
//                int day = cal.get(Calendar.DAY_OF_MONTH);
//                int year = cal.get(Calendar.YEAR);
//                int hr = cal.get(Calendar.HOUR);
//                int min = cal.get(Calendar.MINUTE);
//                String dateStr = getDateString(weekday, month, day, year) + " - " +
//                        (hr < 10 ? "0" + hr : hr) + ":" + (min < 10 ? "0" + min : min);
//                item.setDateStr(dateStr);
//                if(list.indexOf(item) < 0) {
//                    list.add(item);
//                }
//            }
//        }
//
//        cityItem.getCityItem().setForecast(list.toArray(new CityItem[]{}));
    }

    private String getDateString(int weekday, int month, int day, int year){
        String wkd;
        String mnt;
        switch (weekday){
            case 1:
                wkd = "Sunday";
                break;
            case 2:
                wkd = "Monday";
                break;
            case 3:
                wkd = "Tuesday";
                break;
            case 4:
                wkd = "Wednesday";
                break;
            case 5:
                wkd = "Thursday";
                break;
            case 6:
                wkd = "Friday";
                break;
            default:
                wkd = "Saturday";
                break;
        }

        switch (month){
            case 0:
                mnt = "January";
                break;
            case 1:
                mnt = "February";
                break;
            case 2:
                mnt = "March";
                break;
            case 3:
                mnt = "April";
                break;
            case 4:
                mnt = "May";
                break;
            case 5:
                mnt = "June";
                break;
            case 6:
                mnt = "July";
                break;
            case 7:
                mnt = "August";
                break;
            case 8:
                mnt = "September";
                break;
            case 9:
                mnt = "October";
                break;
            case 10:
                mnt = "November";
                break;
            default:
                mnt = "December";
                break;
        }

        return wkd + " " + mnt + " " + day + ", " + year;
    }
}
