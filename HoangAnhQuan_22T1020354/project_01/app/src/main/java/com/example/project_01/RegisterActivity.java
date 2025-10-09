package com.example.project_01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText edtFullName, edtUser, edtPass, edtRePass;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtFullName = findViewById(R.id.edtFullName);
        edtUser     = findViewById(R.id.edtUser);
        edtPass     = findViewById(R.id.edtPass);
        edtRePass   = findViewById(R.id.edtRePass);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterClick();
            }
        });
    }

    private void onRegisterClick() {
        String fullName = edtFullName.getText().toString().trim();
        String user     = edtUser.getText().toString().trim();
        String pass     = edtPass.getText().toString().trim();
        String rePass   = edtRePass.getText().toString().trim();

        Log.d("K46", "CLICK BUTTON REGISTER " + user + "/" + pass);

        // Kiểm tra hợp lệ
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Họ tên không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (user.length() < 3) {
            Toast.makeText(this, "Tài khoản phải >= 3 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải >= 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(rePass)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu hợp lệ thì coi như đăng ký thành công
        String msg = "Đăng ký thành công: [" + user + "]";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        // Có thể quay lại màn hình Login kèm dữ liệu
        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
        i.putExtra("username", user);
        startActivity(i);
        finish();
    }
}
