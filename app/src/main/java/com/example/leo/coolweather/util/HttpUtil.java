package com.example.leo.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static java.lang.Thread.sleep;

public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
