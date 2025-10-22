package com.example.project_02;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Callback;


public class MainActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private EditText username, password;
    private Button submit;
    private TextView goToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // layout đăng nhập

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        submit   = findViewById(R.id.submit);
        goToRegister = findViewById(R.id.goToRegister);

        submit.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                apiLogin(user,pass);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        goToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    void apiLogin(String user, String pass) throws IOException {
        //boolean bOk = (user.equals("vvdung") && pass.equals("123456"));
        String json = "{\"username\":\"" + user + "\",\"password\":\"" + pass +"\"}";
        Toast.makeText(getApplicationContext(),json,Toast.LENGTH_SHORT).show();
        Log.d("K46",json);

        RequestBody body = RequestBody.create(json,JSON);
        Request request = new Request.Builder()
                .url("https://dev.husc.edu.vn/tin4403/api/login") //.url("http://192.168.56.1:4380/login")
//                .url("https://bach-shop-backend.onrender.com/api/v1/users/login")
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String errStr = "Tài khoản hoặc mật khẩu không chính xác.\n" + e.getMessage();
                Log.d("K46","onFailure\n" + errStr);
                Toast.makeText(getApplicationContext(),errStr,Toast.LENGTH_SHORT).show();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("LOGIN_RESPONSE", responseData);

                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(),
                                    "Đăng nhập thất bại: " + responseData, Toast.LENGTH_SHORT).show());
                    return;
                }

                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                    intent.putExtra("userResponse", responseData); // Gửi dữ liệu sang UserActivity
                    startActivity(intent);
                });
            }


        });
    }
}
