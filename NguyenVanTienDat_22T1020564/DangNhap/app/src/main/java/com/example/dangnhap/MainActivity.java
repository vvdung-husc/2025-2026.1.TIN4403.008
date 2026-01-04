package com.example.dangnhap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    EditText edtUser, edtPass;
    Button btnLogin, btnRegister, btnSetting;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Nút thay đổi IP server
        btnSetting = new Button(this);
        btnSetting.setText("Cài đặt Server");
        ((LinearLayout) findViewById(R.id.mainLayout)).addView(btnSetting);

        btnSetting.setOnClickListener(v -> showChangeServerDialog());

        // Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String user = edtUser.getText().toString().trim();
            String pass = edtPass.getText().toString().trim();
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = AppConfig.getBaseUrl(this) + "/api/login";
            RequestBody form = new FormBody.Builder()
                    .add("username", user)
                    .add("password", pass)
                    .build();

            Request request = new Request.Builder().url(url).post(form).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        if (obj.getInt("r") == 1) {
                            String token = obj.getString("m");
                            runOnUiThread(() -> {
                                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                i.putExtra("token", token);
                                i.putExtra("username", user);
                                startActivity(i);
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        // Đăng ký
        btnRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void showChangeServerDialog() {
        final EditText input = new EditText(this);
        input.setHint("http://192.168.1.20:3000");
        input.setText(AppConfig.getBaseUrl(this));

        new AlertDialog.Builder(this)
                .setTitle("Đổi địa chỉ Server API")
                .setView(input)
                .setPositiveButton("Lưu", (d, which) -> {
                    String val = input.getText().toString().trim();
                    if (!val.isEmpty()) {
                        AppConfig.setBaseUrl(this, val);
                        Toast.makeText(this, "Đã lưu server: " + val, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
