package com.prismsoftworks.openweatherapitest.object;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.task.PullTask;
import com.prismsoftworks.openweatherapitest.task.TaskCallback;

public class ForecastViewHolder extends RecyclerView.ViewHolder implements TaskCallback{
    public TextView lblDate;
    public TextView lblTemp;
    public TextView lblHumid;
    public TextView lblRain;
    public TextView lblWindDir;
    public TextView lblWindSpeed;
    public ImageView imgWeatherIcon;
    public ProgressBar progBar;
    public ViewGroup container;

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
        progBar = v.findViewById(R.id.forecastLoading);
        container = v.findViewById(R.id.forecastContainer);
    }

    @Override
    public void callback(Bundle response) {
        byte[] bytes = response.getByteArray(PullTask.IMG_KEY);
        if(bytes != null) {
            final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bmp != null) {
                ((Activity)imgWeatherIcon.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgWeatherIcon.setImageBitmap(bmp);
                    }
                });
            }
        }
    }
}
