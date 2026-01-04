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

    // ==========================
    // API MÁY CÁ NHÂN
    // ==========================
    private static final String URL_REGISTER =
            "http://192.168.1.11:4380/register";

    public static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");

    // Giao diện
    EditText edtFullName, edtEmail, edtUsername, edtPassword, edtRePassword;
    Button btnRegister;
    TextView tvBackToLogin;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Ánh xạ View
        edtFullName     = findViewById(R.id.edtFullName);
        edtEmail        = findViewById(R.id.edtEmail);
        edtUsername     = findViewById(R.id.edtUsername);
        edtPassword     = findViewById(R.id.edtPassword);
        edtRePassword   = findViewById(R.id.edtRePassword);
        btnRegister     = findViewById(R.id.btnRegister);
        tvBackToLogin   = findViewById(R.id.tvBackToLogin);

        // Quay về Login
        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });

        // Đăng ký
        btnRegister.setOnClickListener(v -> {

            String fullName = edtFullName.getText().toString().trim();
            String email    = edtEmail.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();
            String pass1    = edtPassword.getText().toString();
            String pass2    = edtRePassword.getText().toString();

            // ===== Validate =====
            if (fullName.isEmpty() || username.isEmpty()
                    || pass1.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(this,
                        "Vui lòng nhập đầy đủ thông tin!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.length() < 3) {
                Toast.makeText(this,
                        "Tên đăng nhập ít nhất 3 ký tự!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (pass1.length() < 6) {
                Toast.makeText(this,
                        "Mật khẩu ít nhất 6 ký tự!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass1.equals(pass2)) {
                Toast.makeText(this,
                        "Mật khẩu nhập lại không khớp!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.isEmpty()
                    && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this,
                        "Email không hợp lệ!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            apiRegister(username, fullName, email, pass1);
        });

        // Căn lề màn hình
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.registerLayout),
                (v, insets) -> {
                    Insets systemBars =
                            insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });
    }

    // ==========================
    // API REGISTER
    // ==========================
    void apiRegister(String username,
                     String fullname,
                     String email,
                     String password) {

        String json = "{"
                + "\"username\":\"" + username + "\","
                + "\"fullname\":\"" + fullname + "\","
                + "\"email\":\"" + email + "\","
                + "\"password\":\"" + password + "\""
                + "}";

        Log.d("REGISTER_JSON", json);

        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(URL_REGISTER) // ✅ API máy bạn
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(RegisterActivity.this,
                                "Không kết nối được server!",
                                Toast.LENGTH_LONG).show()
                );
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String resStr = response.body() != null
                        ? response.body().string()
                        : "";

                Log.d("REGISTER_RESPONSE", resStr);

                runOnUiThread(() -> {

                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,
                                "Đăng ký thành công! Vui lòng đăng nhập.",
                                Toast.LENGTH_SHORT).show();

                        Intent i =
                                new Intent(RegisterActivity.this,
                                        MainActivity.class);
                        i.putExtra("username", username);
                        startActivity(i);
                        finish();
                        return;
                    }

                    // Đăng ký thất bại
                    String msg = "Đăng ký thất bại!";
                    if (resStr.contains("exists")) {
                        msg = "Tài khoản hoặc email đã tồn tại!";
                    }

                    Toast.makeText(RegisterActivity.this,
                            msg,
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
