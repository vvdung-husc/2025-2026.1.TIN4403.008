package com.example.project__01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project__01.ApiClient;
import com.example.project__01.MainActivity;
import com.example.project__01.R;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    TextView m_txtFullname, m_txtEmail;
    EditText m_edtNewEmail, m_edtPass1, m_edtPass2;
    Button m_btnLogout, m_btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        m_txtFullname = findViewById(R.id.txtFullname);
        m_txtEmail = findViewById(R.id.txtEmail);
        m_edtNewEmail = findViewById(R.id.edtNewEmail);
        m_edtPass1 = findViewById(R.id.edtPassword1);
        m_edtPass2 = findViewById(R.id.edtPassword2);
        m_btnLogout = findViewById(R.id.btnLogout);
        m_btnUpdate = findViewById(R.id.btnUpdate);

        getUserInfo();

        m_btnLogout.setOnClickListener(v -> {
            MainActivity._token = null;
            finish();
        });

        m_btnUpdate.setOnClickListener(v -> {
            updateInfo();
        });
    }

    void getUserInfo() {
        new Thread(() -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("token", MainActivity._token);
            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_INFO, null, headers);
            runOnUiThread(() -> {
                if (r.success) {
                    try {
                        JSONObject m = new JSONObject(r.body).getJSONObject("m");
                        m_txtFullname.setText("Chào mừng: " + m.optString("fullname", "User"));
                        m_txtEmail.setText("Email: " + m.optString("email", "N/A"));
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        }).start();
    }

    void updateInfo() {
        try {
            JSONObject obj = new JSONObject();
            String email = m_edtNewEmail.getText().toString();
            if (!email.isEmpty()) obj.put("email", email);

            String p1 = m_edtPass1.getText().toString();
            String p2 = m_edtPass2.getText().toString();
            if (!p1.isEmpty() && p1.equals(p2)) obj.put("password", p1);

            new Thread(() -> {
                Map<String, String> headers = new HashMap<>();
                headers.put("token", MainActivity._token);
                ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_UPDATE, obj.toString(), headers);
                runOnUiThread(() -> {
                    if (r.success) {
                        com.example.project__01.Utils.showAlert(this, "Thành công", "Đã cập nhật dữ liệu");
                        getUserInfo();
                    }
                });
            }).start();
        } catch (Exception e) { e.printStackTrace(); }
    }
}