package com.prismsoftworks.openweatherapitest.service;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.prismsoftworks.openweatherapitest.model.city.CityItem;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;

public class WeatherDoubleTapListener extends GestureDetector.SimpleOnGestureListener {

    //todo: this doesnt work - couldn't figure it out in time
    private CityListItem city;
    private long prevClick = -1;
    private final long DOUBLE_CLICK_THRESHOLD = 200;

    public WeatherDoubleTapListener(CityListItem item){
        this.city = item;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Log.e("DOWN", "DOWN EVENT");
//        CityListService.getInstance().showDetails(city);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        if(prevClick == -1){
            prevClick = System.currentTimeMillis();
        }

        long curClick = System.currentTimeMillis();
        if(curClick - prevClick >= DOUBLE_CLICK_THRESHOLD){
            onDoubleTap(motionEvent);
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        Log.e("singleTapUp", "single tap up");

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        Log.e("SINGLE TAP", "single tap deteccted");
        CityListService.getInstance().showDetails(city);
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        Log.e("double TAP", "double tap deteccted");
        CityListService.getInstance().moveCamera(city.getCoordinates());
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        Log.e("double TAPevent", "double tapEvent deteccted");

        return false;
    }
}
