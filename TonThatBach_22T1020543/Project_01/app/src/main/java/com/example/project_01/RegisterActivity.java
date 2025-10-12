package com.example.project_01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerUsername, registerPassword, registerConfirmPassword;
    private Button registerButton;
    private TextView backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerUsername = findViewById(R.id.registerUsername);
        registerPassword = findViewById(R.id.registerPassword);
        registerConfirmPassword = findViewById(R.id.registerConfirmPassword);
        registerButton = findViewById(R.id.registerButton);
        backToLogin = findViewById(R.id.backToLogin);

        registerButton.setOnClickListener(v -> {
            String user = registerUsername.getText().toString().trim();
            String pass = registerPassword.getText().toString().trim();
            String confirm = registerConfirmPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            } else {
                // Demo: hiển thị Toast, bạn có thể lưu user vào DB / API sau này
                Toast.makeText(this, "Đăng ký thành công cho tài khoản: " + user, Toast.LENGTH_SHORT).show();
                // Sau khi đăng ký xong, quay về trang đăng nhập
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        backToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
