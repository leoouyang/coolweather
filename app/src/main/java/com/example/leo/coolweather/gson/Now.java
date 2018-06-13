package com.example.leo.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond_txt")
    public String weather;

    @SerializedName("hum")
    public String humidity;

    public String wind_dir;

    public String wind_sc;

    @SerializedName("pcpn")
    public String precipitation;

    public String cond_code;
}
