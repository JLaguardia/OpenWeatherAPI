package com.prismsoftworks.openweatherapitest;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private enum UnitType{
        KELVIN,
        METRIC,
        IMPERIAL
    }

//    private final String URL_FMT = "http://api.openweathermap.org/data/2.5/find?q=%s&units=%s";

    //api key: eb6d211c0e99deef8bb87c94621ce704
    //api base: api.openweathermap.org/data/2.5/find?q={city}&units=imperial
    //api base: api.openweathermap.org/data/2.5/weather?lat={latitude}&lon={longitude}&units=imperial
    //icon: "http://openweathermap.org/img/w/{icon_id}.png"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendOrlandoReq(View v) {
        ((TextView) findViewById(R.id.textView)).setText("sending request...");
        new PullTask(this, "Orlando").execute();
    }


    /**
     * sample:
     // https://samples.openweathermap.org/data/2.5/weather?lat=28.67&lon=-81.42&appid=b6907d289e10d714a6e88b30761fae22

     {
         "coord": {
             "lon": 139.01,
             "lat": 35.02
     },
         "weather": [
         {
             "id": 800,
             "main": "Clear",
             "description": "clear sky",
             "icon": "01n"
         }
     ],
         "base": "stations",
         "main": {
             "temp": 285.514,
             "pressure": 1013.75,
             "humidity": 100,
             "temp_min": 285.514,
             "temp_max": 285.514,
             "sea_level": 1023.22,
             "grnd_level": 1013.75
             },
         "wind": {
             "speed": 5.52,
             "deg": 311
             },
     "clouds": {
     "all": 0
     },
     "dt": 1485792967,
     "sys": {
     "message": 0.0025,
     "country": "JP",
     "sunrise": 1485726240,
     "sunset": 1485763863
     },
     "id": 1907296,
     "name": "Tawarano",
     "cod": 200
     }
     */

    private static class PullTask extends AsyncTask<Void, Void, Void>{

        private final Context context;
        private final String URL_FMT = "http://api.openweathermap.org/data/2.5/find?q=%s&units=%s&appid=%s";
        private final String city;
        private final String apiKey = "eb6d211c0e99deef8bb87c94621ce704";
        private String rawResponse = "";

        public PullTask(Context context, String city){
            this.context = context;
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

                while((line = reader.readLine()) != null){
                    rawResp.append(line);
                }

                reader.close();
            } catch (MalformedURLException mue){
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
            ((TextView)((MainActivity)context).findViewById(R.id.txtOutput)).setText(rawResponse);
            super.onPostExecute(aVoid);
        }
    }
}
