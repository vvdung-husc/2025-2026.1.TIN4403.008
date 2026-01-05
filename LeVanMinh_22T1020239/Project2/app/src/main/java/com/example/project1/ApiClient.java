package com.example.project1;

import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.MediaType;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    public static final String URL_LOGIN = "https://dev.husc.edu.vn/tin4403/api/login";
    public static final String URL_USER_INFO = "https://dev.husc.edu.vn/tin4403/api/userinfo";
    public static final String URL_USER_UPDATE = "https://dev.husc.edu.vn/tin4403/api/userupdate";
    public static final String URL_USER_REGISTER = "https://dev.husc.edu.vn/tin4403/api/register";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public static class ApiResult {
        public boolean success;
        public int  httpCode;
        public String body;

        public ApiResult(boolean success, String body, int code) {
            this.success = success;
            this.body = body;
            this.httpCode = code;
        }
    }

    public static ApiResult httpPost(String url, String json, Map<String, String> headers) {
        final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody requestBody;

        if (json == null || json.isEmpty()) {
            requestBody = RequestBody.create(new byte[0], null);
        } else {
            requestBody = RequestBody.create(json, JSON);
        }

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);

        if (headers != null) {
            for (Map.Entry<String, String> h : headers.entrySet()) {
                builder.addHeader(h.getKey(), h.getValue());
            }
        }

        try (Response response = client.newCall(builder.build()).execute()) {
            String res = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                Log.e("ApiClient", "API Failed: " + url + " | Code: " + response.code() + " | Body: " + res);
            }

            return new ApiResult(response.isSuccessful(), res, response.code());
        } catch (IOException e) {
            Log.e("ApiClient", "HTTP Post Exception for " + url + ": " + e.getMessage());
            return new ApiResult(false, e.getMessage(), 0);
        }
    }
}