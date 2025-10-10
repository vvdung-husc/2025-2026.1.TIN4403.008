package com.example.project_01;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText edtNhapTaiKhoan = findViewById(R.id.id_TaiKhoan);
        EditText edtNhapMatKhau = findViewById(R.id.id_MatKhau);
        Button btnDangNhap = findViewById(R.id.id_DangNhap);
        Button btnDangKy = findViewById(R.id.id_DangKi);

        // ✅ Xử lý nút "Đăng nhập"
        btnDangNhap.setOnClickListener(v -> {
            String taiKhoan = edtNhapTaiKhoan.getText().toString().trim();
            String matKhau = edtNhapMatKhau.getText().toString().trim();

            if (taiKhoan.isEmpty() || matKhau.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else if (taiKhoan.equals("admin") && matKhau.equals("123")) {
                Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                // 👉 Chuyển sang màn hình UserActivity
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Xử lý nút "Đăng ký"
        btnDangKy.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
