package com.prismsoftworks.openweatherapitest.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
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
import com.prismsoftworks.openweatherapitest.MainActivity;
import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.adapter.CityItemInfoAdapter;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.model.list.ListItemState;
import com.prismsoftworks.openweatherapitest.service.CityListService;

import java.util.HashSet;
import java.util.Set;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private static final String TAG = MapFragment.class.getSimpleName();
    private GoogleMap mMap;
    private Set<CityListItem> mSavedCities = new HashSet<>();
    private Marker mCurrentMarker = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CityListService.getInstance().registerMapFragment(this);
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
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
        initMap(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mCurrentMarker == null) {
            String title = getResources().getString(R.string.new_pin_title);
            CityListItem item = new CityListItem(title, latLng);
            item.setName(item.getCityItem().getName());
            if(item.getCityItem().getCoordinates() == null){
                ((MainActivity)getActivity()).displayNetworkError();
                return;
            }

            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(latLng.latitude, latLng.longitude))
                    .title(item.getName());
            mMap.addMarker(marker);
            mSavedCities = CityListService.getInstance().bookmarkCity(item);
        } else {
            mCurrentMarker.hideInfoWindow();
            mCurrentMarker = null;
        }
    }

    public void moveCamera(LatLng coord) {
//        ((MainActivity)getContext()).handleSearchFocus();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coord));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    public void refreshMap(CityListItem focusCity) {
        mMap.clear();
        mSavedCities.clear();
        initMap(false);
        if (focusCity != null) {
            moveCamera(focusCity.getCoordinates());
        }
    }

    private void initMap(boolean moveCamera) {
        mSavedCities = CityListService.getInstance().getCities();
        CityItemInfoAdapter infoAdapter = new CityItemInfoAdapter(getContext(), mSavedCities);
        mMap.setInfoWindowAdapter(infoAdapter);
        final Context context = getContext();

        for (CityListItem city : mSavedCities) {
            if (city != null) {
                mMap.addMarker(new MarkerOptions().title(city.getName()).position(city.getCoordinates()));
            }
        }

        if (moveCamera) {
            if (mSavedCities.size() > 0) {
                CityListItem firstCity = mSavedCities.iterator().next();
                if (firstCity != null) {
                    moveCamera(firstCity.getCoordinates());
                }
            }
        }

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if(mCurrentMarker == null) {
                    LatLng coords = marker.getPosition();
                    for (CityListItem city : mSavedCities) {
                        if (coords.equals(city.getCoordinates())) {
                            marker.showInfoWindow();
                            mCurrentMarker = marker;
                            return false;
                        }
                    }
                } else {
                    mCurrentMarker.hideInfoWindow();
                    mCurrentMarker = null;
                }
                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                final CityListItem[] chosenCity = new CityListItem[] {null};
                for(CityListItem city : mSavedCities){
                    if(marker.getPosition().equals(city.getCoordinates())){
                        chosenCity[0] = city;
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getResources().getString(R.string.new_pin_title));
                FrameLayout container = new FrameLayout(context);
                container.setPadding(12, 0, 12, 0);
                final EditText input = new EditText(context);
                input.setHint(chosenCity[0].getName());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                container.addView(input);
                builder.setView(container);
                builder.setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                marker.setTitle(input.getText().toString());
                                marker.showInfoWindow();
                                chosenCity[0].setName(input.getText().toString());
                                chosenCity[0].setState(ListItemState.UPDATED);

                                CityListService.getInstance().bookmarkCity(chosenCity[0]);
                            }
                        });

                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int i) {
                        dlg.cancel();
                    }
                });

                builder.show();
            }
        });

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                for(CityListItem city : mSavedCities){
                    if(marker.getPosition().equals(city.getCoordinates())){
                        CityListService.getInstance().showDetails(city);
                        break;
                    }
                }
            }
        });
    }
}
