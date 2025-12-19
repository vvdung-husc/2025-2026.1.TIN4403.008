package com.example.project_02;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_02.ApiClient;
import com.example.project_02.Utils;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUser, edtName, edtEmail, edtPass1, edtPass2;
    Button btnCreate;
    TextView txtBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        edtUser = findViewById(R.id.edtUser);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass1 = findViewById(R.id.edtPass1);
        edtPass2 = findViewById(R.id.edtPass2);
        btnCreate = findViewById(R.id.btnCreateUser);
        txtBack = findViewById(R.id.txtBack);

        // Quay về màn hình login
        txtBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Xử lý đăng ký
        btnCreate.setOnClickListener(v -> register());

        // Xử lý insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // ===============================
    // HÀM ĐĂNG KÝ TÀI KHOẢN
    // ===============================
    void register() {
        String user = edtUser.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass1 = edtPass1.getText().toString();
        String pass2 = edtPass2.getText().toString();

        // Validate
        if (user.length() < 3) {
            toast("Tên tài khoản phải ≥ 3 ký tự");
            return;
        }

        if (pass1.length() < 6) {
            toast("Mật khẩu phải ≥ 6 ký tự");
            return;
        }

        if (!pass1.equals(pass2)) {
            toast("Mật khẩu nhập lại không khớp");
            return;
        }

        if (!email.contains("@")) {
            toast("Email không hợp lệ");
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("username", user);
            json.put("password", pass1);
            json.put("fullname", name);
            json.put("email", email);

            // Gọi API ở thread khác
            new Thread(() -> {
                ApiClient.ApiResult r =
                        ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json.toString(), null);

                runOnUiThread(() -> {
                    if (r.success) {
                        Utils.showAlert(
                                RegisterActivity.this,
                                "Thành công",
                                "Đăng ký tài khoản thành công!"
                        );
                    } else {
                        toast("Lỗi: " + r.body);
                    }
                });
            }).start();

        } catch (Exception e) {
            toast("Lỗi xử lý dữ liệu");
        }
    }

    void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}