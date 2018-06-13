package com.example.leo.coolweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.leo.coolweather.gson.Forecast;
import com.example.leo.coolweather.gson.Weather;
import com.example.leo.coolweather.util.HttpUtil;
import com.example.leo.coolweather.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private String token = "db7ebf645a2a45fa9aadce6edd14503c";
    public DrawerLayout drawerLayout;
    private Button navButton;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String cityId;
    private ImageView bingPicImg;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private ImageView weatherImage;
    private TextView temperatureText;
    private TextView weatherInfoText;
    private TextView humidityText;
    private TextView windInfoText;
    private LinearLayout forecastList;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView airQualityText;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        bingPicImg = findViewById(R.id.bing_pic_img);
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        weatherImage = findViewById(R.id.weather_image);
        temperatureText = findViewById(R.id.temprature_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        humidityText = findViewById(R.id.humidity_text);
        windInfoText = findViewById(R.id.wind_info_text);
        forecastList = findViewById(R.id.forecast_list);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        airQualityText = findViewById(R.id.air_quality_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            cityId = weather.basic.id;
            showWeatherInfo(weather);
        }else{
            cityId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(cityId);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(cityId);
            }
        });

        String bingPicUrl = preferences.getString("bing_pic", null);
        if (bingPicUrl != null){
            Glide.with(this).load(bingPicUrl).into(bingPicImg);
        }else{
            loadBingPic2();
        }

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void requestWeather(final String weatherId){
        loadBingPic2();
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
                + "&key=" + token;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            cityId = weather.basic.id;
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    private void loadBingPic2(){
        final String requestBingPic = "https://cn.bing.com/HPImageArchive.aspx?idx=0&n=1&format=js";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                try{
                    JSONObject responseJson = new JSONObject(responseText);
                    JSONArray images = responseJson.getJSONArray("images");
                    JSONObject image = images.getJSONObject(0);
                    final String url = "https://cn.bing.com" + image.getString("url");
                    SharedPreferences.Editor editor = PreferenceManager.
                            getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("bing_pic", url);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(url).into(bingPicImg);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void showWeatherInfo(Weather weather){
        titleCity.setText(weather.basic.district);
        titleUpdateTime.setText(weather.update.localUpdateTime.split(" ")[1]);
        Glide.with(this).load("https://cdn.heweather.com/cond_icon/" +
                weather.now.cond_code + ".png").into(weatherImage);
        String temperature = weather.now.temperature + "℃";
        temperatureText.setText(temperature);
        String weatherInfo;
        if (Float.valueOf(weather.now.precipitation) > 0){
            weatherInfo = weather.now.weather + "，降水量" + weather.now.precipitation + "mm";
        }else{
            weatherInfo = weather.now.weather;
        }
        weatherInfoText.setText(weatherInfo);
        String humidity = "湿度：" + weather.now.humidity + "%";
        humidityText.setText(humidity);
        String windInfo = weather.now.wind_dir + "，" + weather.now.wind_sc + "级";
        windInfoText.setText(windInfo);
        forecastList.removeAllViews();
        for (Forecast forecast: weather.daily_forecast){
            View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item,
                    forecastList, false);
            TextView dateText = view.findViewById(R.id.forecast_date_text);
            TextView infoText = view.findViewById(R.id.forecast_info_text);
            TextView maxText = view.findViewById(R.id.forecast_max_text);
            TextView minText = view.findViewById(R.id.forecast_min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.moreInfo.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastList.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
            airQualityText.setText(weather.aqi.city.quality);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.brf + "。" + weather.suggestion.comfort.txt;
        String carWash = "洗车指数：" + weather.suggestion.carWash.brf + "。" + weather.suggestion.carWash.txt;
        String sport = "运动建议：" + weather.suggestion.sport.brf + "。" + weather.suggestion.sport.txt;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    public static void actionStart(Context context, String weatherId){
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra("weather_id", weatherId);
        context.startActivity(intent);
    }

    public static void actionStartWIthCache(Context context){
        Intent intent = new Intent(context, WeatherActivity.class);
        context.startActivity(intent);
    }
}
