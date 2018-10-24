package com.prismsoftworks.openweatherapitest.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.object.ForecastViewHolder;
import com.prismsoftworks.openweatherapitest.service.CityListService;
import com.prismsoftworks.openweatherapitest.task.PullTask;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastViewHolder> {
    private List<CityItem> items;
    private CityListItem item;

    public ForecastAdapter(CityListItem parent){
        item = parent;
        this.items = Arrays.asList(parent.getCityItem().getForecast());
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item,
                parent, false);
        return new ForecastViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        final CityItem cityItem = items.get(position);
        if(cityItem == null){
            return;
        }

        holder.progBar.setVisibility(View.GONE);
        holder.container.setVisibility(View.VISIBLE);

        holder.lblTemp.setText(CityListService.getInstance().getTemperatureString(item));
        holder.lblHumid.setText(String.valueOf(cityItem.getTemperature().getHumidity()));
        holder.lblRain.setText(CityListService.getInstance().getLengthMeasureString(item,
                cityItem.getRain().getThreeHour()));
        holder.lblWindSpeed.setText(CityListService.getInstance().getSpeedString(item,
                String.valueOf(cityItem.getWind().getSpeed())));
        String windDir = cityItem.getWind().getDeg() + "°";
        holder.lblWindDir.setText(windDir);
        String iconCode = cityItem.getWeather()[0].getIcon();
        Bitmap bmpIcon = CityListService.getInstance().getCachedIcon(iconCode, holder);

        holder.imgWeatherIcon.setImageBitmap(bmpIcon);
        holder.lblDate.setText(cityItem.getDateStr());
    }

    public void setCityItem(CityListItem item){
        this.item = item;
        this.items = Arrays.asList(item.getCityItem().getForecast());
    }
}
