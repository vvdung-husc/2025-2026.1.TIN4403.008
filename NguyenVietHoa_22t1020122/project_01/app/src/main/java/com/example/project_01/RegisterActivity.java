package com.example.project_01;

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
    // Khai báo các biến điều khiển dựa trên layout
    EditText m_edtUser, m_edtName, m_edtPass1, m_edtPass2;
    Button m_btnRegister;
    TextView m_txtBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // 1. Khởi tạo (Ánh xạ) các biến điều khiển từ layout
        m_edtUser = findViewById(R.id.edtUser);
        m_edtName = findViewById(R.id.edtName);
        m_edtPass1 = findViewById(R.id.edtPass1);
        m_edtPass2 = findViewById(R.id.edtPass2);
        m_btnRegister = findViewById(R.id.btnRegister);
        m_txtBack = findViewById(R.id.txtBack);

        // 2. Sự kiện Click để quay lại màn hình Login
        m_txtBack.setOnClickListener(v -> {
            finish(); // Đóng RegisterActivity để quay về MainActivity
        });

        // 3. Sự kiện Click cho nút Đăng ký
        m_btnRegister.setOnClickListener(v -> {
            performRegistration();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void performRegistration() {
        String user = m_edtUser.getText().toString().trim();
        String name = m_edtName.getText().toString().trim();
        String pass1 = m_edtPass1.getText().toString().trim();
        String pass2 = m_edtPass2.getText().toString().trim();

        // Kiểm tra tính hợp lệ của dữ liệu đầu vào
        if (user.isEmpty() || name.isEmpty() || pass1.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass1.equals(pass2)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo JSON object để gửi lên server
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("username", user);
            jsonParams.put("fullname", name);
            jsonParams.put("password", pass1);

            String jsonString = jsonParams.toString();
            okhttpRegister(jsonString);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void okhttpRegister(String json) {
        new Thread(() -> {
            // Sử dụng URL_USER_REGISTER đã định nghĩa trong ApiClient
            // Chú ý sử dụng ApiClient.ApiResult để tránh lỗi "Cannot access"
            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json, null);

            runOnUiThread(() -> {
                try {
                    JSONObject obj = new JSONObject(r.body);
                    int ret = obj.getInt("r"); // Mã lỗi từ server (0 thường là thành công)
                    String msg = obj.getString("m"); // Thông báo từ server

                    if (r.success && ret == 0) {
                        Utils.showAlert(RegisterActivity.this, "Thành công", "Đăng ký tài khoản thành công!");
                        // Sau khi đăng ký thành công có thể chuyển về màn hình đăng nhập
                        // finish();
                    } else {
                        Utils.showAlert(RegisterActivity.this, "Thất bại", msg);
                    }
                } catch (JSONException e) {
                    Log.e("API_REGISTER", "Error parsing JSON: " + r.body);
                    Toast.makeText(this, "Lỗi hệ thống: " + r.httpCode, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}