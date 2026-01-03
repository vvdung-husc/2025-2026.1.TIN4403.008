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
    EditText m_edtUser, m_edtName, m_edtPass1, m_edtPass2;
    Button m_btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Khởi tạo các biến điều khiển tương ứng trong layout
        m_txtBack = (TextView) findViewById(R.id.txtBack);
        m_edtUser = (EditText) findViewById(R.id.edtUser);
        m_edtName = (EditText) findViewById(R.id.edtName);
        m_edtPass1 = (EditText) findViewById(R.id.edtPass1);
        m_edtPass2 = (EditText) findViewById(R.id.edtPass2);
        m_btnRegister = (Button) findViewById(R.id.btnRegister);

        m_txtBack.setOnClickListener(v -> {
            // Trở về màn hình đăng nhập
            finish();
        });

        m_btnRegister.setOnClickListener(v -> doRegister());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void doRegister() {
        String username = m_edtUser.getText().toString().trim();
        String fullname = m_edtName.getText().toString().trim();
        String pass1 = m_edtPass1.getText().toString();
        String pass2 = m_edtPass2.getText().toString();

        if (username.length() < 3) {
            Toast.makeText(getApplicationContext(), "Tên tài khoản tối thiểu 3 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fullname.length() < 3) {
            Toast.makeText(getApplicationContext(), "Họ và tên không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass1.length() < 6) {
            Toast.makeText(getApplicationContext(), "Mật khẩu tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass1.equals(pass2)) {
            Toast.makeText(getApplicationContext(), "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("username", username);
            obj.put("password", pass1);
            obj.put("fullname", fullname);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Lỗi tạo dữ liệu JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        String json = obj.toString();
        Log.d("K46", "REGISTER JSON: " + json);

        m_btnRegister.setEnabled(false);
        registerUser(json, username, pass1);
    }

    private void registerUser(String json, String username, String password) {
        new Thread(() -> {
            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json, null);

            runOnUiThread(() -> {
                m_btnRegister.setEnabled(true);

                try {
                    JSONObject obj = new JSONObject(r.body);

                    int ret = obj.has("r") ? obj.getInt("r") : -999;
                    String msg = obj.has("m") ? obj.getString("m") : r.body;

                    boolean ok = r.success && (ret == 0 || ret == 1);

                    if (ok) {
                        Utils.showAlert(RegisterActivity.this, "Thành công", msg);

                        // Phương án 1: quay lại màn đăng nhập (không auto login)
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("prefill_user", username);
                        intent.putExtra("prefill_pass", password);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Utils.showAlert(RegisterActivity.this, "Đăng ký thất bại", msg);
                    }

                } catch (JSONException e) {
                    Log.e("API", "FAILED: " + r.httpCode + " " + r.body);

                    if (r.httpCode == 404)
                        Utils.showAlert(RegisterActivity.this, "Lỗi dịch vụ", "API không tìm thấy - " + ApiClient.URL_USER_REGISTER);
                    else if (r.httpCode == 502)
                        Utils.showAlert(RegisterActivity.this, "Lỗi dịch vụ", "Dịch vụ API đang không hoạt động");
                    else if (r.body != null && r.body.contains("Failed to connect"))
                        Utils.showAlert(RegisterActivity.this, "Lỗi dịch vụ", r.body);
                    else
                        Utils.showAlert(RegisterActivity.this, "Lỗi", "Không đọc được JSON: " + r.body);
                }
            });
        }).start();
    }
}
