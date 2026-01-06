package com.example.project1;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_AUTH_TOKEN = "authToken";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveAuthToken(Context context, String token) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    public static String getAuthToken(Context context) {
        return getPrefs(context).getString(KEY_AUTH_TOKEN, "");
    }

    public static void clearAuthToken(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove(KEY_AUTH_TOKEN);
        editor.apply();
    }
}