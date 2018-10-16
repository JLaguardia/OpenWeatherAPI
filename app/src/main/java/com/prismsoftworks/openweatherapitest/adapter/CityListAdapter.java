package com.prismsoftworks.openweatherapitest.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.model.city.UnitType;
import com.prismsoftworks.openweatherapitest.model.city.Weather;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.object.CityViewHolder;
import com.prismsoftworks.openweatherapitest.service.CityListService;
import com.prismsoftworks.openweatherapitest.service.WeatherDoubleTapListener;
import com.prismsoftworks.openweatherapitest.task.PullTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityListAdapter extends RecyclerView.Adapter<CityViewHolder> {
    private List<CityListItem> items;
    private Map<String, Bitmap> cachedIcons = new HashMap<>();
    private UnitType chosenUnit = UnitType.IMPERIAL;

    public CityListAdapter(List<CityListItem> items) {
        this.items = items;
    }

    public CityListAdapter(){ }

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

//        holder.container.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                WeatherDoubleTapListener listener = new WeatherDoubleTapListener(item);
//                GestureDetector jesture = new GestureDetector(view.getContext(), listener);
//                jesture.setIsLongpressEnabled(true);
//                jesture.setOnDoubleTapListener(listener);//this does nothing
//                return jesture.onTouchEvent(motionEvent);
//            }
//        });


        String name = holder.lblName.getContext().getResources().getString(R.string.city_noname_label);
        if(item.getName() != null && !item.getName().equals("")){
            name = item.getName();
        }
        holder.lblName.setText(name);
        holder.lblName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = view.getContext();
                final Resources res = context.getResources();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(res.getString(R.string.city_rename_label));
                FrameLayout container = new FrameLayout(context);
                container.setPadding(8, 0, 8, 0);
                final EditText input = new EditText(context);
                String inputHint = "";

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
//                        notifyDataSetChanged();
                    }
                });


                builder.setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });

        String temperatureText = item.getCityItem().getTemperature().getTemperature() + getUnitSymbol();//"°F";
        holder.lblCurrent.setText(temperatureText);
        Weather weather = item.getCityItem().getWeather()[0];
        String weatherInfo = weather.getLabel() + " : " +  weather.getDescription();
        holder.lblInfo.setText(weatherInfo);
        String iconCode = item.getCityItem().getWeather()[0].getIcon();
        Bitmap bmpIcon = cachedIcons.get(iconCode);
        if(bmpIcon == null){
            PullTask.getInstance().addImageView(holder.getItemId(), holder.imgWeatherIcon);
            bmpIcon = PullTask.getInstance().getWeatherIconBitmap(holder.getItemId(), iconCode);
            cachedIcons.put(iconCode, bmpIcon);
        }

        holder.imgWeatherIcon.setImageBitmap(bmpIcon);
        holder.imgWeatherIcon.setVisibility(View.VISIBLE);
        holder.btnDelete.setVisibility(View.VISIBLE);
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

    private SimpleOnGestureListener doubleTapListener(final CityListItem item){
        return new SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.e("SINGLE TAP", "single tap deteccted");
                CityListService.getInstance().showDetails(item);
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.e("double TAP", "single tap deteccted");
                CityListService.getInstance().moveCamera(item.getCoordinates());
                return true;
            }
        };
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setChosenUnit(UnitType unit){
        this.chosenUnit = unit;
    }

    private String getUnitSymbol(){
        switch (chosenUnit){
            case KELVIN:
                return "°K";
            case IMPERIAL:
                return "°F";
            case METRIC:
                return "°C";
        }

        return "";
    }

    public void setItemList(List<CityListItem> list){
        this.items = list;
        notifyDataSetChanged();
    }
}
