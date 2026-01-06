package com.example.project__01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project__01.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static String _token;
    EditText m_edtUser, m_edtPass;
    Button m_btnLogin, m_btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_edtUser = findViewById(R.id.edtUsername);
        m_edtPass = findViewById(R.id.edtPassword);
        m_btnLogin = findViewById(R.id.btnLogin);
        m_btnRegister = findViewById(R.id.btnRegister);

        m_btnLogin.setOnClickListener(v -> {
            String user = m_edtUser.getText().toString();
            String pass = m_edtPass.getText().toString();
            if (user.length() < 3 || pass.length() < 6) {
                Toast.makeText(this, "Thông tin không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
            login(user, pass);
        });

        m_btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.project__01.RegisterActivity.class));
        });
    }

    void login(String user, String pass) {
        String json = "{\"username\":\"" + user + "\",\"password\":\"" + pass + "\"}";
        new Thread(() -> {
            com.example.project__01.ApiClient.ApiResult r = com.example.project__01.ApiClient.httpPost(com.example.project__01.ApiClient.URL_LOGIN, json, null);
            runOnUiThread(() -> {
                try {
                    JSONObject obj = new JSONObject(r.body);
                    if (r.success) {
                        _token = obj.getString("m");
                        startActivity(new Intent(this, com.example.project__01.UserActivity.class));
                    } else {
                        Toast.makeText(this, "Lỗi: " + obj.getString("m"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    com.example.project__01.Utils.showAlert(this, "Lỗi", "Không thể kết nối Server");
                }
            });
        }).start();
    }
}