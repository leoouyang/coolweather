package com.example.leo.coolweather.util;

import android.text.TextUtils;

import com.example.leo.coolweather.db.City;
import com.example.leo.coolweather.db.District;
import com.example.leo.coolweather.db.Province;
import com.example.leo.coolweather.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray jsonArray = new JSONArray(response);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Province curProvince = new Province();
                    curProvince.setCode(jsonObject.getInt("id"));
                    curProvince.setName(jsonObject.getString("name"));
                    curProvince.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceId){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray jsonArray = new JSONArray(response);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    City curCity = new City();
                    curCity.setCode(jsonObject.getInt("id"));
                    curCity.setName(jsonObject.getString("name"));
                    curCity.setProvinceId(provinceId);
                    curCity.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleDistrictResponse(String response, int cityId){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray jsonArray = new JSONArray(response);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    District curDistrict = new District();
                    curDistrict.setWeatherId(jsonObject.getString("weather_id"));
                    curDistrict.setName(jsonObject.getString("name"));
                    curDistrict.setCityId(cityId);
                    curDistrict.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Weather weather =  new Gson().fromJson(weatherContent,Weather.class);
            return weather;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
