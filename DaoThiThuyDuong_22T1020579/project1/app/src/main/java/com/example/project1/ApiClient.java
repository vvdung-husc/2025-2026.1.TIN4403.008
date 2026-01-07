package com.example.project1;
import android.util.Log;


import okhttp3.*;
import java.io.IOException;
import java.util.Map;

//Định nghĩa lớp API để nhận thông tin từ server
public class ApiClient {
    /**
     * xem tất cả các tài khoản đang có trong hệ thống từ trình duyệt
     * https://dev.husc.edu.vn/tin4403/api/users
     **/

    //CHỈ SỬ DỤNG 1 TRONG 2
    //ĐÂY LÀ ĐỊNH NGHĨA URL CÁC API CHO APP QUAN INTERNET BẰNG DOMAIN dev.husc.edu.vn
    public static final String URL_LOGIN = "https://dev.husc.edu.vn/tin4403/api/login";
    public static final String URL_USER_INFO = "https://dev.husc.edu.vn/tin4403/api/userinfo";
    public static final String URL_USER_UPDATE = "https://dev.husc.edu.vn/tin4403/api/userupdate";
    public static final String URL_USER_REGISTER = "https://dev.husc.edu.vn/tin4403/api/register";

    // ĐÂY LÀ ĐỊNH NGHĨA URL CÁC API CHO APP DÙNG MẠNG NỘI BỘ - THAY ĐỊA CHỈ IP VÀ PORT ĐÚNG VỚI DỊCH VỤ ĐANG CHẠY

//    public static final String URL_LOGIN = "http://192.168.56.1:4380/login";
//    public static final String URL_USER_INFO = "http://192.168.56.1:4380/userinfo";
//    public static final String URL_USER_UPDATE = "http://192.168.56.1:4380/userupdate";
//    public static final String URL_USER_REGISTER = "http://192.168.56.1:4380/register";

    private static final OkHttpClient client = new OkHttpClient();

    // Class trả về kết quả
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
            return new ApiResult(false, e.getMessage(), e.hashCode());
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

            return new ApiResult(response.isSuccessful(), res, response.code());

        } catch (Exception e) {
            return new ApiResult(false, e.getMessage(), e.hashCode());
        }
    }
}