package com.example.project_01;

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

public class RegisterActivity extends AppCompatActivity {

    EditText m_edtUser, m_edtPass, m_edtFullname;
    Button m_btnRegister, m_btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // ánh xạ UI
        m_edtUser = findViewById(R.id.edtUser);
        m_edtPass = findViewById(R.id.edtPass1);
        m_edtFullname = findViewById(R.id.edtName);

        m_btnRegister = findViewById(R.id.btnRegister);
        m_btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // sự kiện nút đăng ký
        m_btnRegister.setOnClickListener(v -> registerUser());

        // sự kiện nút quay lại đăng nhập
        m_btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish(); // optional, to close register activity
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void registerUser() {
        String user = m_edtUser.getText().toString().trim();
        String pass = m_edtPass.getText().toString().trim();
        String fullname = m_edtFullname.getText().toString().trim();

        // validations
        if (user.length() < 3) {
            Toast.makeText(RegisterActivity.this, "Username phải >= 3 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password phải >= 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fullname.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Họ tên không được bỏ trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // tạo JSON gửi API
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", user);
            obj.put("password", pass);
            obj.put("fullname", fullname);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        String json = obj.toString();
        Log.d("API", "REGISTER JSON: " + json);

        // chạy API bằng thread khác
        new Thread(() -> {
            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json, null);

            runOnUiThread(() -> {
                try {
                    JSONObject ob = new JSONObject(r.body);

                    int ret = ob.getInt("r");
                    String msg = ob.getString("m");

                    if (r.success && ret == 1) {
                        Utils.showAlert(RegisterActivity.this,
                                "Thành công",
                                "Đăng ký thành công, hãy đăng nhập!");

                        // clear form
                        m_edtUser.setText("");
                        m_edtPass.setText("");
                        m_edtFullname.setText("");

                    } else {
                        Toast.makeText(RegisterActivity.this, "Lỗi: " + msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(RegisterActivity.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
                }
            });

        }).start();
    }
}
