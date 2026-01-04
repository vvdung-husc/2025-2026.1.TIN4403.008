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
    TextView m_txtBack;
    Button m_btnRegister;
    EditText m_edtUser, m_edtName, m_edtPass1, m_edtPass2; // Thêm các biến nhập liệu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // 1. Khởi tạo các biến điều khiển (Ánh xạ từ XML)
        m_txtBack = findViewById(R.id.txtBack);
        m_btnRegister = findViewById(R.id.btnRegister);
        m_edtUser = findViewById(R.id.edtUser);
        m_edtName = findViewById(R.id.edtName);
        m_edtPass1 = findViewById(R.id.edtPass1);
        m_edtPass2 = findViewById(R.id.edtPass2);

        // 2. Nút quay lại
        m_txtBack.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

        // 3. Nút Đăng ký
        m_btnRegister.setOnClickListener(v -> {
            performRegister();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void performRegister() {
        String user = m_edtUser.getText().toString().trim();
        String name = m_edtName.getText().toString().trim(); // Dùng làm email hoặc fullname tùy server
        String pass1 = m_edtPass1.getText().toString().trim();
        String pass2 = m_edtPass2.getText().toString().trim();

        // Kiểm tra hợp lệ
        if (user.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass1.equals(pass2)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo JSON để gửi lên Server
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", user);
            obj.put("password", pass1);
            obj.put("fullname", null);
            obj.put("email", "null"); // Truyền thông tin email/tên vào đây
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = obj.toString();

        // Chạy Thread để gọi API
        new Thread(() -> {
            // Gọi đến URL_REGISTER mà mình đã thêm trong ApiClient
            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json, null);

            runOnUiThread(() -> {
                try {
                    JSONObject resObj = new JSONObject(r.body);
                    int ret = resObj.getInt("r");
                    String msg = resObj.getString("m");

                    if (r.success && ret == 1) {
                        Utils.showAlert(RegisterActivity.this, "Thành công", "Tài khoản đã được tạo!");
                        // Đăng ký xong thì quay lại màn hình Login sau khi bấm OK
                    } else {
                        Utils.showAlert(RegisterActivity.this, "Lỗi", msg);
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "Lỗi phản hồi từ Server", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}