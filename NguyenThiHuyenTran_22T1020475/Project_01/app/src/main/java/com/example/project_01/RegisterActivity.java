package com.example.project_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // Đảm bảo đã import EditText
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
    // 1. Khai báo các EditText tương ứng với giao diện
    EditText m_edtName, m_edtUser, m_edtPass1, m_edtPass2;
    Button m_btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // 2. Ánh xạ các biến từ Layout XML
        m_txtBack = (TextView) findViewById(R.id.txtBack);
        m_btnRegister = (Button) findViewById(R.id.btnRegister);
        m_edtName = (EditText) findViewById(R.id.edtName);
        m_edtUser = (EditText) findViewById(R.id.edtUser);
        m_edtPass1 = (EditText) findViewById(R.id.edtPass1);
        m_edtPass2 = (EditText) findViewById(R.id.edtPass2);

        m_txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 3. Viết mã xử lý cho nút Đăng ký
        m_btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegister();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Hàm xử lý logic đăng ký
    void performRegister() {
        String name = m_edtName.getText().toString().trim();
        String user = m_edtUser.getText().toString().trim();
        String pass1 = m_edtPass1.getText().toString();
        String pass2 = m_edtPass2.getText().toString();

        // Kiểm tra nhập liệu cơ bản
        if (name.isEmpty() || user.isEmpty() || pass1.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass1.equals(pass2)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng JSON và xử lý ngoại lệ JSONException
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", user);
            obj.put("password", pass1);
            obj.put("fullname", name);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Gửi dữ liệu lên Server trong Thread riêng
        new Thread(() -> {
            // Sử dụng URL_USER_REGISTER đã khai báo
            APIClient.ApiResult r = APIClient.httpPost(APIClient.URL_USER_REGISTER, obj.toString(), null);

            runOnUiThread(() -> {
                if (r.success) {
                    // Hiển thị thông báo thành công thay vì thông báo cũ
                    Utils.showAlert(RegisterActivity.this, "Thành công", "Tài khoản đã được tạo!");
                    finish(); // Quay lại màn hình đăng nhập
                } else {
                    Utils.showAlert(RegisterActivity.this, "Lỗi", "Đăng ký thất bại: " + r.body);
                }
            });
        }).start();
    }
}