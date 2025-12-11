package com.example.baitap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity2 extends AppCompatActivity {

    TextView tvInfo;
    Button btnLogout;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        tvInfo = findViewById(R.id.tvInfo);
        btnLogout = findViewById(R.id.btnLogout);

        // Nhận token từ màn hình đăng nhập
        String token = getIntent().getStringExtra("token");

        if (token != null && token.length() > 0) {
            getUserInfo(token);
        } else {
            Toast.makeText(this, "Không có token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
        }

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity2.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(HomeActivity2.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        });
    }

    private void getUserInfo(String token) {
        new Thread(() -> {
            try {
                RequestBody body = new FormBody.Builder()
                        .add("token", token)
                        .build();

                Request request = new Request.Builder()
                        .url("http://192.168.1.139:5000/userinfo") // hoặc IP thật của PC
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    String resBody = response.body().string();
                    System.out.println(">>> Response body: " + resBody);

                    JSONObject json = new JSONObject(resBody);
                    if (json.getInt("r") == 1 && json.has("m")) {
                        JSONObject user = json.getJSONObject("m");

                        String fullname = user.optString("fullname", "N/A");
                        String username = user.optString("username", "N/A");
                        String password = user.optString("password", "N/A");
                        String email = user.optString("email", "N/A");

                        String info = "👤 Họ và tên: " + fullname +
                                "\n🧾 Tài khoản: " + username +
                                "\n🔑 Mật khẩu: " + password +
                                "\n📧 Email: " + email;

                        runOnUiThread(() -> tvInfo.setText(info));
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(HomeActivity2.this, "Không lấy được thông tin người dùng!", Toast.LENGTH_SHORT).show());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(HomeActivity2.this, "Lỗi khi kết nối hoặc xử lý dữ liệu", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}
