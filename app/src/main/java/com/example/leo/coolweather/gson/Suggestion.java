package com.example.leo.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {

    @SerializedName("comf")
    public suggestionInfo comfort;

    @SerializedName("cw")
    public suggestionInfo carWash;

    public suggestionInfo sport;

    public class suggestionInfo{
        public String brf;
        public String txt;
    }
}
