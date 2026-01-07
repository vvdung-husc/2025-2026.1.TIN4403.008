package com.example.project_01;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    TextView m_txtBack;
    Button m_btnRegister;
    EditText m_edtUser, m_edtName, m_edtPass1, m_edtPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ ID (Giữ nguyên theo yêu cầu của bạn)
        m_txtBack = findViewById(R.id.textView4);
        m_btnRegister = findViewById(R.id.btnRegister);
        m_edtUser = findViewById(R.id.edtUser);
        m_edtName = findViewById(R.id.edtName);
        m_edtPass1 = findViewById(R.id.edtPass1);
        m_edtPass2 = findViewById(R.id.edtPass2);

        if (m_txtBack != null) m_txtBack.setOnClickListener(v -> finish());

        if (m_btnRegister != null) {
            m_btnRegister.setOnClickListener(v -> {
                String user = m_edtUser.getText().toString().trim();
                String name = m_edtName.getText().toString().trim();
                String pass1 = m_edtPass1.getText().toString().trim();
                String pass2 = m_edtPass2.getText().toString().trim();

                if (user.isEmpty() || pass1.length() < 6) {
                    Utils.showAlert(this, "Thông báo", "Tài khoản không được để trống và mật khẩu >= 6 ký tự!");
                    return;
                }
                if (!pass1.equals(pass2)) {
                    Utils.showAlert(this, "Thông báo", "Mật khẩu nhập lại không khớp!");
                    return;
                }

                // Gọi hàm xử lý
                execRegister(user, pass1, name);
            });
        }
    }

    private void execRegister(String user, String pass, String name) {
        new Thread(() -> {
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", user);
                jsonBody.put("password", pass);
                jsonBody.put("fullname", name);
                jsonBody.put("email", "");

                // Gọi API với đường dẫn đầy đủ để sửa lỗi "Cannot access"
                com.example.project_01.ApiClient.ApiResult res = com.example.project_01.ApiClient.httpPost(
                        com.example.project_01.ApiClient.URL_USER_REGISTER,
                        jsonBody.toString(),
                        null
                );

                runOnUiThread(() -> {
                    if (res != null && res.success) {
                        try {
                            JSONObject resObj = new JSONObject(res.body);
                            Toast.makeText(RegisterActivity.this, resObj.getString("m"), Toast.LENGTH_SHORT).show();
                            if (resObj.getInt("r") > 0) finish();
                        } catch (Exception e) { e.printStackTrace(); }
                    } else {
                        String msg = (res != null && res.body != null) ? res.body : "Lỗi kết nối máy chủ";
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
}