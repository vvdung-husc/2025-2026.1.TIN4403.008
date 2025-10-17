package com.example.baitap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity2 extends AppCompatActivity {

    TextView tvInfo;
    Button btnLogout;
    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        tvInfo = findViewById(R.id.tvInfo);
        btnLogout = findViewById(R.id.btnLogout);

        // Nhận token từ MainActivity
        String token = getIntent().getStringExtra("token");

        if (token != null && !token.isEmpty()) {
            getUserInfo(token);
        } else {
            Toast.makeText(this, "Không có token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
        }

        // 👉 Nút Đăng xuất
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity2.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa stack để không quay lại bằng nút Back
            startActivity(intent);
            Toast.makeText(HomeActivity2.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        });
    }

    private void getUserInfo(String token) {
        new Thread(() -> {
            try {
                RequestBody body = RequestBody.create("", JSON);
                Request request = new Request.Builder()
                        .url("https://dev.husc.edu.vn/tin4403/api/userinfo")
                        .post(body)
                        .addHeader("token", token)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String resBody = response.body().string();
                    JSONObject json = new JSONObject(resBody);
                    JSONObject user = json.getJSONObject("m");

                    String fullname = user.optString("fullname");
                    String username = user.optString("username");
                    String password = user.optString("password");
                    String email = user.optString("email");

                    runOnUiThread(() -> tvInfo.setText(
                            "Họ và tên: " + fullname +
                            "\n👤 Tài khoản: " + username +
                                    "\n🔑 Mật khẩu: " + password +
                                    "\n📧 Email: " + email
                    ));
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(HomeActivity2.this, "Không thể kết nối server", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(HomeActivity2.this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
