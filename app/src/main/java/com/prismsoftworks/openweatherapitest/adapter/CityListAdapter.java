package com.prismsoftworks.openweatherapitest.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.model.city.Weather;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.object.CityViewHolder;
import com.prismsoftworks.openweatherapitest.service.CityListService;
import com.prismsoftworks.openweatherapitest.task.PullTask;

import java.util.List;

public class CityListAdapter extends RecyclerView.Adapter<CityViewHolder> {
    private List<CityListItem> items;

    public CityListAdapter(){ }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_details_listitem,
                parent, false);
        return new CityViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        final CityListItem item = items.get(position);
        if(item == null){
            return;
        }

        holder.itemBg.setVisibility(View.VISIBLE);
        holder.itemIndex = holder.getAdapterPosition();
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CityListService.getInstance().showDetails(item);
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CityListService.getInstance().moveCamera(item.getCoordinates());
                return true;
            }
        });

        String name = holder.lblName.getContext().getResources().getString(R.string.city_noname_label);
        if(item.getName() != null && !item.getName().equals("")){
            name = item.getName();
        }

        holder.lblName.setText(name);
        holder.lblName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Context context = view.getContext();
                final Resources res = context.getResources();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(res.getString(R.string.city_rename_label));
                FrameLayout container = new FrameLayout(context);
                container.setPadding(8, 0, 8, 0);
                final EditText input = new EditText(context);
                String inputHint;

                if(item.getName() == null || item.getName().equals("")){
                    inputHint = res.getString(R.string.city_rename_hint);
                } else {
                    inputHint = item.getName();
                }

                input.setHint(inputHint);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                container.addView(input);
                builder.setView(container);

                builder.setPositiveButton(res.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String chosenName = input.getText().toString();
                        item.setName(chosenName);
                        CityListService.getInstance().addItems(item);
                    }
                });


                builder.setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
                return false;
            }
        });

        String temperatureText = CityListService.getInstance().getTemperatureString(item);//"°F";
        holder.lblCurrent.setText(temperatureText);
        Weather weather = item.getCityItem().getWeather()[0];
        String weatherInfo = weather.getLabel() + " : " +  weather.getDescription();
        holder.lblInfo.setText(weatherInfo);
        String iconCode = item.getCityItem().getWeather()[0].getIcon();
        Bitmap bmpIcon = CityListService.getInstance().getCachedIcon(iconCode);
        if(bmpIcon == null){
            bmpIcon = PullTask.getInstance().getWeatherIconBitmap(iconCode);
            CityListService.getInstance().registerIcon(iconCode, bmpIcon);
        }

        holder.imgWeatherIcon.setImageBitmap(bmpIcon);
        holder.imgWeatherIcon.setVisibility(View.VISIBLE);
    }

    public void setItemList(List<CityListItem> list){
        this.items = list;
        notifyDataSetChanged();
    }

}
