package com.example.leo.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {

    public String id;

    @SerializedName("parent_city")
    public String city;

    @SerializedName("admin_area")
    public String province;

    @SerializedName("cnty")
    public String country;

    @SerializedName("city")
    public String district;

    public String lon;

    public String lat;

    public String tz;
}
