package com.prismsoftworks.openweatherapitest.object;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.prismsoftworks.openweatherapitest.R;

public class ForecastViewHolder extends RecyclerView.ViewHolder{
    public TextView lblDate;
    public TextView lblTemp;
    public TextView lblHumid;
    public TextView lblRain;
    public TextView lblWindDir;
    public TextView lblWindSpeed;
    public ImageView imgWeatherIcon;

    public ForecastViewHolder(View itemView) {
        super(itemView);
        init(itemView);
    }

    private void init(View v){
        lblDate = v.findViewById(R.id.lblDateStr);
        lblTemp = v.findViewById(R.id.lblForecastTemp);
        lblHumid = v.findViewById(R.id.lblForecastHumidity);
        lblRain = v.findViewById(R.id.lblForecastRain);
        lblWindDir = v.findViewById(R.id.lblForecastWindDir);
        lblWindSpeed = v.findViewById(R.id.lblForecastWindSpeed);
        imgWeatherIcon = v.findViewById(R.id.forecastIcon);
    }
}
