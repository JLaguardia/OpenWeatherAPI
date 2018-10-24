package com.prismsoftworks.openweatherapitest.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.prismsoftworks.openweatherapitest.model.city.UnitType;
import com.prismsoftworks.openweatherapitest.service.CityListService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class PullTask {
    //REGULAR: http://api.openweathermap.org/data/2.5/find?q=%s&units=%s&appid=%s
    //FORECAST: http://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=%s&appid=%s
    //icon: "http://openweathermap.org/img/w/{icon_id}.png"
    private static final String ICO_URL_FMT = "http://openweathermap.org/img/w/%s.png";
    private static final String URL_FMT = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=%s&appid=%s";
    private static final String FORECAST_URL_FMT = "http://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=%s&appid=%s";
    private static final String mApiKey = "eb6d211c0e99deef8bb87c94621ce704";
    public static final String JSON_KEY = "jsonstr";
    public static final String IMG_KEY = "imgico";
    private static final int READ_TIMEOUT = 25000;
    private static final int CONNECT_TIMEOUT = 5000;
    private static PullTask instance = null;
    private static List<Worker> workerList = new ArrayList<>();
    private static Map<String, byte[]> cachedIcons = new HashMap<>();

    public static PullTask getInstance(){
        if(instance == null){
            instance = new PullTask();
        }

        return instance;
    }

    private PullTask() { }

    public void getForecastCityJson(LatLng coord, UnitType chosenUnit, TaskCallback callback){
        String apiKey = CityListService.getInstance().getWeatherApiKey();
        apiKey = (apiKey.equals("") ? mApiKey : apiKey);
        String formattedUrl = String.format(FORECAST_URL_FMT,
                String.valueOf(coord.latitude),
                String.valueOf(coord.longitude),
                chosenUnit.name(),
                apiKey);
        fireJob(callback, formattedUrl);
    }

    public void getWeatherCityJson(Set<LatLng> coords, UnitType chosenUnit, TaskCallback callback) {
        String apiKey = CityListService.getInstance().getWeatherApiKey();
        apiKey = (apiKey.equals("") ? mApiKey : apiKey);
        for(LatLng coord: coords) {
            String formattedUrl = String.format(URL_FMT,
                                                String.valueOf(coord.latitude),
                                                String.valueOf(coord.longitude),
                                                chosenUnit.name(),
                                                apiKey);
            fireJob(callback, formattedUrl);
        }
    }

    public void getWeatherIconBitmap(String iconCode, TaskCallback callback){
        if(cachedIcons.containsKey(iconCode)) {
            Bundle res = new Bundle();
            res.putByteArray(IMG_KEY, cachedIcons.get(iconCode));
            callback.callback(res);
        }

        String formattedUrl = String.format(ICO_URL_FMT, iconCode);
        fireJob(callback, formattedUrl, iconCode);
    }

    public void registerBitmap(String key, byte[] bitmap){
        cachedIcons.put(key, bitmap);
    }

    public void stopTasks(){
        Log.e("worker", "Stopping worker threads: " + workerList.size());
        for(Worker wk : workerList){
            wk.cancel(true);
        }
    }

    private void fireJob(TaskCallback callback, String... url){
        workerList.add(new Worker(callback));
        workerList.get(workerList.size() - 1).execute(url);
    }

    private static class Worker extends AsyncTask<String, Void, Void>{
        private final TaskCallback callback;

        private Worker(TaskCallback callback){
            Log.e("worker", "task created");
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(String... params) {
            Bundle response = new Bundle();
            StringBuilder rawResp = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                if(params.length > 1){
                    byte[] bytes = toByteArray(url.openStream());
                    response.putByteArray(IMG_KEY, bytes);
                    PullTask.getInstance().registerBitmap(params[1], bytes);
                } else {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(CONNECT_TIMEOUT);
                    conn.setReadTimeout(READ_TIMEOUT);
                    int code = conn.getResponseCode();
                    BufferedReader reader;
                    if(code >= 200 && code <= 299) {
                        reader = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        String line;

                        while ((line = reader.readLine()) != null) {
                            rawResp.append(line);
                        }

                    } else {
                        reader = new BufferedReader(
                                new InputStreamReader(conn.getErrorStream()));
                        String line;

                        while ((line = reader.readLine()) != null) {
                            rawResp.append(line);
                        }
                    }
                    conn.disconnect();
                    reader.close();
                }
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
                rawResp.append(mue.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                rawResp.append(e.getMessage());
            } catch(Exception e){
                e.printStackTrace();
                rawResp.append(e.getMessage());
            }

            response.putString(JSON_KEY, rawResp.toString());
            callback.callback(response);
            return null;
        }

        private byte[] toByteArray(InputStream in) throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int data;

            while((data = in.read(buffer)) != -1){
                bos.write(buffer, 0, data);
            }

            return bos.toByteArray();
        }

        @Override
        protected void onPostExecute(Void data) {
            workerList.remove(this);
            super.onPostExecute(data);
        }

        @Override
        protected void onCancelled() {
            workerList.remove(this);
            super.onCancelled();
        }
    }
}
