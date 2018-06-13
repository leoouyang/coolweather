package com.example.leo.coolweather.gson;

import com.example.leo.coolweather.util.Utility;

import java.util.List;

public class Weather {
    public String status;

    public Basic basic;

    public Aqi aqi;

    public List<Forecast> daily_forecast;

    public Now now;

    public Suggestion suggestion;
    
    public Update update;
}
