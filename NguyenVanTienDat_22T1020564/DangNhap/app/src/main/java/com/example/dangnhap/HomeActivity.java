package com.example.dangnhap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import okhttp3.*;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    TextView txtWelcome;
    Button btnInfo, btnLogout;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtWelcome = findViewById(R.id.txtWelcome);
        btnInfo = findViewById(R.id.btnInfo);
        btnLogout = findViewById(R.id.btnLogout);

        String username = getIntent().getStringExtra("username");
        String token = getIntent().getStringExtra("token");

        if (username != null) {
            txtWelcome.setText("Xin chào, " + username + "!");
        }

        btnInfo.setOnClickListener(v -> {
            String url = AppConfig.getBaseUrl(this) + "/api/userinfo";
            Request req = new Request.Builder()
                    .url(url)
                    .addHeader("token", token)
                    .post(RequestBody.create("", null))
                    .build();

            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(HomeActivity.this, "Không kết nối được server!", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    runOnUiThread(() -> new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Thông tin tài khoản")
                            .setMessage(resp)
                            .setPositiveButton("OK", null)
                            .show());
                }
            });
        });

        btnLogout.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có muốn đăng xuất không?")
                .setPositiveButton("Có", (DialogInterface d, int w) -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("Không", null)
                .show());
    }
}
