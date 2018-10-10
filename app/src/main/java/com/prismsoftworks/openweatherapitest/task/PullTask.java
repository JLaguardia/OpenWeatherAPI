package com.prismsoftworks.openweatherapitest.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;
import com.prismsoftworks.openweatherapitest.model.city.UnitType;
import com.prismsoftworks.openweatherapitest.model.city.WrapperObj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PullTask extends AsyncTask<Void, Void, Void> {
    //api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s
    private final String URL_FMT = "http://api.openweathermap.org/data/2.5/find?q=%s&units=%s&appid=%s";
    private final String city;
    private final String apiKey = "eb6d211c0e99deef8bb87c94621ce704";
    private String rawResponse = "";

    public PullTask(String city) {
        this.city = city;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        StringBuilder rawResp = new StringBuilder();
        try {
            String formattedUrl = String.format(URL_FMT, city, UnitType.IMPERIAL.name(),
                    apiKey);
            URL url = new URL(formattedUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                rawResp.append(line);
            }

            reader.close();
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
            rawResp.append(mue.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            rawResp.append(e.getMessage());
        }

        rawResponse = rawResp.toString();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Gson gson = new GsonBuilder().create();
        WrapperObj li = gson.fromJson(rawResponse, WrapperObj.class);
        Log.e("POST EXECUTE", "response with " + li.list.length + " results");
        for(CityItem c : li.list) {
            Log.i("POST EXECUTE", "city: " + c.getName() + ", " + c.getIntl() + " | "
                    + c.getTemperature().getTemperature() + " degrees F");
        }
        super.onPostExecute(aVoid);
    }
}
