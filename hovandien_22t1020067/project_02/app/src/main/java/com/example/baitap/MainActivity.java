package com.example.baitap;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();
            Log.d("K46","CLICK BUTTON LOGIN ACCOUNT " + user + "/" + pass);
            if (user.length() >= 3 || pass.length() >= 6) {
                // Đăng nhập thành công → chuyển sang HomeActivity
                Intent intent = new Intent(MainActivity.this, HomeActivity2.class);
                intent.putExtra("username", user);
                intent.putExtra("password", pass);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });



        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


    }
}
