package com.prismsoftworks.openweatherapitest.object;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.task.PullTask;
import com.prismsoftworks.openweatherapitest.task.TaskCallback;

public class CityViewHolder extends RecyclerView.ViewHolder implements TaskCallback {
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
