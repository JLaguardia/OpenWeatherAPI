package com.prismsoftworks.openweatherapitest.object;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.prismsoftworks.openweatherapitest.R;

public class CityViewHolder extends RecyclerView.ViewHolder{
    public int itemIndex;
    public TextView lblName;
    public TextView lblCurrent;
    public TextView lblInfo;
    public ImageView imgWeatherIcon;
    public ViewGroup container;
    public ViewGroup itemBg;
    public ViewGroup itemFg;

    public CityViewHolder(View itemView) {
        super(itemView);
        init(itemView);
    }

    private void init(View v){
        container = v.findViewById(R.id.cityDetailContainer);
        itemBg = v.findViewById(R.id.itemBg);
        itemFg = v.findViewById(R.id.itemFg);
        lblName = v.findViewById(R.id.lblCityLabel);
        lblCurrent = v.findViewById(R.id.lblCurrent);
        lblInfo = v.findViewById(R.id.lblInfo);
        imgWeatherIcon = v.findViewById(R.id.imgWeatherIcon);
    }
}
