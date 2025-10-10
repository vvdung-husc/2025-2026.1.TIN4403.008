package com.example.baitap;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etRegUsername, etRegPassword, etRegConfirmPassword, etRegEmail;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        etRegEmail = findViewById(R.id.etRegEmail);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String username = etRegUsername.getText().toString();
            String password = etRegPassword.getText().toString();
            String confirmPass = etRegConfirmPassword.getText().toString();
            String email = etRegEmail.getText().toString();

            if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đăng ký thành công cho tài khoản: " + username, Toast.LENGTH_LONG).show();
                finish(); // Quay lại trang đăng nhập
            }
        });
    }
}
