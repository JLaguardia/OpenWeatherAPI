package com.prismsoftworks.openweatherapitest.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.object.CityViewHolder;

import java.util.List;

public class CityListAdapter extends RecyclerView.Adapter<CityViewHolder>{
    private List<CityListItem> items;

    public CityListAdapter(){
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        final CityListItem item = items.get(position);
        holder.lblName.setText(item.getName());
        holder.lblInfo.setText(item.getCurrentWeather().getDescription());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItemList(List<CityListItem> list){
        this.items = list;
        notifyDataSetChanged();
    }
}
