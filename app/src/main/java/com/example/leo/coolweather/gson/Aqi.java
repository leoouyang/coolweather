package com.example.leo.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Aqi {
    public AqiCity city;
    public class AqiCity{
        public String aqi;
        public String pm25;
        @SerializedName("qlty")
        public String quality;
    }
}
