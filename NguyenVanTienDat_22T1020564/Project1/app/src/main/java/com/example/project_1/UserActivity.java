package com.example.project_1;

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

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    TextView m_txtFullname, m_txtEmail;
    EditText m_edtNewEmail, m_edtPassword1, m_edtPassword2, m_edtNewFullname;
    Button m_btnLogout, m_btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        // Khởi tạo điều khiển
        m_txtFullname = findViewById(R.id.txtFullname);
        m_txtEmail = findViewById(R.id.txtEmail);
        m_btnLogout = findViewById(R.id.btnLogout);

        m_edtNewEmail = findViewById(R.id.edtNewEmail);
        m_edtPassword1 = findViewById(R.id.edtPassword1);
        m_edtPassword2 = findViewById(R.id.edtPassword2);
        m_edtNewFullname = findViewById(R.id.edtNewname2);
        m_btnUpdate = findViewById(R.id.btnUpdate);

        // Load info
        getUserInfo();

        m_btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        m_btnUpdate.setOnClickListener(v -> {
            String email = m_edtNewEmail.getText().toString().trim();
            String pass = m_edtPassword1.getText().toString();
            String pass2 = m_edtPassword2.getText().toString();
            String fullname = m_edtNewFullname.getText().toString().trim();

            if (email.isEmpty() && pass.isEmpty() && fullname.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Phải nhập Email, Password hoặc Fullname để cập nhật", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject obj = new JSONObject();

            if (!email.isEmpty()) {
                if (!email.contains("@")) {
                    Toast.makeText(getApplicationContext(), "Địa chỉ email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                try { obj.put("email", email); } catch (JSONException e) { throw new RuntimeException(e); }
            }

            if (!fullname.isEmpty()) {
                if (fullname.length() < 3) {
                    Toast.makeText(getApplicationContext(), "Fullname không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                try { obj.put("fullname", fullname); } catch (JSONException e) { throw new RuntimeException(e); }
            }

            if (!pass.isEmpty()) {
                if (pass.length() < 6 || pass2.isEmpty() || !pass.equals(pass2)) {
                    Toast.makeText(getApplicationContext(), "Mật khẩu thay đổi không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                try { obj.put("password", pass); } catch (JSONException e) { throw new RuntimeException(e); }
            }

            String json = obj.toString();
            Log.d("K46", "CLICK BUTTON UPDATE " + json);
            updateUserInfo(json);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void getUserInfo(){
        new Thread(() -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("token", MainActivity._token);

            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_INFO, null, headers);

            runOnUiThread(() -> {
                if (r.success) {
                    try {
                        JSONObject obj = new JSONObject(r.body);
                        JSONObject m = obj.getJSONObject("m");

                        String fullname = m.has("fullname") ? m.getString("fullname") : "<Chưa có fullname>";
                        String szEmail = m.has("email") ? m.getString("email") : "<Chưa có email>";

                        m_txtFullname.setText("Chào mừng tài khoản : " + fullname);
                        m_txtEmail.setText("Địa chỉ thư điện tử : " + szEmail);

                    } catch (JSONException e) {
                        Toast.makeText(this, "Lỗi ParseJSON", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "ERR: " + r.body, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    void updateUserInfo(String json) {
        new Thread(() -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("token", MainActivity._token);

            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_UPDATE, json, headers);

            runOnUiThread(() -> {
                try {
                    JSONObject obj = new JSONObject(r.body);
                    int ret = obj.getInt("r");
                    String msg = obj.getString("m");

                    if (r.success) {
                        Utils.showAlert(UserActivity.this,"Thành công",msg);
                        getUserInfo();

                        m_edtNewEmail.setText("");
                        m_edtPassword1.setText("");
                        m_edtPassword2.setText("");
                        m_edtNewFullname.setText("");

                    } else {
                        Toast.makeText(this, "ERR: " + r.body, Toast.LENGTH_SHORT).show();
                        if (ret == -3) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "Lỗi ParseJSON", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
