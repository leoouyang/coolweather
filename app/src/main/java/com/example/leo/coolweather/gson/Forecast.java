package com.example.leo.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature{
        public String min;
        public String max;
    }

    @SerializedName("cond")
    public MoreInfo moreInfo;

    public class MoreInfo{
        @SerializedName("txt_d")
        public String info;
    }
}
