package com.example.project1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static String _userNameLogined;
    EditText m_edtUser, m_edtPass;
    Button m_btnLogin, m_btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        m_edtUser = (EditText) findViewById(R.id.edtUsername);
        m_edtPass = (EditText) findViewById(R.id.edtPassword);
        m_btnLogin = (Button) findViewById(R.id.btnLogin);
        m_btnRegister = (Button) findViewById(R.id.btnRegister);

        m_btnLogin.setOnClickListener(new CButtonLogin());

        m_btnRegister.setOnClickListener(new CButtonRegister());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public class CButtonLogin implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String user = m_edtUser.getText().toString();
            String pass = m_edtPass.getText().toString();
            Log.d("K46", "CLICK BUTTON LOGIN ACCOUNT " + user + "/" + pass);
            if (user.length() < 3 || pass.length() < 6) {
                Toast.makeText(getApplicationContext(), "Tài khoản hoặc mật khẩu không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                apiLogin(user, pass);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            String msg = "Đã nhập thông tin tài khoản [" + user + "]";
//            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        }
    }

    public class CButtonRegister implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(i);
        }
    }

    void doGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("K46", myResponse);
                    }
                });
            }
        });
    }

    void doPost(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("K46", response.body().string());
            }
        });
    }

    //Hàm dịch vụ Login
    void apiLogin(String user, String pass) throws IOException {
        String json = "{\"username\":\"" + user + "\",\"password\":\"" + pass + "\"}";
        Toast.makeText(getApplicationContext(), json, Toast.LENGTH_SHORT).show();
        Log.d("K46", json);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("https://dev.husc.edu.vn/tin4403/api/login")
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String errStr = "Tài khoản hoặc mật khẩu không chính xác.\n" + e.getMessage();
                Log.d("K46", "onFailure\n" + errStr);
                Toast.makeText(getApplicationContext(), errStr, Toast.LENGTH_SHORT).show();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String errStr = "Tài khoản hoặc mật khẩu không chính xác.\n" + response.body().string();
                Log.d("K46", errStr);
                if (!response.isSuccessful()) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), errStr, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(intent);

            }
        });
    }
}