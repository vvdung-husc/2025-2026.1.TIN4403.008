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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    // Khai báo các biến giao diện
    TextView m_txtBack;
    Button m_btnRegister;
    EditText m_edtUser, m_edtName, m_edtPass1, m_edtPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // 1. Khởi tạo các biến điều khiển tương ứng trong layout
        m_txtBack = findViewById(R.id.txtBack);
        m_btnRegister = findViewById(R.id.btnRegister);

        // Ánh xạ các trường nhập liệu (dựa trên id trong activity_register.xml)
        m_edtUser = findViewById(R.id.edtUser);     // Tên tài khoản
        m_edtName = findViewById(R.id.edtName);     // Họ và tên
        m_edtPass1 = findViewById(R.id.edtPass1);   // Mật khẩu
        m_edtPass2 = findViewById(R.id.edtPass2);   // Nhập lại mật khẩu

        // Sự kiện quay lại màn hình đăng nhập
        m_txtBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Sự kiện nhấn nút Đăng ký
        m_btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 2. Lấy dữ liệu từ EditText
                String username = m_edtUser.getText().toString().trim();
                String fullname = m_edtName.getText().toString().trim();
                String pass1 = m_edtPass1.getText().toString().trim();
                String pass2 = m_edtPass2.getText().toString().trim();

                // 3. Kiểm tra dữ liệu đầu vào (Validate)
                if (username.isEmpty() || fullname.isEmpty() || pass1.isEmpty()) {
                    Utils.showAlert(RegisterActivity.this, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin!");
                    return;
                }

                if (username.length() < 3) {
                    Utils.showAlert(RegisterActivity.this, "Cảnh báo", "Tên tài khoản phải lớn hơn 3 ký tự");
                    return;
                }

                if (pass1.length() < 6) {
                    Utils.showAlert(RegisterActivity.this, "Cảnh báo", "Mật khẩu phải lớn hơn 6 ký tự");
                    return;
                }

                if (!pass1.equals(pass2)) {
                    Utils.showAlert(RegisterActivity.this, "Cảnh báo", "Mật khẩu nhập lại không khớp!");
                    return;
                }

                // 4. Tạo JSON object để gửi đi
                JSONObject obj = new JSONObject();
                try {
                    obj.put("username", username);
                    obj.put("password", pass1);
                    obj.put("fullname", fullname);
                    // Có thể thêm email nếu API yêu cầu, nhưng layout hiện tại chưa có ô nhập email
                    // obj.put("email", "email@example.com");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String jsonBody = obj.toString();
                Log.d("API_REGISTER", "Sending JSON: " + jsonBody);

                // 5. Gọi hàm đăng ký
                registerUser(jsonBody);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Hàm thực hiện gọi API Đăng ký (Tương tự updateUserInfo bên UserActivity)
    void registerUser(String json) {
        // Chạy trên thread khác để không chặn giao diện (UI)
        new Thread(() -> {
            // Đăng ký thường không cần Token trong Header, nên để null hoặc map rỗng
            // Nếu server yêu cầu header đặc biệt, thêm vào đây
            Map<String, String> headers = null;

            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json, headers);

            // Quay lại luồng giao diện để hiển thị kết quả
            runOnUiThread(() -> {
                try {
                    // Parse kết quả trả về từ Server
                    JSONObject obj = new JSONObject(r.body);

                    // Dựa trên cấu trúc code MainActivity: r là mã lỗi, m là thông báo
                    // Lưu ý: Cần kiểm tra server trả về key gì (thường là 'r' và 'm' theo code mẫu)

                    if (r.success) {
                        // Kiểm tra logic nghiệp vụ từ server (nếu server trả về r=1 là thành công)
                        if (obj.has("r")) {
                            int ret = obj.getInt("r");
                            String msg = obj.has("m") ? obj.getString("m") : "Đăng ký thành công";

                            if (ret == 1 || r.httpCode == 200 || r.httpCode == 201) {
                                // Đăng ký thành công
                                Log.w("API", "Register OK: " + r.body);
                                Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_LONG).show();

                                // Chuyển về màn hình đăng nhập
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Server trả về lỗi nghiệp vụ (ví dụ: tài khoản đã tồn tại)
                                Utils.showAlert(RegisterActivity.this, "Đăng ký thất bại", msg);
                            }
                        } else {
                            // Trường hợp API trả về JSON khác cấu trúc mong đợi
                            Utils.showAlert(RegisterActivity.this, "Thông báo", "Phản hồi: " + r.body);
                        }

                    } else {
                        // Lỗi kết nối mạng hoặc lỗi server (500, 404...)
                        Log.e("API", "ERR: " + r.httpCode + " " + r.body);

                        String message = "Có lỗi xảy ra: " + r.body;
                        if (r.httpCode == 409) message = "Tài khoản đã tồn tại!";

                        Utils.showAlert(RegisterActivity.this, "Lỗi đăng ký (" + r.httpCode + ")", message);
                    }

                } catch (JSONException e) {
                    Log.e("API", "JSON Parse Error: " + e.getMessage());
                    // Nếu server không trả về JSON đúng chuẩn
                    Utils.showAlert(RegisterActivity.this, "Lỗi", "Không thể xử lý phản hồi từ máy chủ.\n" + r.body);
                }
            });

        }).start();
    }
}