package com.oceanbreezeresorts.hotel.model;

import okhttp3.OkHttpClient;

public class OkHttpSingleton {
    private static OkHttpClient client;

    public static OkHttpClient getInstance() {
        if (client == null) {
            client = new OkHttpClient.Builder().build();
        }
        return client;
    }

}
