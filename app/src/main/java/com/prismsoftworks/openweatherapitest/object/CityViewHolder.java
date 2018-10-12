package com.prismsoftworks.openweatherapitest.object;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.prismsoftworks.openweatherapitest.R;

public class CityViewHolder extends RecyclerView.ViewHolder{
    public TextView lblName;
    public TextView lblInfo;

    public CityViewHolder(View itemView) {
        super(itemView);
        init(itemView);
    }

    private void init(View v){
        lblName = v.findViewById(R.id.lblCityLabel);
        lblInfo = v.findViewById(R.id.lblInfo);
    }
}
