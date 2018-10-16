package com.prismsoftworks.openweatherapitest.object;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.prismsoftworks.openweatherapitest.R;

public class CityViewHolder extends RecyclerView.ViewHolder{
    public TextView lblName;
    public TextView lblCurrent;
    public TextView lblInfo;
    public ImageButton btnDelete;
    public ImageView imgWeatherIcon;
    public ViewGroup container;

    public CityViewHolder(View itemView) {
        super(itemView);
        init(itemView);
    }

    private void init(View v){
        lblName = v.findViewById(R.id.lblCityLabel);
        lblCurrent = v.findViewById(R.id.lblCurrent);
        lblInfo = v.findViewById(R.id.lblInfo);
        btnDelete = v.findViewById(R.id.btnDelete);
        imgWeatherIcon = v.findViewById(R.id.imgWeatherIcon);
        container = v.findViewById(R.id.cityDetailContainer);
    }
}
