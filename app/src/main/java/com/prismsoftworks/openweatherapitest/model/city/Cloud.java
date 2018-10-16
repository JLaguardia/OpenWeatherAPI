package com.prismsoftworks.openweatherapitest.model.city;

import com.google.gson.annotations.SerializedName;

public class Cloud {
    @SerializedName("all")
    private String cloudLevel = "0";

    public String getCloudLevel() {
        return cloudLevel;
    }

    public void setCloudLevel(String cloudLevel) {
        this.cloudLevel = cloudLevel;
    }
}
