package com.prismsoftworks.openweatherapitest.model.city;

import com.google.gson.annotations.SerializedName;

public class Weather {

    @SerializedName("main")
    private String label;
    private String description;
    private String icon;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
