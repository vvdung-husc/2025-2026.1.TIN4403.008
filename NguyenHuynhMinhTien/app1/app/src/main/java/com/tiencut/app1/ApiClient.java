package com.tiencut.app1;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;

public class ApiClient {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "https://dev.husc.edu.vn/tin4403/api";

    private static String getTokenFromPrefs(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPref.getString("AuthToken", "");
    }

    /**
     * POST /userinfo
     * Header: token (from login)
     * Asynchronous. Pass an OkHttp Callback to handle response.
     */
    public static void getUserInfo(@NonNull Context context, @NonNull Callback callback) {
        String token = getTokenFromPrefs(context);

        Request.Builder reqBuilder = new Request.Builder()
                .url(BASE_URL + "/userinfo")
                .post(RequestBody.create(new byte[0])) // empty POST body
                .header("token", token); // server may expect header named 'token'

        // Also add Authorization header (Bearer) for compatibility
        if (token != null && !token.isEmpty()) {
            reqBuilder.header("Authorization", "Bearer " + token);
        }

        Request request = reqBuilder.build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * GET /api/students from local backend
     * Use http://10.0.2.2:3000 when testing on Android emulator
     */
    public static void getStudentsLocal(@NonNull Callback callback) {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/api/students")
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * POST /userupdate
     * Header: token (from login)
     * Body: x-www-form-urlencoded: password, fullname, email (send only fields you want to update)
     * Asynchronous. Pass an OkHttp Callback to handle response.
     */
    public static void updateUser(@NonNull Context context, String password, String fullname, String email, @NonNull Callback callback) {
        FormBody.Builder form = new FormBody.Builder();
        if (password != null && !password.isEmpty()) form.add("password", password);
        if (fullname != null && !fullname.isEmpty()) form.add("fullname", fullname);
        if (email != null && !email.isEmpty()) form.add("email", email);

        RequestBody body = form.build();

        String token = getTokenFromPrefs(context);

        Request.Builder reqBuilder = new Request.Builder()
                .url(BASE_URL + "/userupdate")
                .post(body)
                .header("token", token);

        if (token != null && !token.isEmpty()) {
            reqBuilder.header("Authorization", "Bearer " + token);
        }

        Request request = reqBuilder.build();
        client.newCall(request).enqueue(callback);
    }
}
