package com.tiencut.app1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUser, edtName, edtPass1, edtPass2;
    Button btnCreateUser;
    TextView txtBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUser = findViewById(R.id.edtUser);
        edtName = findViewById(R.id.edtName);
        edtPass1 = findViewById(R.id.edtPass1);
        edtPass2 = findViewById(R.id.edtPass2);
        btnCreateUser = findViewById(R.id.btnCreateUser);
        txtBack = findViewById(R.id.txtBack);

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUser.getText().toString();
                String fullname = edtName.getText().toString();
                String password = edtPass1.getText().toString();
                String confirmPassword = edtPass2.getText().toString();

                if (username.isEmpty() || fullname.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                } else {
                    // Logic đăng ký tài khoản ở đây
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    // Chuyển về màn hình đăng nhập sau khi đăng ký thành công
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}