package com.prismsoftworks.openweatherapitest.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.adapter.CityItemInfoAdapter;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.service.CityListService;

import java.util.HashSet;
import java.util.Set;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener{
    private static final String TAG = MapFragment.class.getSimpleName();
    private GoogleMap mMap;
    private Set<LatLng> mSavedCoords = new HashSet<>();
    private Set<CityListItem> mSavedCities = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        //do something with v if necessary
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        initMap();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .title(getResources().getString(R.string.new_pin_title));

        mMap.addMarker(marker);
    }

    public MapFragment setCoords(Set<LatLng> coords){
        this.mSavedCoords = coords;
        return this;
    }

    public MapFragment setCities(Set<CityListItem> cities){
        this.mSavedCities = cities;
        return this;
    }

    private void initMap(){
//         infoWindowCities
//        CityItemInfoAdapter infoAdapter = new CityItemInfoAdapter(this);
//        mMap.setInfoWindowAdapter(infoAdapter);
        final Context context = getContext();

        for(CityListItem city : mSavedCities){
            mMap.addMarker(new MarkerOptions().title(city.getName()).position(city.getCoordinates()));
        }

        if(mSavedCities.size() > 0){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mSavedCities.iterator().next().getCoordinates()));
        }


        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                LatLng coords = marker.getPosition();
                for(LatLng savedCoords : mSavedCoords){
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
                container.setPadding(12, 0, 12, 0);
                final EditText input = new EditText(context);
                input.setHint("Enter Nickname to save for this pin");
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                container.addView(input);
                builder.setView(container);
                builder.setPositiveButton(getResources().getString(R.string.bookmark),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                marker.setTitle(input.getText().toString());
                                mSavedCoords.add(marker.getPosition());
                                marker.showInfoWindow();
                                CityListItem item = new CityListItem(input.getText().toString(),
                                        marker.getPosition());
                                CityListService.getInstance().bookmarkCity(item);
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
}
