package com.example.project_01;
import android.util.Log;

import okhttp3.*;
import java.io.IOException;
import java.util.Map;

//Định nghĩa lớp API để nhận thông tin từ server
public class ApiClient {

    private static final OkHttpClient client = new OkHttpClient();

    // Class trả về kết quả
    public static class ApiResult {
        public boolean success;
        public String body;

        public ApiResult(boolean success, String body) {
            this.success = success;
            this.body = body;
        }
    }

    // ==========================
    // GET
    // ==========================
    public static ApiResult httpGet(String url, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder().url(url);

        if (headers != null) {
            for (Map.Entry<String, String> h : headers.entrySet()) {
                builder.addHeader(h.getKey(), h.getValue());
            }
        }

        try (Response response = client.newCall(builder.build()).execute()) {
            String body = response.body() != null ? response.body().string() : "";

            return new ApiResult(response.isSuccessful(), body);

        } catch (Exception e) {
            return new ApiResult(false, e.getMessage());
        }
    }

    // ==========================
    // POST JSON
    // ==========================
    public static ApiResult httpPost(String url, String json, Map<String, String> headers) {
        RequestBody requestBody;

        if (json == null || json.isEmpty()) {
            // POST không có body
            requestBody = RequestBody.create(new byte[0], null);
        } else {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
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

            return new ApiResult(response.isSuccessful(), res);

        } catch (Exception e) {
            return new ApiResult(false, e.getMessage());
        }
    }
}