package com.example.project_1;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;

import java.util.Map;

// ==========================
// API CLIENT - OKHTTP 4.x
// ==========================
public class ApiClient {

    /**
     * Test API trên trình duyệt:
     * https://dev.husc.edu.vn/tin4403/api/users
     */

    // ==========================
    // API URL (CHỌN 1 TRONG 2)
    // ==========================

    // --- API qua Internet ---
    // public static final String URL_LOGIN = "https://dev.husc.edu.vn/tin4403/api/login";
    // public static final String URL_USER_INFO = "https://dev.husc.edu.vn/tin4403/api/userinfo";
    // public static final String URL_USER_UPDATE = "https://dev.husc.edu.vn/tin4403/api/userupdate";
    // public static final String URL_USER_REGISTER = "https://dev.husc.edu.vn/tin4403/api/register";

    // --- API mạng nội bộ ---
    public static final String URL_LOGIN = "http://192.168.56.1:4380/login";
    public static final String URL_USER_INFO = "http://192.168.56.1:4380/userinfo";
    public static final String URL_USER_UPDATE = "http://192.168.56.1:4380/userupdate";
    public static final String URL_USER_REGISTER = "http://192.168.56.1:4380/register";

    // OkHttp client (dùng chung)
    private static final OkHttpClient client = new OkHttpClient();

    // ==========================
    // CLASS KẾT QUẢ TRẢ VỀ
    // ==========================
    public static class ApiResult {
        public boolean success;
        public int httpCode;
        public String body;

        public ApiResult(boolean success, String body, int httpCode) {
            this.success = success;
            this.body = body;
            this.httpCode = httpCode;
        }
    }

    // ==========================
    // HTTP GET
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
    // HTTP POST (JSON)
    // ==========================
    public static ApiResult httpPost(String url, String json, Map<String, String> headers) {

        RequestBody requestBody;

        if (json == null || json.isEmpty()) {

            requestBody = RequestBody.create(new byte[0], null);
        } else {
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
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
            String body = response.body() != null ? response.body().string() : "";
            return new ApiResult(response.isSuccessful(), body, response.code());
        } catch (Exception e) {
            return new ApiResult(false, e.getMessage(), -1);
        }
    }
}
