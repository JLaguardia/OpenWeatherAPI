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
    private static final String JSON_KEY = "jsonstr";
    private static final String IMG_KEY = "imgico";
    private static final int READ_TIMEOUT = 25000;
    private static final int CONNECT_TIMEOUT = 5000;
    private static PullTask instance = null;
    private static List<Worker> workerList = new ArrayList<>();

    public static PullTask getInstance(){
        if(instance == null){
            instance = new PullTask();
        }

        return instance;
    }

    private PullTask() { }

    public String getForecastCityJson(LatLng coord, UnitType chosenUnit){
        String apiKey = CityListService.getInstance().getWeatherApiKey();
        apiKey = (apiKey.equals("") ? mApiKey : apiKey);
        String formattedUrl = String.format(FORECAST_URL_FMT,
                String.valueOf(coord.latitude),
                String.valueOf(coord.longitude),
                chosenUnit.name(),
                apiKey);
        return fireJob(formattedUrl).getString(JSON_KEY);
    }

    public String getForecastCityRx(LatLng coord, UnitType chosenUnit){
        String apiKey = CityListService.getInstance().getWeatherApiKey();
        apiKey = (apiKey.equals("") ? mApiKey : apiKey);
        String formattedUrl = String.format(FORECAST_URL_FMT,
                String.valueOf(coord.latitude),
                String.valueOf(coord.longitude),
                chosenUnit.name(),
                apiKey);
        return rawTask(formattedUrl);
    }

    public String getWeatherCityJson(Set<LatLng> coords, UnitType chosenUnit) {
        String apiKey = CityListService.getInstance().getWeatherApiKey();
        apiKey = (apiKey.equals("") ? mApiKey : apiKey);
        StringBuilder res = new StringBuilder();
        for(LatLng coord: coords) {
            String formattedUrl = String.format(URL_FMT,
                                                String.valueOf(coord.latitude),
                                                String.valueOf(coord.longitude),
                                                chosenUnit.name(),
                                                apiKey);
            res.append(fireJob(formattedUrl).getString(JSON_KEY));
        }

        return res.toString();
    }

    public String getCityItemRx(LatLng coord){
        Log.e("Pull TASK: ", coord.latitude + " / " + coord.longitude);
        String apiKey = CityListService.getInstance().getWeatherApiKey();
        apiKey = (apiKey.equals("") ? mApiKey : apiKey);
        String formattedUrl = String.format(URL_FMT,
                String.valueOf(coord.latitude),
                String.valueOf(coord.longitude),
                UnitType.IMPERIAL.name(),
                apiKey);
        return rawTask(formattedUrl);

    }

    public Bitmap getWeatherIconBitmap(String iconCode){
        Bitmap res = null;
        String formattedUrl = String.format(ICO_URL_FMT, iconCode);
        byte[] bytes = fireJob(formattedUrl, IMG_KEY).getByteArray(IMG_KEY);
        if(bytes != null) {
            res = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

        return res;
    }

    public void stopTasks(){
        Log.e("worker", "Stopping worker threads: " + workerList.size());
        for(Worker wk : workerList){
            wk.cancel(true);
        }
    }

    private Bundle fireJob(String... url){
        Bundle res = null;
        try {
            workerList.add(new Worker());
            res = workerList.get(workerList.size() - 1).execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return res;
    }

    private String rawTask(String fmtUrl){
        StringBuilder rawResp = new StringBuilder();
        try {
            URL url = new URL(fmtUrl);
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

        return rawResp.toString();
    }

    private static class Worker extends AsyncTask<String, Void, Bundle>{
        @Override
        protected Bundle doInBackground(String... params) {
            Bundle response = new Bundle();
            StringBuilder rawResp = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                if(params.length > 1){
                    response.putByteArray(IMG_KEY, toByteArray(url.openStream()));
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
            return response;
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
        protected void onPostExecute(Bundle data) {
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
