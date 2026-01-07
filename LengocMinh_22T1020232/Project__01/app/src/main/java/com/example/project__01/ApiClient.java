package com.example.project__01; // Sửa lại package cho đúng cấu trúc folder của bạn

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.Map;

public class ApiClient {
    // URL API - Đảm bảo IP này là IP máy tính chạy Node.js của bạn
    public static final String BASE_URL = "http://192.168.1.25:4380";
    public static final String URL_LOGIN = BASE_URL + "/login";
    public static final String URL_USER_INFO = BASE_URL + "/userinfo";
    public static final String URL_USER_UPDATE = BASE_URL + "/userupdate";
    public static final String URL_USER_REGISTER = BASE_URL + "/register";

    private static final OkHttpClient client = new OkHttpClient();

    public static class ApiResult {
        public boolean success;
        public int httpCode;
        public String body;

        public ApiResult(boolean success, String body, int code) {
            this.success = success;
            this.body = body;
            this.httpCode = code;
        }
    }

    public static ApiResult httpPost(String url, String json, Map<String, String> headers) {
        RequestBody requestBody;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        if (json == null || json.isEmpty()) {
            requestBody = RequestBody.create(new byte[0], null);
        } else {
            requestBody = RequestBody.create(json, JSON);
        }

        Request.Builder builder = new Request.Builder().url(url).post(requestBody);

        if (headers != null) {
            for (Map.Entry<String, String> h : headers.entrySet()) {
                builder.addHeader(h.getKey(), h.getValue());
            }
        }

        try (Response response = client.newCall(builder.build()).execute()) {
            String res = response.body() != null ? response.body().string() : "";
            return new ApiResult(response.isSuccessful(), res, response.code());
        } catch (Exception e) {
            return new ApiResult(false, e.getMessage(), 0);
        }
    }
}