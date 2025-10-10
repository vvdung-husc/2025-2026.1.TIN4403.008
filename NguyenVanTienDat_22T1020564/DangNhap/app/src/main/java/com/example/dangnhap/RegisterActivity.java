package com.example.dangnhap;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText edtNewUser, edtNewPass, edtConfirmPass;
    Button btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtNewUser = findViewById(R.id.edtNewUser);
        edtNewPass = findViewById(R.id.edtNewPass);
        edtConfirmPass = findViewById(R.id.edtConfirmPass);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(v -> {
            String user = edtNewUser.getText().toString().trim();
            String pass = edtNewPass.getText().toString().trim();
            String confirm = edtConfirmPass.getText().toString().trim();

            if(user.isEmpty() || pass.isEmpty() || confirm.isEmpty()){
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else if(!pass.equals(confirm)){
                Toast.makeText(this, "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đăng ký thành công cho user: " + user, Toast.LENGTH_LONG).show();
                finish(); // quay về MainActivity
            }
        });
    }
}
