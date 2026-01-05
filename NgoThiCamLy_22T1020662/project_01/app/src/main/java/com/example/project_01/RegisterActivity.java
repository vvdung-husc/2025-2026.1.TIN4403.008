package com.example.project_01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUser, edtName, edtPass1, edtPass2;
    Button btnRegister;
    TextView txtBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. Ánh xạ view
        edtUser = findViewById(R.id.edtUser);
        edtName = findViewById(R.id.edtName);
        edtPass1 = findViewById(R.id.edtPass1);
        edtPass2 = findViewById(R.id.edtPass2);
        btnRegister = findViewById(R.id.btnRegister);
        txtBack = findViewById(R.id.txtBack);

        // 2. Quay lại màn hình Login
        txtBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // 3. Xử lý đăng ký
        btnRegister.setOnClickListener(v -> doRegister());
    }

    // ==========================
    // HÀM ĐĂNG KÝ
    // ==========================
    void doRegister() {
        String username = edtUser.getText().toString().trim();
        String fullname = edtName.getText().toString().trim();
        String pass1 = edtPass1.getText().toString();
        String pass2 = edtPass2.getText().toString();

        // 1. Validate dữ liệu
        if (username.length() < 3) {
            Utils.showAlert(this, "Lỗi", "Tên tài khoản tối thiểu 3 ký tự");
            return;
        }

        if (pass1.length() < 6) {
            Utils.showAlert(this, "Lỗi", "Mật khẩu tối thiểu 6 ký tự");
            return;
        }

        if (!pass1.equals(pass2)) {
            Utils.showAlert(this, "Lỗi", "Mật khẩu nhập lại không khớp");
            return;
        }

        // 2. Tạo JSON gửi lên API
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", username);
            obj.put("password", pass1);
            obj.put("fullname", fullname);
        } catch (JSONException e) {
            Utils.showAlert(this, "Lỗi", "Không tạo được JSON");
            return;
        }

        String json = obj.toString();
        Log.d("REGISTER", json);

        // 3. Gửi API (THREAD RIÊNG)
        new Thread(() -> {
            ApiClient.ApiResult r =
                    ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json, null);

            runOnUiThread(() -> {
                try {
                    JSONObject res = new JSONObject(r.body);
                    int ret = res.getInt("r");
                    String msg = res.getString("m");

                    if (r.success) {
                        Utils.showAlert(this, "Thành công", msg);

                        // Sau khi đăng ký thành công → quay về Login
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Utils.showAlert(this, "Thất bại", msg);
                    }

                } catch (JSONException e) {
                    Utils.showAlert(this, "Lỗi", "Phản hồi server không hợp lệ");
                }
            });
        }).start();
    }
}
