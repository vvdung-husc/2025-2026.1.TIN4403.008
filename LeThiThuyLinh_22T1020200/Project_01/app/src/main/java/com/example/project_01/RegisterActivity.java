package com.example.project_01;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUser, edtFullName, edtPass;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Gắn các view trong XML
        edtUser = findViewById(R.id.edtUser);
        edtFullName = findViewById(R.id.edtName);
        edtPass = findViewById(R.id.edtPass1);
        btnRegister = findViewById(R.id.btnCreateUser);

        // Bắt sự kiện khi nhấn nút "Tạo mới tài khoản"
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtUser.getText().toString().trim();
                String name = edtFullName.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();

                // Kiểm tra dữ liệu nhập vào
                if (user.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pass.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thông báo thành công (tạm thời)
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công cho: " + name, Toast.LENGTH_SHORT).show();

                // Quay lại màn hình đăng nhập
                finish();
            }
        });

        // Giữ lại đoạn xử lý layout nếu cần (tùy chọn)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });
    }
}