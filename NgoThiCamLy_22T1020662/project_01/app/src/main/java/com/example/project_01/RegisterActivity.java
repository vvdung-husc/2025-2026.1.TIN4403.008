package com.example.project_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Xử lý hiển thị an toàn
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Nút "Tạo mới tài khoản"
        Button btnTaoMoiTK = findViewById(R.id.id_TaoMoiTK);
        btnTaoMoiTK.setOnClickListener(v -> {
            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
        });

        // 👉 TextView "Đã có tài khoản? Đăng nhập"
        TextView txtDaCoTK = findViewById(R.id.id_DaCoTK);
        txtDaCoTK.setOnClickListener(v -> {
            // Chuyển về MainActivity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng RegisterActivity để không quay lại khi nhấn nút Back
        });
    }
}
