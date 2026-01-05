package com.example.project1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    // ... (Các khai báo biến khác giữ nguyên) ...
    private TextView m_tvTitle;
    private TextView m_tvDisplayUsername;
    private TextView m_tvDisplayFullname;
    private TextView m_tvDisplayEmail;
    private EditText m_edtNewEmail;
    private EditText m_edtPassword1;
    private EditText m_edtPassword2;
    private Button m_btnUpdate;
    private Button m_btnLogout;

    private String currentToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Ánh xạ ID giữ nguyên
        m_tvTitle = findViewById(R.id.tvUserTitle);
        m_tvDisplayUsername = findViewById(R.id.tvDisplayUsername);
        m_tvDisplayFullname = findViewById(R.id.tvDisplayFullname);
        m_tvDisplayEmail = findViewById(R.id.tvDisplayEmail);

        m_edtNewEmail = findViewById(R.id.edtNewEmail);
        m_edtPassword1 = findViewById(R.id.edtPassword1);
        m_edtPassword2 = findViewById(R.id.edtPassword2);

        m_btnUpdate = findViewById(R.id.btnUpdate);
        m_btnLogout = findViewById(R.id.btnLogout);

        currentToken = Utils.getAuthToken(this);
        if (currentToken == null || currentToken.isEmpty()) {
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserActivity.this, MainActivity.class));
            finish();
            return;
        }

        new GetUserInfoTask().execute();

        m_btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra mật khẩu không khớp trước khi gọi AsyncTask
                String newPassword = m_edtPassword1.getText().toString().trim();
                String confirmPassword = m_edtPassword2.getText().toString().trim();

                if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
                    Toast.makeText(UserActivity.this, "Mật khẩu mới và xác nhận không khớp.", Toast.LENGTH_LONG).show();
                    return; // Ngăn không cho chạy AsyncTask
                }
                new UpdateUserTask().execute();
            }
        });

        m_btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.clearAuthToken(UserActivity.this);
                Toast.makeText(UserActivity.this, "Đã đăng xuất.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UserActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    // ... (GetUserInfoTask giữ nguyên) ...
    private class GetUserInfoTask extends AsyncTask<Void, Void, ApiClient.ApiResult> {

        @Override
        protected ApiClient.ApiResult doInBackground(Void... voids) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + currentToken);

            return ApiClient.httpPost(ApiClient.URL_USER_INFO, null, headers);
        }

        @Override
        protected void onPostExecute(ApiClient.ApiResult result) {
            if (result.success && result.httpCode == 200) {
                try {
                    JSONObject jsonResponse = new JSONObject(result.body);
                    String status = jsonResponse.optString("status");

                    if ("success".equals(status)) {
                        JSONObject data = jsonResponse.optJSONObject("data");
                        if (data != null) {
                            String username = data.optString("username", "N/A");
                            String fullname = data.optString("fullname", "N/A");
                            String email = data.optString("email", "N/A");

                            m_tvDisplayUsername.setText(username);
                            m_tvDisplayFullname.setText(fullname);
                            m_tvDisplayEmail.setText(email);
                            // Giữ lại email cũ trong ô nhập mới, tiện cho người dùng chỉ muốn đổi mật khẩu
                            m_edtNewEmail.setText(email);
                        }
                    } else {
                        String msg = jsonResponse.optString("msg", "Lỗi khi tải thông tin.");
                        Toast.makeText(UserActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(UserActivity.this, "Lỗi phân tích dữ liệu người dùng.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(UserActivity.this, "Tải thông tin thất bại. Code: " + result.httpCode, Toast.LENGTH_LONG).show();
            }
        }
    }

    // UpdateUserTask đã sửa để không cần publishProgress
    private class UpdateUserTask extends AsyncTask<Void, Void, ApiClient.ApiResult> {

        // Loại bỏ kiểm tra mật khẩu ở đây và di chuyển lên onClick
        @Override
        protected ApiClient.ApiResult doInBackground(Void... voids) {
            String newEmail = m_edtNewEmail.getText().toString().trim();
            String newPassword = m_edtPassword1.getText().toString().trim();

            try {
                JSONObject jsonPayload = new JSONObject();
                jsonPayload.put("email", newEmail);

                if (!newPassword.isEmpty()) {
                    jsonPayload.put("new_password", newPassword);
                }

                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + currentToken);

                return ApiClient.httpPost(ApiClient.URL_USER_UPDATE, jsonPayload.toString(), headers);

            } catch (JSONException e) {
                return new ApiClient.ApiResult(false, "Lỗi tạo dữ liệu JSON: " + e.getMessage(), 0);
            }
        }

        // Đã xóa onProgressUpdate vì không còn cần thiết

        @Override
        protected void onPostExecute(ApiClient.ApiResult result) {
            if (result == null) return;

            m_edtPassword1.setText("");
            m_edtPassword2.setText("");

            if (result.success && result.httpCode == 200) {
                try {
                    JSONObject jsonResponse = new JSONObject(result.body);
                    String status = jsonResponse.optString("status");
                    String msg = jsonResponse.optString("msg", "Cập nhật thành công!");

                    Toast.makeText(UserActivity.this, msg, Toast.LENGTH_LONG).show();

                    if ("success".equals(status)) {
                        new GetUserInfoTask().execute();
                    }
                } catch (JSONException e) {
                    Toast.makeText(UserActivity.this, "Lỗi phân tích phản hồi server.", Toast.LENGTH_LONG).show();
                }
            } else {
                String errorMessage = "Cập nhật thất bại. Code: " + result.httpCode;
                Toast.makeText(UserActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}