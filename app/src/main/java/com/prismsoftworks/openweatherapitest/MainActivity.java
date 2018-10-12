package com.prismsoftworks.openweatherapitest;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.Marker;
import com.prismsoftworks.openweatherapitest.adapter.CityItemInfoAdapter;
import com.prismsoftworks.openweatherapitest.fragments.CitiesListFragment;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private SharedPreferences mPref;
    public static final String CITIES_KEY = "storedCities";
    private List<CityItem> infoWindowCities = new ArrayList<>();
    private Set<LatLng> savedCoords = new HashSet<>();


    //api key: eb6d211c0e99deef8bb87c94621ce704
    //api base: api.openweathermap.org/data/2.5/find?q={city}&units=imperial
    //api base: api.openweathermap.org/data/2.5/weather?lat={latitude}&lon={longitude}&units=imperial
    //icon: "http://openweathermap.org/img/w/{icon_id}.png"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: handle orientation shift
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setOnMapClickListener(this);
        init();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .title(getResources().getString(R.string.new_pin_title));

        mMap.addMarker(marker);
    }

    private void init(){
        ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map))
                .getMapAsync(this);

        mPref = getSharedPreferences("prefs", MODE_PRIVATE);
        final String citiesCsv = mPref.getString(CITIES_KEY, "");
        if(!citiesCsv.equals("")){ // "Orlando,123.44,-100;Atlanta,345.11,-80"
            for(String city : citiesCsv.split(";")){
                String[] info = city.split(",");
                savedCoords.add(new LatLng(Double.parseDouble(info[1]),
                        Double.parseDouble(info[2])));
            }
        }

        FloatingActionButton vbtn = findViewById(R.id.btnAction);
        vbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString(CITIES_KEY, citiesCsv);
                CitiesListFragment frag = new CitiesListFragment();
                frag.setArguments(args);
                findViewById(R.id.viewPager).setVisibility(View.VISIBLE);
                FragmentTransaction fragTrn = getFragmentManager().beginTransaction();
                fragTrn.replace(R.id.viewPager, frag);
                fragTrn.show(frag);//getFragmentManager().findFragmentById(LIST_FRAG_ID));
                fragTrn.addToBackStack(null);
                fragTrn.commit();
            }
        });

        initMap();
    }


    private void initMap(){

         infoWindowCities
        CityItemInfoAdapter infoAdapter = new CityItemInfoAdapter()
        mMap.setInfoWindowAdapter(infoAdapter);
        final Context context = this;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                LatLng coords = marker.getPosition();
                for(LatLng savedCoords : savedCoords){
                    if(savedCoords.latitude == coords.latitude &&
                            savedCoords.longitude == coords.longitude ){
                        marker.showInfoWindow();
                        //TODO: open cities frag for this location.
                        return true;
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getResources().getString(R.string.new_pin_title));
                FrameLayout container = new FrameLayout(context);
                container.setPadding(8, 0, 8, 0);
                final EditText input = new EditText(context);
                input.setHint("Enter Nickname to save for this pin");
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                container.addView(input);
                builder.setView(container);
                builder.setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                savedCoords.add(marker.getPosition());
                                new CityListItem(input.getText().toString(), marker.getPosition());
                            }
                        });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int i) {
                        dlg.cancel();
                    }
                });

                builder.show();
                return false;
            }
        });
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
