package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private EditText m_edtUsername;
    private EditText m_edtPassword;
    private Button m_btnLogin;
    private Button m_btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_edtUsername = findViewById(R.id.edtUsername);
        m_edtPassword = findViewById(R.id.edtPassword);
        m_btnLogin = findViewById(R.id.btnLogin);
        m_btnRegister = findViewById(R.id.btnRegister);

        // Sử dụng Lambda (Sửa lỗi phong cách)
        m_btnLogin.setOnClickListener(v -> {
            // Khởi tạo LoginTask tĩnh với tham chiếu đến Activity hiện tại
            new LoginTask(this).execute();
        });

        // Sử dụng Lambda (Sửa lỗi phong cách)
        m_btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    // ====================================================================================
    // SỬA LỖI MEMORY LEAK: Chuyển AsyncTask thành Static class và sử dụng WeakReference
    // ====================================================================================

    private static class LoginTask extends AsyncTask<Void, Void, ApiClient.ApiResult> {

        private WeakReference<MainActivity> activityWeakReference;
        private String username;
        private String password;

        LoginTask(MainActivity context) {
            activityWeakReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                cancel(true);
                return;
            }

            username = activity.m_edtUsername.getText().toString().trim();
            password = activity.m_edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(activity, "Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu.", Toast.LENGTH_SHORT).show();
                cancel(true);
            }
        }

        @Override
        protected ApiClient.ApiResult doInBackground(Void... voids) {
            if (isCancelled()) {
                return null;
            }

            try {
                JSONObject jsonPayload = new JSONObject();
                jsonPayload.put("username", username);
                jsonPayload.put("password", password);
                return ApiClient.httpPost(ApiClient.URL_LOGIN, jsonPayload.toString(), null);

            } catch (JSONException e) {
                return new ApiClient.ApiResult(false, "Lỗi tạo dữ liệu JSON: " + e.getMessage(), 0);
            }
        }

        @Override
        protected void onPostExecute(ApiClient.ApiResult result) {
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing() || result == null) return;

            if (result.success && result.httpCode == 200) {
                try {
                    JSONObject jsonResponse = new JSONObject(result.body);

                    // SỬA LỖI LOGIC: Đặt giá trị mặc định an toàn
                    String status = jsonResponse.optString("status", "failed"); // Mặc định là failed nếu không có
                    String token = jsonResponse.optString("token", null);       // Mặc định là null nếu không có
                    String msg = jsonResponse.optString("msg", "Đăng nhập thành công!");

                    // KIỂM TRA CHUYỂN MÀN HÌNH:
                    // Yêu cầu status là "success" VÀ token phải có giá trị (không null, không rỗng)
                    if ("success".equals(status) && token != null && !token.isEmpty()) {

                        Utils.saveAuthToken(activity, token);

                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(activity, UserActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        // Nếu status/token không đúng (ví dụ: status="" và token=""), hiển thị msg
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, "Lỗi phân tích phản hồi server.", Toast.LENGTH_LONG).show();
                }

            } else {
                String errorMessage = "Đăng nhập thất bại. Code: " + result.httpCode;
                if (result.body != null && !result.body.isEmpty()) {
                    try {
                        JSONObject jsonError = new JSONObject(result.body);
                        errorMessage = jsonError.optString("msg", errorMessage);
                    } catch (JSONException ignored) {}
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}