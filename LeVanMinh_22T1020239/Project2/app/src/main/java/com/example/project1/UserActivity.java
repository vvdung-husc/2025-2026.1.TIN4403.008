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

import com.example.project1.ApiClient;
import com.example.project1.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    TextView m_txtFullname, m_txtEmail;
    EditText m_edtNewFullname, m_edtNewEmail, m_edtPassword1, m_edtPassword2;
    Button m_btnLogout, m_btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        m_txtFullname = findViewById(R.id.txtFullname);
        m_txtEmail = findViewById(R.id.txtEmail);

        m_edtNewFullname = findViewById(R.id.edtNewFullname);
        m_edtNewEmail = findViewById(R.id.edtNewEmail);
        m_edtPassword1 = findViewById(R.id.edtPassword1);
        m_edtPassword2 = findViewById(R.id.edtPassword2);

        m_btnUpdate = findViewById(R.id.btnUpdate);
        m_btnLogout = findViewById(R.id.btnLogout);

        getUserInfo();

        m_btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), com.example.project1.MainActivity.class));
            finish();
        });

        m_btnUpdate.setOnClickListener(v -> updateInfo());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void updateInfo() {
        String fullname = m_edtNewFullname.getText().toString().trim();
        String email = m_edtNewEmail.getText().toString().trim();
        String pass1 = m_edtPassword1.getText().toString();
        String pass2 = m_edtPassword2.getText().toString();

        if (fullname.isEmpty() && email.isEmpty() && pass1.isEmpty()) {
            Toast.makeText(this, "Phải nhập ít nhất 1 thông tin để cập nhật", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject obj = new JSONObject();

        try {
            if (!fullname.isEmpty())
                obj.put("fullname", fullname);

            if (!email.isEmpty()) {
                if (!email.contains("@")) {
                    Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                obj.put("email", email);
            }

            if (!pass1.isEmpty()) {
                if (pass1.length() < 6 || !pass1.equals(pass2)) {
                    Toast.makeText(this, "Mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                obj.put("password", pass1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateUserInfo(obj.toString());
    }

    void getUserInfo() {
        new Thread(() -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("token", com.example.project1.MainActivity._token);

            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_INFO, null, headers);

            runOnUiThread(() -> {
                try {
                    JSONObject obj = new JSONObject(r.body);
                    JSONObject m = obj.getJSONObject("m");

                    m_txtFullname.setText("Xin chào: " + m.optString("fullname", "Chưa có"));
                    m_txtEmail.setText("Email: " + m.optString("email", "Chưa có"));

                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi tải thông tin", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    void updateUserInfo(String json) {
        new Thread(() -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("token", com.example.project1.MainActivity._token);

            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_UPDATE, json, headers);

            runOnUiThread(() -> {
                try {
                    JSONObject obj = new JSONObject(r.body);
                    int ret = obj.getInt("r");
                    String msg = obj.getString("m");

                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                    if (ret > 0) {
                        getUserInfo();
                        m_edtNewFullname.setText("");
                        m_edtNewEmail.setText("");
                        m_edtPassword1.setText("");
                        m_edtPassword2.setText("");
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}