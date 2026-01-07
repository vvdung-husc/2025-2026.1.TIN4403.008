package com.example.project_02;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText edtFullName, edtEmail, edtUsername, edtPassword, edtRePassword;
    Button btnRegister;
    TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // ====== MAP ID ĐÚNG VỚI XML ======
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> register());

        // ⚠️ đổi main → registerLayout
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.registerLayout),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                }
        );
    }

    void register() {
        String fullname = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String pass1 = edtPassword.getText().toString();
        String pass2 = edtRePassword.getText().toString();

        // ===== VALIDATE =====
        if (username.length() < 3) {
            toast("Tên đăng nhập phải ≥ 3 ký tự");
            return;
        }

        if (pass1.length() < 6) {
            toast("Mật khẩu phải ≥ 6 ký tự");
            return;
        }

        if (!pass1.equals(pass2)) {
            toast("Mật khẩu nhập lại không khớp");
            return;
        }

        if (!email.isEmpty() && !email.contains("@")) {
            toast("Email không hợp lệ");
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", pass1);
            json.put("fullname", fullname);
            json.put("email", email);

            new Thread(() -> {
                ApiClient.ApiResult r =
                        ApiClient.httpPost(
                                ApiClient.URL_USER_REGISTER,
                                json.toString(),
                                null
                        );

                runOnUiThread(() -> {
                    if (r.success) {
                        Utils.showAlert(
                                RegisterActivity.this,
                                "Thành công",
                                "Đăng ký tài khoản thành công!"
                        );
                    } else {
                        toast("Lỗi: " + r.body);
                    }
                });
            }).start();

        } catch (Exception e) {
            toast("Lỗi xử lý dữ liệu");
        }
    }

    void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
