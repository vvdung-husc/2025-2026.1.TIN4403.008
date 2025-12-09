package com.example.dangnhap;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {
    private static final String PREFS = "app_prefs";
    private static final String KEY_BASE = "base_url";

    // Lấy base URL
    public static String getBaseUrl(Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String url = p.getString(KEY_BASE, null);
        if (url == null) {
            url = ctx.getString(R.string.server_host); // mặc định từ strings.xml
            p.edit().putString(KEY_BASE, url).apply();
        }
        return url;
    }

    // Lưu base URL mới
    public static void setBaseUrl(Context ctx, String baseUrl) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY_BASE, baseUrl).apply();
    }
}

