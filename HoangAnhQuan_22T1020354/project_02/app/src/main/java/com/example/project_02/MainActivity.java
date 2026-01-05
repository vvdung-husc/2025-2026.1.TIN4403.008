package com.example.project_02;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // ==========================
    // CẤU HÌNH API MÁY CÁ NHÂN
    // ==========================
    private static final String URL_LOGIN = "http://192.168.1.11:4380/login";
    public static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");

    static String _userNameLogined;

    EditText m_edtUser, m_edtPass;
    Button m_btnLogin, m_btnRegister;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ánh xạ View
        m_edtUser = findViewById(R.id.edtUsername);
        m_edtPass = findViewById(R.id.edtPassword);
        m_btnLogin = findViewById(R.id.btnLogin);
        m_btnRegister = findViewById(R.id.btnRegister);

        // Sự kiện Login
        m_btnLogin.setOnClickListener(new CButtonLogin());

        // Sự kiện Register
        m_btnRegister.setOnClickListener(new CButtonRegister());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // ==========================
    // BUTTON LOGIN
    // ==========================
    public class CButtonLogin implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            String user = m_edtUser.getText().toString().trim();
            String pass = m_edtPass.getText().toString().trim();

            Log.d("LOGIN", user + "/" + pass);

            if (user.length() < 3 || pass.length() < 6) {
                Toast.makeText(MainActivity.this,
                        "Tài khoản hoặc mật khẩu không hợp lệ!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            apiLogin(user, pass);
        }
    }

    // ==========================
    // BUTTON REGISTER
    // ==========================
    public class CButtonRegister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
        }
    }

    // ==========================
    // API LOGIN
    // ==========================
    void apiLogin(String user, String pass) {

        String json = "{\"username\":\"" + user + "\",\"password\":\"" + pass + "\"}";
        Log.d("LOGIN_JSON", json);

        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(URL_LOGIN) // ✅ API máy bạn
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this,
                                "Không kết nối được server!",
                                Toast.LENGTH_LONG).show()
                );
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String resStr = response.body() != null
                        ? response.body().string()
                        : "";

                Log.d("LOGIN_RESPONSE", resStr);

                runOnUiThread(() -> {

                    if (!response.isSuccessful()) {
                        Toast.makeText(MainActivity.this,
                                "Đăng nhập thất bại!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        org.json.JSONObject obj =
                                new org.json.JSONObject(resStr);

                        int r = obj.optInt("r", 0);
                        String token = obj.optString("m", "");

                        if (r != 1 || token.isEmpty()) {
                            Toast.makeText(MainActivity.this,
                                    "Sai tài khoản hoặc mật khẩu!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Đăng nhập thành công
                        _userNameLogined = user;

                        Intent intent =
                                new Intent(MainActivity.this, UserActivity.class);
                        intent.putExtra("token", token);
                        startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this,
                                "Lỗi xử lý dữ liệu!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
