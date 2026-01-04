package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    TextView m_txtBack;
    Button m_btnRegister;
    // Khởi tạo biến điều khiển theo ID mới trong XML
    EditText m_edtRegUsername, m_edtRegPassword, m_edtRegFullname, m_edtRegEmail, m_edtRegPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Khởi tạo View
        m_txtBack = (TextView) findViewById(R.id.txtBack);
        m_btnRegister = (Button) findViewById(R.id.btnDoRegister); // ID đã sửa trong XML

        m_edtRegUsername = (EditText) findViewById(R.id.edtRegUsername);
        m_edtRegPassword = (EditText) findViewById(R.id.edtRegPassword);
        m_edtRegPasswordConfirm = (EditText) findViewById(R.id.edtRegPasswordConfirm); // ID đã thêm
        m_edtRegFullname = (EditText) findViewById(R.id.edtRegFullname);
        m_edtRegEmail = (EditText) findViewById(R.id.edtRegEmail); // ID đã thêm

        m_txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        m_btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = m_edtRegUsername.getText().toString();
                String pass = m_edtRegPassword.getText().toString();
                String pass2 = m_edtRegPasswordConfirm.getText().toString();
                String name = m_edtRegFullname.getText().toString();
                String email = m_edtRegEmail.getText().toString();

                // Kiểm tra ràng buộc
                if (user.length() < 3 || name.isEmpty() || email.isEmpty() || email.indexOf('@') == -1) {
                    Utils.showAlert(RegisterActivity.this, "Lỗi", "Vui lòng nhập đầy đủ Tài khoản, Họ tên, và Email hợp lệ.");
                    return;
                }
                if (pass.length() < 6 || !pass.equals(pass2)) {
                    Utils.showAlert(RegisterActivity.this, "Lỗi", "Mật khẩu phải từ 6 ký tự và nhập lại phải khớp.");
                    return;
                }

                okhttpRegister(user, pass, name, email);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void okhttpRegister(String user, String pass, String name, String email) {
        String json;
        try {
            JSONObject obj = new JSONObject();
            obj.put("username", user);
            obj.put("password", pass);
            obj.put("fullname", name);
            obj.put("email", email);
            json = obj.toString();
        } catch (JSONException e) {
            Toast.makeText(this, "Lỗi tạo dữ liệu đăng ký", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json, null);

            runOnUiThread(() -> {
                try {
                    JSONObject obj = new JSONObject(r.body);
                    int ret = obj.getInt("r");
                    String msg = obj.getString("m");

                    if (r.success && ret == 1) {
                        Log.w("API", "Register OK: " + r.httpCode + " " + r.body);
                        Utils.showAlert(RegisterActivity.this, "Thành công", "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay.");

                        // Chuyển về màn hình Login
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Log.e("API", "Register ERR: " + r.httpCode + " " + r.body);
                        Utils.showAlert(RegisterActivity.this, "Lỗi Đăng ký", msg);
                    }
                } catch (JSONException e) {
                    Log.e("API", "Register FAILED: " + r.httpCode + " " + r.body);
                    Toast.makeText(this, "Lỗi ParseJSON: " + r.body, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}