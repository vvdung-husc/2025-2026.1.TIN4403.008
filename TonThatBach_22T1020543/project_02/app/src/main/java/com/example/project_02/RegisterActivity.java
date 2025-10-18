package com.example.project_02;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerUsername, registerEmail, registerPassword;
    private Button registerButton;
    private TextView backToLogin;

    // Định nghĩa MediaType JSON cho body request
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        registerUsername = findViewById(R.id.registerUsername);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerButton = findViewById(R.id.registerButton);
        backToLogin = findViewById(R.id.backToLogin);

        // Khi nhấn nút Đăng ký
        registerButton.setOnClickListener(v -> {
            String user = registerUsername.getText().toString().trim();
            String email = registerEmail.getText().toString().trim();
            String pass = registerPassword.getText().toString().trim();

            if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    apiRegister(user, email, pass);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Khi nhấn “Đã có tài khoản? Đăng nhập”
        backToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    // Gửi request đăng ký bằng OkHttp
    void apiRegister(String user, String email, String pass) throws IOException {
        String json = "{\"username\":\"" + user + "\",\"email\":\"" + email + "\",\"password\":\"" + pass + "\"}";
        Log.d("REGISTER_JSON", json);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("https://dev.husc.edu.vn/tin4403/api/register") // ⚠️ sửa lại “rgister” → “register”
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(),
                                "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                Log.e("REGISTER_FAIL", e.getMessage());
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d("REGISTER_RESPONSE", result);

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Đăng ký thất bại: " + result, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
