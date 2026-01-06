package com.example.project1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText m_edtUsername;
    private EditText m_edtPassword;
    private EditText m_edtConfirmPassword;
    private EditText m_edtFullname;
    private EditText m_edtEmail;
    private Button m_btnConfirm;
    private Button m_btnBack;

    // Pattern đơn giản để kiểm tra định dạng email
    private static final Pattern EMAIL_ADDRESS =
            Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ ID - Đã được chuẩn hóa
        m_edtUsername = findViewById(R.id.edtRegUsername);
        m_edtPassword = findViewById(R.id.edtRegPassword);
        m_edtConfirmPassword = findViewById(R.id.edtRegConfirmPassword);
        m_edtFullname = findViewById(R.id.edtRegFullname);
        m_edtEmail = findViewById(R.id.edtRegEmail);
        m_btnConfirm = findViewById(R.id.btnRegConfirm);
        m_btnBack = findViewById(R.id.btnRegBack);

        m_btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RegisterTask().execute();
            }
        });

        m_btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean isDataValid(String username, String password, String confirmPassword, String fullname, String email) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullname.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ tất cả các trường.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Định dạng Email không hợp lệ.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private class RegisterTask extends AsyncTask<Void, Void, ApiClient.ApiResult> {

        private String username, password, fullname, email;

        @Override
        protected void onPreExecute() {
            username = m_edtUsername.getText().toString().trim();
            password = m_edtPassword.getText().toString().trim();
            String confirmPassword = m_edtConfirmPassword.getText().toString().trim();
            fullname = m_edtFullname.getText().toString().trim();
            email = m_edtEmail.getText().toString().trim();

            if (!isDataValid(username, password, confirmPassword, fullname, email)) {
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
                jsonPayload.put("fullname", fullname);
                jsonPayload.put("email", email);

                return ApiClient.httpPost(ApiClient.URL_USER_REGISTER, jsonPayload.toString(), null);

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
                    String msg = jsonResponse.optString("msg", "Đăng ký thành công!");

                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();

                    if ("success".equals(status)) {
                        finish();
                    }
                } catch (JSONException e) {
                    Toast.makeText(RegisterActivity.this, "Lỗi phân tích phản hồi server.", Toast.LENGTH_LONG).show();
                }

            } else {
                String errorMessage = "Đăng ký thất bại. Code: " + result.httpCode;
                if (result.body != null && !result.body.isEmpty()) {
                    try {
                        JSONObject jsonError = new JSONObject(result.body);
                        errorMessage = jsonError.optString("msg", errorMessage);
                    } catch (JSONException ignored) {}
                }
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}