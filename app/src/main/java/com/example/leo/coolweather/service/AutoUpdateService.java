package com.example.leo.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.leo.coolweather.WeatherActivity;
import com.example.leo.coolweather.gson.Weather;
import com.example.leo.coolweather.util.HttpUtil;
import com.example.leo.coolweather.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 1000*60*60;
        long triggerAtTime = SystemClock.elapsedRealtime() + 8 * anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.id;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
                    + "&key=" + WeatherActivity.token;
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    private void updateBingPic(){
        String requestBingPic = "https://cn.bing.com/HPImageArchive.aspx?idx=0&n=1&format=js";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                try {
                    JSONObject responseJson = new JSONObject(responseText);
                    JSONArray images = responseJson.getJSONArray("images");
                    JSONObject image = images.getJSONObject(0);
                    final String url = "https://cn.bing.com" + image.getString("url");
                    SharedPreferences.Editor editor = PreferenceManager.
                            getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("bing_pic", url);
                    editor.apply();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
