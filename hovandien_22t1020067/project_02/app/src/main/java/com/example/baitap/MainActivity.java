package com.example.baitap;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvRegister;

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Nếu vừa đăng ký xong thì điền lại username cho tiện
        if (getIntent().hasExtra("username")) {
            etUsername.setText(getIntent().getStringExtra("username"));
        }

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API đăng nhập
            loginUser(user, pass);
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
    private void loginUser(String username, String password) {
        new Thread(() -> {
            try {
                RequestBody body = new okhttp3.FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();

                Request request = new Request.Builder()
                        .url("http://192.168.88.124:4380/login")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {

                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);

                    int r = json.optInt("r", 0);
                    String token = json.optString("token", "");

                    runOnUiThread(() -> {
                        if (r == 1 && !token.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, HomeActivity2.class);
                            intent.putExtra("token", token);        // ← GỬI TOKEN ĐI
                            startActivity(intent);

                        } else {
                            Toast.makeText(MainActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    });
//    private void loginUser(String username, String password) {
//        new Thread(() -> {
//            try {
//                RequestBody body = new okhttp3.FormBody.Builder()
//                        .add("username", username)
//                        .add("password", password)
//                        .build();
//
//                Request request = new Request.Builder()
//                        .url("http://192.168.88.124:4380/login")//https://dev.husc.edu.vn/tin4403/api/login
//                        .post(body)
//                        .build();
//
//                try (Response response = client.newCall(request).execute()) {
//                    String responseData = response.body().string();
//
//                    JSONObject jsonResponse = new JSONObject(responseData);
//
//                    int r = jsonResponse.optInt("r", 0);
//                    String token = jsonResponse.optString("token", "");
//
//                    boolean finalOk = (r == 1 && !token.isEmpty());
//
//                    runOnUiThread(() -> {
//                        if (finalOk) {
//                            Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(MainActivity.this, HomeActivity2.class));
//                        } else {
//                            Toast.makeText(MainActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
//                        }
//                    });

//                    JSONObject jsonResponse = new JSONObject(responseData);
//
//                    int r = jsonResponse.optInt("r", 0);
//                    String token = jsonResponse.optString("token", "");
//
//                    runOnUiThread(() -> {
//                        if (r == 1 && !token.isEmpty()) {
//                            Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
//
//                            Intent intent = new Intent(MainActivity.this, HomeActivity2.class);
//                            intent.putExtra("token", token);
//                            startActivity(intent);
//
//                        } else {
//                            Toast.makeText(MainActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}
