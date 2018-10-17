package com.prismsoftworks.openweatherapitest;

import android.content.SharedPreferences;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import com.prismsoftworks.openweatherapitest.adapter.CityItemInfoAdapter;
import com.prismsoftworks.openweatherapitest.fragments.CityDetailFragment;
import com.prismsoftworks.openweatherapitest.fragments.MapFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.service.CityListService;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends FragmentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences mPref;
    public static final String CITIES_KEY = "storedCities";
    private Set<CityListItem> savedCities = new HashSet<>();
    private FragmentManager mFragMan = null;
    private MapFragment mMapFragment;
    private CityDetailFragment mCityDetailFragment;
    private boolean appInit = false;
    private final String MAP_FRAG_KEY = "mapFrag";
    private final String DETAIL_FRAG_KEY = "detailFrag";


    //api key: eb6d211c0e99deef8bb87c94621ce704
    //api base: api.openweathermap.org/data/2.5/find?q={city}&units=imperial
    //api base: api.openweathermap.org/data/2.5/weather?lat={latitude}&lon={longitude}&units=imperial
    //icon: "http://openweathermap.org/img/w/{icon_id}.png"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragMan = getSupportFragmentManager();
        if(savedInstanceState != null){
            mCityDetailFragment = (CityDetailFragment) mFragMan.getFragment(savedInstanceState, DETAIL_FRAG_KEY);
            CityListService.getInstance().registerContext(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CityListService.getInstance().clearContext();
        setContentView(R.layout.activity_main);
        CityListService.getInstance().registerContext(this);
        ((LinearLayout)findViewById(R.id.mainContainer)).setOrientation(getResources()
                                                                .getConfiguration().orientation);
        mFragMan = getSupportFragmentManager();
        init();
    }

    private void init(){
        mPref = getSharedPreferences("prefs", MODE_PRIVATE);
        final String citiesCsv = mPref.getString(CITIES_KEY, "");
        if(!citiesCsv.equals("")){ // "Orlando,123.44,-100;Atlanta,345.11,-80"
            for(String city : citiesCsv.split(";")){
                String[] info = city.split(",");
                LatLng coor = new LatLng(Double.parseDouble(info[1]),
                        Double.parseDouble(info[2]));
                savedCities.add(new CityListItem(info[0], coor));
            }
        }

        RecyclerView savedRec = findViewById(R.id.markerRecycler);
        CityListItem[] arr = new CityListItem[savedCities.size()];
        arr = savedCities.toArray(arr);
        if(arr.length == 0){
            arr = new CityListItem[1];
            arr[0] = null;
        }

        //add map frag
        mMapFragment = new MapFragment().setCities(savedCities);

        if(mCityDetailFragment != null && mCityDetailFragment.isAdded()){
            setBarWeight(0);
            mCityDetailFragment = new CityDetailFragment().setCityItemFromListItem(
                    mCityDetailFragment.getCityItem());
            replaceFrag(mCityDetailFragment);
        } else {
            setBarWeight(1);
            replaceFrag(mMapFragment);
        }

        CityListService.getInstance().addItems(arr);
        savedRec.setLayoutManager(new LinearLayoutManager(this));
        savedRec.setAdapter(CityListService.getInstance().getAdapter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        CityListService.getInstance().clearContext();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mCityDetailFragment != null && mCityDetailFragment.isAdded()){
            mFragMan.putFragment(outState, DETAIL_FRAG_KEY, mCityDetailFragment);
        }

    }

    public void replaceFrag(Fragment frag){
        FragmentTransaction ft = mFragMan.beginTransaction();
        ft.replace(R.id.testFragContainer, frag, "test");
        if(appInit) {
            ft.addToBackStack(null);
        }

        ft.commit();
        appInit = true;
    }

    private void setBarWeight(int weight){
        AppBarLayout bar = findViewById(R.id.main_appbar);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bar.getLayoutParams();
        lp.weight = weight;
        bar.setLayoutParams(lp);
    }

    @Override
    public void onBackPressed() {
        if(mFragMan.getBackStackEntryCount() > 0) {
            if(mMapFragment.isAdded()){
                setBarWeight(0);
            } else {
                setBarWeight(1);
            }

            mFragMan.popBackStackImmediate();
            Log.e(TAG, "backstack popped");
        } else {
            super.onBackPressed();
        }
    }

    public void showCityDetails(CityListItem city){
        if(mCityDetailFragment == null){
            mCityDetailFragment = new CityDetailFragment();
        }

        mCityDetailFragment.setCityItemFromListItem(city);
        setBarWeight(0);
        replaceFrag(mCityDetailFragment);
    }

    public void showMapScreen(){
        if(mMapFragment == null){
            mMapFragment = new MapFragment().setCities(savedCities);
        }

        setBarWeight(1);
        replaceFrag(mMapFragment);
    }

    public void clearRecycler(){
        RecyclerView rec = findViewById(R.id.markerRecycler);
        rec.removeAllViewsInLayout();
        rec.setAdapter(null);
        rec.setAdapter(CityListService.getInstance().getAdapter());
    }

//    public boolean onTouchEvent(MotionEvent me){
//        return true;
//    }

    /**
     * sample:
     // https://samples.openweathermap.org/data/2.5/weather?lat=28.67&lon=-81.42&appid=b6907d289e10d714a6e88b30761fae22

     {
         "coord": {
             "lon": 139.01,
             "lat": 35.02
     },
         "weather": [
         {
             "id": 800,
             "main": "Clear",
             "description": "clear sky",
             "icon": "01n"
         }
     ],
         "base": "stations",
         "main": {
             "temp": 285.514,
             "pressure": 1013.75,
             "humidity": 100,
             "temp_min": 285.514,
             "temp_max": 285.514,
             "sea_level": 1023.22,
             "grnd_level": 1013.75
             },
         "wind": {
             "speed": 5.52,
             "deg": 311
             },
     "clouds": {
     "all": 0
     },
     "dt": 1485792967,
     "sys": {
     "message": 0.0025,
     "country": "JP",
     "sunrise": 1485726240,
     "sunset": 1485763863
     },
     "id": 1907296,
     "name": "Tawarano",
     "cod": 200
     }
     */


}
