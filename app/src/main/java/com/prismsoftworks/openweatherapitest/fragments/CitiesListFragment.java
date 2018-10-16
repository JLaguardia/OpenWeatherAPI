package com.prismsoftworks.openweatherapitest.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.prismsoftworks.openweatherapitest.MainActivity;
import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.adapter.CityListAdapter;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.model.list.ListItemState;

import java.util.ArrayList;
import java.util.List;

public class CitiesListFragment extends Fragment {
    private List<CityListItem> list = new ArrayList<>();
    private CityListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e("LIST FRAG", "CREATE VIEW LIST FRAG");
        populateItems(getArguments().getString(MainActivity.CITIES_KEY));
        View v = inflater.inflate(R.layout.list_fragment, container, false);
        RecyclerView recycler = v.findViewById(R.id.recyclerView);
        adapter = new CityListAdapter(list);
        recycler.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recycler.setAdapter(adapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void invalidateItems(CityListItem... cityListItems){
        for(CityListItem item : cityListItems){
            switch (item.getState()){
                case INSERTED:
                    list.add(item);
                    break;
                case DELETED:
                    list.remove(item);
                    break;
                case UPDATED://?
                    list.remove(item);
                    list.add(item);
                    default:
            }
            adapter.notifyDataSetChanged();//?
        }
    }

    private void populateItems(String citiesCsv){
        if(citiesCsv != null && !citiesCsv.equals("")){ // "Orlando,123.44,-100;Atlanta,345.11,-80"
            for(String city : citiesCsv.split(";")){
                String[] info = city.split(",");
                list.add(new CityListItem(info[0], new LatLng(Double.parseDouble(info[1]),
                        Double.parseDouble(info[2]))));
            }
        }
    }
}
