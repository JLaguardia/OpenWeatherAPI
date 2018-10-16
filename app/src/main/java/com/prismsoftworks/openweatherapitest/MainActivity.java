package com.prismsoftworks.openweatherapitest;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import com.prismsoftworks.openweatherapitest.adapter.CityItemInfoAdapter;
import com.prismsoftworks.openweatherapitest.fragments.MapFragment;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.service.CityListService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends FragmentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private SharedPreferences mPref;
    public static final String CITIES_KEY = "storedCities";
    private List<CityItem> infoWindowCities = new ArrayList<>();
    private Set<CityListItem> savedCities = new HashSet<>();
    private CityItemInfoAdapter mInfoAdapter = null;
    private FragmentManager mFragMan = null;


    //api key: eb6d211c0e99deef8bb87c94621ce704
    //api base: api.openweathermap.org/data/2.5/find?q={city}&units=imperial
    //api base: api.openweathermap.org/data/2.5/weather?lat={latitude}&lon={longitude}&units=imperial
    //icon: "http://openweathermap.org/img/w/{icon_id}.png"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.test_coordinator);
        ((LinearLayout)findViewById(R.id.mainContainer))
                .setOrientation(getResources().getConfiguration().orientation);
        mFragMan = getSupportFragmentManager();
        init();
    }



    private void init(){
        CityListService.getInstance().registerContext(this);
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

        Log.i(TAG, "saved cities: " + savedCities.size());

        RecyclerView savedRec = findViewById(R.id.markerRecycler);
        CityListItem[] arr = new CityListItem[savedCities.size()];
        arr = savedCities.toArray(arr);
        if(arr.length == 0){
            arr = new CityListItem[1];
            arr[0] = null;
        }
        CityListService.getInstance().addItems(arr);
        savedRec.setLayoutManager(new LinearLayoutManager(this));
        savedRec.setAdapter(CityListService.getInstance().getAdapter());

        //add map frag
        FragmentTransaction ft = mFragMan.beginTransaction();
        ft.replace(R.id.testFragContainer, new MapFragment().setCities(savedCities));
        ft.commit();
    }


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
