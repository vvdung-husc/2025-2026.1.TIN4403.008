package com.example.project_02;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "API_USERINFO";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    TextView txtUsername, txtFullname, txtEmail;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        txtUsername = findViewById(R.id.txtUsername);
        txtFullname = findViewById(R.id.txtFullname);
        txtEmail = findViewById(R.id.txtEmail);
        btnLogout = findViewById(R.id.btnLogout);

        // Lấy token từ Intent
        String token = getIntent().getStringExtra("token");
        Log.d("TOKEN_DEBUG", "Token nhận được (raw): " + token);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy token đăng nhập!", Toast.LENGTH_LONG).show();
        } else {
            // Gọi API lấy thông tin
            getUserInfo(token);
        }

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Gọi API userinfo bằng POST.
     * Thử gửi token ở cả header "token" và header "Authorization: Bearer <token>"
     * In log chi tiết để debug khi server trả lỗi.
     */
    private void getUserInfo(String token) {
        OkHttpClient client = new OkHttpClient();

        // Body JSON rỗng để POST (nhiều API yêu cầu POST)
        RequestBody body = RequestBody.create("{}", JSON);

        Request request = new Request.Builder()
                .url("https://dev.husc.edu.vn/tin4403/api/userinfo")
                .post(body)
                // Gửi cả 2 header để thử: server có thể chấp nhận 1 trong 2
                .addHeader("token", token)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Log.d(TAG, "Gửi request tới /userinfo với token (tóm tắt): " + summarizeToken(token));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + e.getMessage(), e);
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Lỗi kết nối máy chủ: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) {
                String responseBody = "";
                int code = response.code();
                try {
                    if (response.body() != null) {
                        responseBody = response.body().string();
                    } else {
                        responseBody = "";
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Lỗi đọc body: " + e.getMessage(), e);
                }

                // In log chi tiết: code, headers, body
                Log.d(TAG, "HTTP Code: " + code);
                Log.d(TAG, "Response headers: " + Objects.requireNonNull(response).headers().toString());
                Log.d(TAG, "Response body: " + responseBody);

                final String finalBody = responseBody;
                runOnUiThread(() -> {
                    if (!response.isSuccessful()) {
                        // Hiển thị mã lỗi + body cho debug
                        String msg = "Không thể tải thông tin người dùng! Mã: " + code;
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                        // Hiện raw response để bạn copy gửi mình (debug)
                        txtUsername.setText("ERROR: HTTP " + code);
                        txtFullname.setText(finalBody);
                        txtEmail.setText("");
                        return;
                    }

                    // Nếu thành công, cố gắng parse JSON theo vài khả năng
                    try {
                        JSONObject json = new JSONObject(finalBody);
                        // Trường hợp server trả { "r":1, "m": { ... } }
                        JSONObject info;
                        if (json.has("m") && json.get("m") instanceof JSONObject) {
                            info = json.getJSONObject("m");
                        } else if (json.has("data") && json.get("data") instanceof JSONObject) {
                            // hoặc { "data": { ... } }
                            info = json.getJSONObject("data");
                        } else {
                            // hoặc server trả trực tiếp object chứa username/fullname/email
                            info = json;
                        }

                        String username = info.optString("username", info.optString("user", "Chưa có"));
                        String fullname = info.optString("fullname", info.optString("name", "Chưa có"));
                        String email = info.optString("email", info.optString("mail", "Chưa có"));

                        txtUsername.setText("Tài khoản: " + username);
                        txtFullname.setText("Họ tên: " + fullname);
                        txtEmail.setText("Email: " + email);

                    } catch (Exception e) {
                        // Nếu parse thất bại — hiển thị raw body để debug
                        Log.e(TAG, "Lỗi parse JSON: " + e.getMessage(), e);
                        txtUsername.setText("Không parse được JSON");
                        txtFullname.setText(finalBody);
                        txtEmail.setText("");
                        Toast.makeText(getApplicationContext(), "Lỗi đọc dữ liệu người dùng!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    // Hiển thị tóm tắt token (không in toàn bộ để tránh lộ nhạy cảm)
    private String summarizeToken(String token) {
        if (token == null) return "null";
        if (token.length() <= 10) return token;
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}
