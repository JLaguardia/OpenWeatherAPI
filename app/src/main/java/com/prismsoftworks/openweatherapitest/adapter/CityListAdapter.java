package com.prismsoftworks.openweatherapitest.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.model.city.Weather;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.object.CityViewHolder;
import com.prismsoftworks.openweatherapitest.service.CityListService;
import com.prismsoftworks.openweatherapitest.task.PullTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityListAdapter extends RecyclerView.Adapter<CityViewHolder>{
    private List<CityListItem> items;
    private Map<String, Bitmap> icons = new HashMap<>();

    public CityListAdapter(List<CityListItem> items) {
        this.items = items;
    }

    public CityListAdapter(){
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_details_listitem,
                parent, false);
        return new CityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        final CityListItem item = items.get(position);
        if(item == null){
            return;
        }

        holder.lblName.setText(item.getName());
        holder.lblCurrent.setText(item.getCityItem().getTemperature().getTemperature() + "°F");
        Weather weather = item.getCityItem().getWeather()[0];
        holder.lblInfo.setText(weather.getLabel() + " : " +  weather.getDescription());
        String iconCode = item.getCityItem().getWeather()[0].getIcon();
        Bitmap bmpIcon = icons.get(iconCode);
        if(bmpIcon == null){
            PullTask.getInstance().addImageView(holder.getItemId(), holder.imgWeatherIcon);
            bmpIcon = PullTask.getInstance().getWeatherIconBitmap(holder.getItemId(), iconCode);
            icons.put(iconCode, bmpIcon);
        }

        holder.imgWeatherIcon.setImageBitmap(bmpIcon);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources res = view.getResources();
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(res.getString(R.string.delete_confirm));
                LinearLayout container = new LinearLayout(view.getContext());
                container.setPadding(12, 0, 12, 0);
                TextView tv = new TextView(view.getContext());
                tv.setGravity(Gravity.CENTER);
                tv.setText(res.getText(R.string.delete_message));
                container.addView(tv);
                builder.setView(container);
                builder.setPositiveButton(res.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CityListService.getInstance().deleteCity(item);
                            }
                        });

                builder.setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int i) {
                        dlg.cancel();
                    }
                });

                builder.show();
            }
        });

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
