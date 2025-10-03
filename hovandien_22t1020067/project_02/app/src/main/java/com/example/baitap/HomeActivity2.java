package com.example.baitap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity2 extends AppCompatActivity {

    TextView tvMessage, tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        tvMessage = findViewById(R.id.tvMessage);
        tvInfo = findViewById(R.id.tvInfo);

        // Lấy dữ liệu từ MainActivity
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        // Hiển thị thông tin
        tvInfo.setText("Tài khoản: " + username + "\nMật khẩu: " + password);
    }
}
