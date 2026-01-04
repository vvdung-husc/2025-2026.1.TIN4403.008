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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    // TextViews hiển thị thông tin
    TextView m_tvDisplayUsername, m_tvDisplayFullname, m_tvDisplayEmail;
    // EditTexts để nhập thông tin cập nhật
    EditText m_edtNewEmail, m_edtPassword1, m_edtPassword2;
    Button m_btnLogout, m_btnUpdate;

    private String currentToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        currentToken = Utils.getToken(this);
        if (currentToken.isEmpty()) {
            Toast.makeText(this, "Phiên hết hạn, vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // 1. Khởi tạo View (KHẮC PHỤC LỖI ID)
        // TextViews để hiển thị thông tin người dùng
        m_tvDisplayUsername = (TextView)findViewById(R.id.tvDisplayUsername);
        m_tvDisplayFullname = (TextView)findViewById(R.id.tvDisplayFullname);
        m_tvDisplayEmail = (TextView)findViewById(R.id.tvDisplayEmail);

        // EditTexts để nhập thông tin cập nhật
        m_edtNewEmail = (EditText) findViewById(R.id.edtNewEmail);
        m_edtPassword1 = (EditText) findViewById(R.id.edtPassword1);
        m_edtPassword2 = (EditText) findViewById(R.id.edtPassword2);

        // Buttons
        m_btnLogout = (Button) findViewById(R.id.btnLogout);
        m_btnUpdate = (Button) findViewById(R.id.btnUpdate);

        // 2. Lấy thông tin người dùng
        getUserInfo();

        // 3. Xử lý Đăng xuất
        m_btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Utils.clearToken(getApplicationContext());
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 4. Xử lý Cập nhật
        m_btnUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String newEmail = m_edtNewEmail.getText().toString();
                String pass = m_edtPassword1.getText().toString();
                String pass2 = m_edtPassword2.getText().toString();

                if (newEmail.isEmpty() && pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập Email hoặc Mật khẩu mới để cập nhật.", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject obj = new JSONObject();
                boolean dataValid = true;

                if (!newEmail.isEmpty()){
                    if (newEmail.indexOf('@') == -1) {
                        Toast.makeText(getApplicationContext(), "Địa chỉ email không hợp lệ", Toast.LENGTH_SHORT).show();
                        dataValid = false;
                    }
                    try {
                        obj.put("email", newEmail);
                    } catch (JSONException e) { /* Bỏ qua */ }
                }

                if (!pass.isEmpty()){
                    if (pass.length() < 6 || !pass.equals(pass2)) {
                        Toast.makeText(getApplicationContext(), "Mật khẩu mới phải từ 6 ký tự và nhập lại phải khớp.", Toast.LENGTH_SHORT).show();
                        dataValid = false;
                    }
                    try {
                        obj.put("password", pass);
                    } catch (JSONException e) { /* Bỏ qua */ }
                }

                if (dataValid) {
                    updateUserInfo(obj.toString());
                }
            }
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
            headers.put("token", currentToken);

            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_INFO,null, headers);

            runOnUiThread(() -> {
                try {
                    JSONObject obj = new JSONObject(r.body);
                    int ret = obj.getInt("r");

                    if (r.success && ret == 1) {
                        JSONObject m = obj.getJSONObject("m");
                        String username = m.getString("username");
                        String fullname = m.optString("fullname", "Chưa cập nhật");
                        String szEmail = m.optString("email", "Chưa cập nhật");

                        m_tvDisplayUsername.setText(username);
                        m_tvDisplayFullname.setText(fullname);
                        m_tvDisplayEmail.setText(szEmail);

                    } else {
                        String msg = obj.optString("m", "Lỗi lấy thông tin");
                        Toast.makeText(this, "Lỗi: " + msg, Toast.LENGTH_SHORT).show();
                        if (ret == -3) { // Token hết hạn
                            Utils.clearToken(this);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    void updateUserInfo(String json) {
        new Thread(() -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("token", currentToken);

            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_UPDATE, json, headers);

            runOnUiThread(() -> {
                try {
                    JSONObject obj = new JSONObject(r.body);
                    int ret = obj.getInt("r");
                    String msg = obj.getString("m");

                    if (r.success && ret == 1) {
                        Utils.showAlert(UserActivity.this,"Thành công","Cập nhật thành công!");

                        getUserInfo(); // Cập nhật lại giao diện

                        // Xóa nội dung trong các ô EditText
                        m_edtNewEmail.setText("");
                        m_edtPassword1.setText("");
                        m_edtPassword2.setText("");
                    } else {
                        Utils.showAlert(UserActivity.this,"Lỗi Cập nhật",msg);

                        if (ret == -3) { // Token hết hạn
                            Utils.clearToken(this);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "Lỗi phân tích dữ liệu hoặc kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}