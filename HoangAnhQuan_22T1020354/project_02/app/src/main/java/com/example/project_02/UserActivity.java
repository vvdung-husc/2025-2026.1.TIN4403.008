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
    Button btnLogout, btnEdit;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        txtUsername = findViewById(R.id.txtUsername);
        txtFullname = findViewById(R.id.txtFullname);
        txtEmail = findViewById(R.id.txtEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnEdit = findViewById(R.id.btnEdit);

        // Nhận token từ Intent
        token = getIntent().getStringExtra("token");
        Log.d("TOKEN_DEBUG", "Token nhận được (raw): " + token);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy token đăng nhập!", Toast.LENGTH_LONG).show();
        } else {
            // Gọi API lấy thông tin người dùng
            getUserInfo(token);
        }

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        // Nút chỉnh sửa tài khoản
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /** Gọi API /userinfo để lấy thông tin người dùng **/
    private void getUserInfo(String token) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create("{}", JSON);

        Request request = new Request.Builder()
                .url("https://dev.husc.edu.vn/tin4403/api/userinfo")
                .post(body)
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
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Lỗi đọc body: " + e.getMessage(), e);
                }

                Log.d(TAG, "HTTP Code: " + code);
                Log.d(TAG, "Response headers: " + Objects.requireNonNull(response).headers().toString());
                Log.d(TAG, "Response body: " + responseBody);

                final String finalBody = responseBody;
                runOnUiThread(() -> {
                    if (!response.isSuccessful()) {
                        txtUsername.setText("ERROR: HTTP " + code);
                        txtFullname.setText(finalBody);
                        txtEmail.setText("");
                        Toast.makeText(getApplicationContext(), "Không thể tải thông tin người dùng!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(finalBody);
                        JSONObject info;
                        if (json.has("m") && json.get("m") instanceof JSONObject) {
                            info = json.getJSONObject("m");
                        } else if (json.has("data") && json.get("data") instanceof JSONObject) {
                            info = json.getJSONObject("data");
                        } else {
                            info = json;
                        }

                        String username = info.optString("username", "Chưa có");
                        String fullname = info.optString("fullname", "Chưa có");
                        String email = info.optString("email", "Chưa có");

                        txtUsername.setText("Tài khoản: " + username);
                        txtFullname.setText("Họ tên: " + fullname);
                        txtEmail.setText("Email: " + email);

                    } catch (Exception e) {
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

    // Tóm tắt token để debug mà không lộ toàn bộ
    private String summarizeToken(String token) {
        if (token == null) return "null";
        if (token.length() <= 10) return token;
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}
