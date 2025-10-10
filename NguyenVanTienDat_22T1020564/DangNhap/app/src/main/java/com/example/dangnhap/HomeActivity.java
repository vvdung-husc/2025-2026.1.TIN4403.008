package com.example.dangnhap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    TextView txtWelcome;
    EditText edtUserHome, edtPassHome;
    Button btnLoginHome, btnInfo, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtWelcome = findViewById(R.id.txtWelcome);
        edtUserHome = findViewById(R.id.edtUserHome);
        edtPassHome = findViewById(R.id.edtPassHome);
        btnLoginHome = findViewById(R.id.btnLoginHome);
        btnInfo = findViewById(R.id.btnInfo);
        btnLogout = findViewById(R.id.btnLogout);

        String username = getIntent().getStringExtra("username");
        if(username != null){
            txtWelcome.setText("Xin chào, " + username + "!");
        }

        // Đăng nhập tại Home
        btnLoginHome.setOnClickListener(v -> {
            String user = edtUserHome.getText().toString().trim();
            String pass = edtPassHome.getText().toString().trim();
            if(user.equals("admin") && pass.equals("123")){
                Toast.makeText(this, "Đăng nhập Home thành công!", Toast.LENGTH_SHORT).show();
                txtWelcome.setText("Xin chào, " + user + "!");
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xem thông tin
        btnInfo.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Thông tin")
                    .setMessage("Tên đăng nhập hiện tại: " + txtWelcome.getText().toString())
                    .setPositiveButton("OK", null)
                    .show();
        });

        // Đăng xuất
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có muốn đăng xuất không?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }
}
