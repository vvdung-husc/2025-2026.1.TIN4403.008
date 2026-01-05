package com.example.project1;

import android.content.Context;
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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    // Khắc phục lỗi 'Field can be converted to a local variable' bằng cách xóa m_tvTitle khỏi đây
    // private TextView m_tvTitle;

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

        // Ánh xạ ID
        // m_tvTitle = findViewById(R.id.tvUserTitle); // m_tvTitle chỉ dùng để khai báo
        m_tvDisplayUsername = findViewById(R.id.tvDisplayUsername);
        m_tvDisplayFullname = findViewById(R.id.tvDisplayFullname);
        m_tvDisplayEmail = findViewById(R.id.tvDisplayEmail);

        m_edtNewEmail = findViewById(R.id.edtNewEmail);
        m_edtPassword1 = findViewById(R.id.edtPassword1);
        m_edtPassword2 = findViewById(R.id.edtPassword2);

        m_btnUpdate = findViewById(R.id.btnUpdate);
        m_btnLogout = findViewById(R.id.btnLogout);

        // Sửa lỗi logic: Lấy token và kiểm tra ngay. currentToken không được khởi tạo mặc định là null.
        currentToken = Utils.getAuthToken(this);
        if (currentToken == null || currentToken.isEmpty()) {
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Khởi chạy AsyncTask với tham chiếu Activity yếu
        new GetUserInfoTask(this).execute();

        // Sử dụng Lambda expressions (Sửa lỗi phong cách)
        m_btnUpdate.setOnClickListener(v -> {
            String newPassword = m_edtPassword1.getText().toString().trim();
            String confirmPassword = m_edtPassword2.getText().toString().trim();

            if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
                Toast.makeText(UserActivity.this, "Mật khẩu mới và xác nhận không khớp.", Toast.LENGTH_LONG).show();
                return;
            }
            new UpdateUserTask(this, currentToken).execute();
        });

        // Sử dụng Lambda expressions (Sửa lỗi phong cách)
        m_btnLogout.setOnClickListener(v -> {
            Utils.clearAuthToken(UserActivity.this);
            Toast.makeText(UserActivity.this, "Đã đăng xuất.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserActivity.this, MainActivity.class));
            finish();
        });
    }

    // ====================================================================================
    // SỬA LỖI MEMORY LEAK: Chuyển AsyncTask thành Static class và sử dụng WeakReference
    // ====================================================================================

    private static class GetUserInfoTask extends AsyncTask<Void, Void, ApiClient.ApiResult> {
        // Sử dụng WeakReference để tránh rò rỉ bộ nhớ
        private WeakReference<UserActivity> activityWeakReference;
        private String token;

        GetUserInfoTask(UserActivity context) {
            activityWeakReference = new WeakReference<>(context);
            this.token = context.currentToken; // Lấy token từ Activity
        }

        @Override
        protected ApiClient.ApiResult doInBackground(Void... voids) {
            if (token == null || token.isEmpty()) {
                return new ApiClient.ApiResult(false, "Token rỗng.", 0);
            }
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + token);

            return ApiClient.httpPost(ApiClient.URL_USER_INFO, null, headers);
        }

        @Override
        protected void onPostExecute(ApiClient.ApiResult result) {
            UserActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing() || result == null) return;

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

                            activity.m_tvDisplayUsername.setText(username);
                            activity.m_tvDisplayFullname.setText(fullname);
                            activity.m_tvDisplayEmail.setText(email);
                            activity.m_edtNewEmail.setText(email);
                        }
                    } else {
                        String msg = jsonResponse.optString("msg", "Lỗi khi tải thông tin.");
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, "Lỗi phân tích dữ liệu người dùng.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(activity, "Tải thông tin thất bại. Code: " + result.httpCode, Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class UpdateUserTask extends AsyncTask<Void, Void, ApiClient.ApiResult> {

        private WeakReference<UserActivity> activityWeakReference;
        private String token;

        UpdateUserTask(UserActivity context, String currentToken) {
            activityWeakReference = new WeakReference<>(context);
            this.token = currentToken;
        }

        @Override
        protected ApiClient.ApiResult doInBackground(Void... voids) {
            UserActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) return null;

            String newEmail = activity.m_edtNewEmail.getText().toString().trim();
            String newPassword = activity.m_edtPassword1.getText().toString().trim();

            try {
                JSONObject jsonPayload = new JSONObject();
                jsonPayload.put("email", newEmail);

                if (!newPassword.isEmpty()) {
                    jsonPayload.put("new_password", newPassword);
                }

                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);

                return ApiClient.httpPost(ApiClient.URL_USER_UPDATE, jsonPayload.toString(), headers);

            } catch (JSONException e) {
                return new ApiClient.ApiResult(false, "Lỗi tạo dữ liệu JSON: " + e.getMessage(), 0);
            }
        }

        @Override
        protected void onPostExecute(ApiClient.ApiResult result) {
            UserActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing() || result == null) return;

            // Xóa trường mật khẩu sau khi cập nhật
            activity.m_edtPassword1.setText("");
            activity.m_edtPassword2.setText("");

            if (result.success && result.httpCode == 200) {
                try {
                    JSONObject jsonResponse = new JSONObject(result.body);
                    String status = jsonResponse.optString("status");
                    String msg = jsonResponse.optString("msg", "Cập nhật thành công!");

                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();

                    if ("success".equals(status)) {
                        // Gọi lại AsyncTask để tải lại thông tin người dùng
                        new GetUserInfoTask(activity).execute();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, "Lỗi phân tích phản hồi server.", Toast.LENGTH_LONG).show();
                }
            } else {
                String errorMessage = "Cập nhật thất bại. Code: " + result.httpCode;
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}