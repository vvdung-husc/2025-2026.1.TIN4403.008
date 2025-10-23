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

        if (token != null && !token.isEmpty()) {
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
                // 🔹 Gửi yêu cầu POST kiểu x-www-form-urlencoded
                RequestBody body = new FormBody.Builder()
                        .add("token", token)
                        .build();

                Request request = new Request.Builder()
                        // 👉 Nếu bạn chạy trên EMULATOR: dùng "http://10.0.2.2:5000"
                        // 👉 Nếu bạn chạy trên ĐIỆN THOẠI THẬT: dùng IP thật như dưới đây
                        .url("http://192.168.1.139:5000/userinfo")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    String resBody = response.body().string();
                    JSONObject json = new JSONObject(resBody);

                    // 🔹 Xử lý dữ liệu phản hồi từ Flask
                    JSONObject user = null;
                    if (json.has("m")) {
                        user = json.getJSONObject("m");
                    } else if (json.has("user")) {
                        user = json.getJSONObject("user");
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(HomeActivity2.this, "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    String fullname = user.optString("fullname", "N/A");
                    String username = user.optString("username", "N/A");
                    String password = user.optString("password", "N/A");
                    String email = user.optString("email", "N/A");

                    String info = "👤 Họ và tên: " + fullname +
                            "\n🧾 Tài khoản: " + username +
                            "\n🔑 Mật khẩu: " + password +
                            "\n📧 Email: " + email;

                    runOnUiThread(() -> tvInfo.setText(info));
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(HomeActivity2.this, "Không thể kết nối server", Toast.LENGTH_SHORT).show());
            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(HomeActivity2.this, "Phản hồi JSON không hợp lệ", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
