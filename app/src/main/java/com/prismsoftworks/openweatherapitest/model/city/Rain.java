package com.prismsoftworks.openweatherapitest.model.city;

import com.google.gson.annotations.SerializedName;

public class Rain {
    @SerializedName("3h")
    private String threeHour = "0";

    public String getThreeHour() {
        return threeHour;
    }

    public void setThreeHour(String threeHour) {
        this.threeHour = threeHour;
    }
}
