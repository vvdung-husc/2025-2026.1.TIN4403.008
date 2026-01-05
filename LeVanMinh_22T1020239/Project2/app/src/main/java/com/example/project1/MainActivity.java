package com.example.project1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText m_edtUsername;
    private EditText m_edtPassword;
    private Button m_btnLogin;
    private Button m_btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_edtUsername = findViewById(R.id.edtUsername);
        m_edtPassword = findViewById(R.id.edtPassword);
        m_btnLogin = findViewById(R.id.btnLogin);
        m_btnRegister = findViewById(R.id.btnRegister);

        m_btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoginTask().execute();
            }
        });

        m_btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private class LoginTask extends AsyncTask<Void, Void, ApiClient.ApiResult> {

        private String username;
        private String password;

        @Override
        protected void onPreExecute() {
            username = m_edtUsername.getText().toString().trim();
            password = m_edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu.", Toast.LENGTH_SHORT).show();
                cancel(true);
            }
        }

        @Override
        protected ApiClient.ApiResult doInBackground(Void... voids) {
            if (isCancelled()) {
                return null;
            }

            try {
                JSONObject jsonPayload = new JSONObject();
                jsonPayload.put("username", username);
                jsonPayload.put("password", password);
                return ApiClient.httpPost(ApiClient.URL_LOGIN, jsonPayload.toString(), null);

            } catch (JSONException e) {
                return new ApiClient.ApiResult(false, "Lỗi tạo dữ liệu JSON: " + e.getMessage(), 0);
            }
        }

        @Override
        protected void onPostExecute(ApiClient.ApiResult result) {
            if (result == null) return;

            if (result.success && result.httpCode == 200) {
                try {
                    JSONObject jsonResponse = new JSONObject(result.body);
                    String status = jsonResponse.optString("status");
                    String token = jsonResponse.optString("token");
                    String msg = jsonResponse.optString("msg", "Đăng nhập thành công!");

                    if ("success".equals(status) && token != null) {
                        Utils.saveAuthToken(MainActivity.this, token);

                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(MainActivity.this, UserActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Lỗi phân tích phản hồi server.", Toast.LENGTH_LONG).show();
                }

            } else {
                String errorMessage = "Đăng nhập thất bại. Code: " + result.httpCode;
                if (result.body != null && !result.body.isEmpty()) {
                    try {
                        JSONObject jsonError = new JSONObject(result.body);
                        errorMessage = jsonError.optString("msg", errorMessage);
                    } catch (JSONException ignored) {}
                }
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}