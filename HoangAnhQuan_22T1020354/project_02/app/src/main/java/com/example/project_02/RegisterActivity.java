package com.example.project_02;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // Các thành phần giao diện
    EditText edtFullName, edtEmail, edtUsername, edtPassword, edtRePassword;
    Button btnRegister;
    TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Liên kết với XML
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Quay lại đăng nhập
        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        // Nút đăng ký
        btnRegister.setOnClickListener(v -> {
            String fullName = edtFullName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();
            String pass1 = edtPassword.getText().toString();
            String pass2 = edtRePassword.getText().toString();

            // 🧩 Kiểm tra dữ liệu nhập
            if (fullName.isEmpty() || username.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.length() < 3) {
                Toast.makeText(this, "Tên đăng nhập phải có ít nhất 3 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pass1.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass1.equals(pass2)) {
                Toast.makeText(this, "Mật khẩu nhập lại không trùng khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🧩 Gọi API đăng ký
            try {
                apiRegister(username, fullName, email, pass1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Căn chỉnh giao diện cho vừa màn hình
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 🧩 Hàm gọi API đăng ký người dùng
    void apiRegister(String username, String fullname, String email, String password) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Chuẩn bị dữ liệu JSON
        String json = "{\"username\":\"" + username + "\"," +
                "\"fullname\":\"" + fullname + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"" + password + "\"}";

        Log.d("REGISTER_JSON", json);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("https://dev.husc.edu.vn/tin4403/api/register")
                .post(body)
                .build();

        // Gửi request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(),
                                "Không thể kết nối máy chủ: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("REGISTER_RESPONSE", responseBody);

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                "Đăng ký thành công! Vui lòng đăng nhập.",
                                Toast.LENGTH_SHORT).show();

                        // Quay lại trang đăng nhập
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("username", username); // Gửi sẵn tên đăng nhập
                        startActivity(intent);
                        finish();
                    } else {
                        String msg = "Đăng ký thất bại!";
                        if (responseBody.contains("exists")) {
                            msg = "Tên tài khoản hoặc email đã tồn tại!";
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
