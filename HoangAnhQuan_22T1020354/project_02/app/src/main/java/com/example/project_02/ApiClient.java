package com.example.project_02;

import okhttp3.*;
import java.util.Map;

public class ApiClient {

    // ==========================
    // API chạy trên MÁY CÁ NHÂN
    // IP WiFi: 192.168.1.11
    // Port: 4380
    // ==========================

    public static final String BASE_URL = "http://192.168.1.11:4380";

    public static final String URL_LOGIN         = BASE_URL + "/login";
    public static final String URL_USER_INFO     = BASE_URL + "/userinfo";
    public static final String URL_USER_UPDATE   = BASE_URL + "/userupdate";
    public static final String URL_USER_REGISTER = BASE_URL + "/register";

    private static final OkHttpClient client = new OkHttpClient();

    // ==========================
    // KẾT QUẢ TRẢ VỀ
    // ==========================
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
            return new ApiResult(response.isSuccessful(), body, response.code());
        } catch (Exception e) {
            return new ApiResult(false, e.getMessage(), -1);
        }
    }

    // ==========================
    // POST JSON
    // ==========================
    public static ApiResult httpPost(String url, String json, Map<String, String> headers) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody =
                (json == null || json.isEmpty())
                        ? RequestBody.create(new byte[0], null)
                        : RequestBody.create(json, JSON);

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
            return new ApiResult(response.isSuccessful(), res, response.code());
        } catch (Exception e) {
            return new ApiResult(false, e.getMessage(), -1);
        }
    }
}
