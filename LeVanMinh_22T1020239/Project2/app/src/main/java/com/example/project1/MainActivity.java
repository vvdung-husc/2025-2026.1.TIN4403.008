package com.example.project1;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    // KHÔNG SỬ DỤNG static String _token; nữa. Dùng Utils.getToken().
    EditText m_edtUser, m_edtPass;
    Button m_btnLogin, m_btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. KIỂM TRA TRẠNG THÁI ĐĂNG NHẬP
        if (!Utils.getToken(this).isEmpty()) {
            Log.d("K46", "Token đã tồn tại, chuyển thẳng sang UserActivity.");
            Intent i = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(i);
            finish(); // Đóng Activity Login
            return;
        }

        // 2. Khởi tạo View
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

            if (user.length() < 3 || pass.length() < 6) {
                Toast.makeText(getApplicationContext(), "Tài khoản hoặc mật khẩu không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                okhttpLogin(user, pass);
            } catch (IOException e) {
                // Xử lý lỗi IOException (rất hiếm)
                Toast.makeText(getApplicationContext(), "Lỗi khởi tạo đăng nhập: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public class CButtonRegister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(i);
        }
    }

    void okhttpLogin(String user, String pass) throws IOException {
        String json = "{\"username\":\"" + user + "\",\"password\":\"" + pass +"\"}";
        Log.d("K46", "Login JSON: " + json);

        new Thread(() -> {
            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_LOGIN, json, null);

            runOnUiThread(() -> {
                try {
                    JSONObject obj = new JSONObject(r.body);
                    int ret = obj.getInt("r");
                    String msg = obj.getString("m");

                    if (r.success && ret == 1) { // Đăng nhập thành công
                        Log.w("API", "Login OK: " + r.httpCode + " " + r.body);

                        // LƯU TOKEN
                        Utils.saveToken(getApplicationContext(), msg); // msg chứa token

                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        // CHUYỂN MÀN HÌNH
                        Intent i = new Intent(getApplicationContext(), UserActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else {
                        // Đăng nhập thất bại (ret=0)
                        Log.e("API", "Login ERR: " + r.httpCode + " " + r.body);
                        Toast.makeText(this, "Đăng nhập thất bại: " + msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("API", "Login FAILED: " + r.httpCode + " " + r.body);

                    if (r.httpCode == 404)
                        Utils.showAlert(MainActivity.this,"Lỗi dịch vụ","API không tìm thấy - " + ApiClient.URL_LOGIN);
                    else if (r.httpCode == 502)
                        Utils.showAlert(MainActivity.this,"Lỗi dịch vụ","Dịch vụ API đang không hoạt động");
                    else
                        Toast.makeText(this, "Lỗi ParseJSON " + r.body, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}