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

    // 🔹 API máy cá nhân
    private static final String BASE_URL = "http://192.168.1.11:8080";

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

        // Nhận token
        token = getIntent().getStringExtra("token");
        Log.d("TOKEN_DEBUG", "Token nhận được: " + token);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy token đăng nhập!", Toast.LENGTH_LONG).show();
        } else {
            getUserInfo(token);
        }

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

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

    /** 🔹 Gọi API lấy thông tin người dùng **/
    private void getUserInfo(String token) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create("{}", JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/userinfo")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Log.d(TAG, "Request: " + request.url());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure", e);
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(),
                                "Không kết nối được server!",
                                Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";

                Log.d(TAG, "HTTP " + response.code());
                Log.d(TAG, "BODY: " + responseBody);

                runOnUiThread(() -> {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                "Lỗi tải thông tin!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(responseBody);

                        String username = json.optString("username", "N/A");
                        String fullname = json.optString("fullname", "N/A");
                        String email = json.optString("email", "N/A");

                        txtUsername.setText("Tài khoản: " + username);
                        txtFullname.setText("Họ tên: " + fullname);
                        txtEmail.setText("Email: " + email);

                    } catch (Exception e) {
                        Log.e(TAG, "JSON error", e);
                        Toast.makeText(getApplicationContext(),
                                "Lỗi đọc dữ liệu!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
