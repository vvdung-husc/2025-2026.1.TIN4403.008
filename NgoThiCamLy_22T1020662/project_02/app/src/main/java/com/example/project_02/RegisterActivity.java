package com.example.project_02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    EditText m_edUsername, m_edPassword, m_edConfirmPassword;
    Button m_btnCreate;
    TextView m_txtLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Ánh xạ view đúng với XML
        m_edUsername = findViewById(R.id.id_User);
        m_edPassword = findViewById(R.id.id_Pass1);
        m_edConfirmPassword = findViewById(R.id.id_Pass2);
        m_btnCreate = findViewById(R.id.id_CreateUser);
        m_txtLoginLink = findViewById(R.id.id_Back);

        // Xử lý nút "Tạo tài khoản mới"
        m_btnCreate.setOnClickListener(v -> {
            String username = m_edUsername.getText().toString().trim();
            String password = m_edPassword.getText().toString().trim();
            String confirm = m_edConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Xử lý khi nhấn “Đã có tài khoản? Đăng nhập.”
        m_txtLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
