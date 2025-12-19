package com.example.baitap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText etRegFullname,etRegUsername, etRegPassword, etRegConfirmPassword, etRegEmail;
    Button btnRegister;

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etRegFullname = findViewById(R.id.etRegFullname);
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        etRegEmail = findViewById(R.id.etRegEmail);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String fullname = etRegFullname.getText().toString().trim();
            String username = etRegUsername.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();
            String confirmPass = etRegConfirmPassword.getText().toString().trim();
            String email = etRegEmail.getText().toString().trim();

            if (fullname.isEmpty() ||username.isEmpty() || password.isEmpty() || confirmPass.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(fullname, username, password, email);
        });
    }

    private void registerUser(String fullname, String username, String password, String email) {
        new Thread(() -> {
            try {
                RequestBody body = new okhttp3.FormBody.Builder()
                        .add("fullname", fullname)
                        .add("username", username)
                        .add("password", password)
                        .add("email", email)
                        .build();

                String apiUrl = "http://192.168.137.38:4380/register";

                Request request = new Request.Builder()
                        .url(apiUrl)
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String resBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(resBody);

                    int resultCode = jsonResponse.optInt("r", 0); // lấy giá trị r
                    boolean success = (resultCode == 1);

                    runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Quay lại đăng nhập...", Toast.LENGTH_SHORT).show();

                            // Chờ 1 chút rồi quay về MainActivity
                            new android.os.Handler().postDelayed(() -> {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                // Truyền dữ liệu để tự động điền username nếu muốn
                                intent.putExtra("username", username);
                                startActivity(intent);
                                finish();
                            }, 1500); // 1.5 giây
                        } else {
                            Toast.makeText(RegisterActivity.this, "Tài khoản đã tồn tại hoặc lỗi khác!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Không thể kết nối đến server", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
