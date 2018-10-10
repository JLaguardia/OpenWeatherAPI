package com.prismsoftworks.openweatherapitest.model.city;


import com.google.gson.annotations.SerializedName;

public class Temperature {

    @SerializedName("temp")
    private double temperature;

    @SerializedName("temp_min")
    private double low;

    @SerializedName("temp_max")
    private double high;
    private int humidity;

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }
}
