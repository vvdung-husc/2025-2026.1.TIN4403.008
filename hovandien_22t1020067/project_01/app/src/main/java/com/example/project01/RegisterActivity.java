package com.example.project01;

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

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUser, edtName, edtPass1, edtPass2;
    Button btnRegister;
    TextView txtBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        edtUser = findViewById(R.id.edtUser);
        edtName = findViewById(R.id.edtName);
        edtPass1 = findViewById(R.id.edtPass1);
        edtPass2 = findViewById(R.id.edtPass2);
        btnRegister = findViewById(R.id.btnRegister);
        txtBack = findViewById(R.id.txtBack);

        // Sự kiện nút đăng ký
        btnRegister.setOnClickListener(new CButtonRegister());

        // Quay lại màn hình login
        txtBack.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // ===== XỬ LÝ BUTTON REGISTER =====
    public class CButtonRegister implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            String user = edtUser.getText().toString().trim();
            String name = edtName.getText().toString().trim();
            String pass1 = edtPass1.getText().toString();
            String pass2 = edtPass2.getText().toString();

            Log.d("K46", "REGISTER: " + user + "/" + name);

            // Kiểm tra username
            if (user.length() < 3) {
                Toast.makeText(getApplicationContext(),
                        "Tên tài khoản không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra fullname
            if (name.length() < 3) {
                Toast.makeText(getApplicationContext(),
                        "Họ tên không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra password
            if (pass1.length() < 6 || !pass1.equals(pass2)) {
                Toast.makeText(getApplicationContext(),
                        "Mật khẩu không hợp lệ hoặc không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                okhttpRegister(user, name, pass1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ===== HÀM GỌI API REGISTER =====
    void okhttpRegister(String user, String name, String pass) throws IOException {

        JSONObject obj = new JSONObject();
        try {
            obj.put("username", user);
            obj.put("fullname", name);
            obj.put("password", pass);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        String json = obj.toString();
        Log.d("K46", json);

        new Thread(() -> {
            ApiClient.ApiResult r =
                    ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json, null);

            runOnUiThread(() -> {
                try {
                    JSONObject o = new JSONObject(r.body);
                    int ret = o.getInt("r");
                    String msg = o.getString("m");

                    if (r.success && ret == 1) {
                        Toast.makeText(this,
                                "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        finish(); // quay về login
                    } else {
                        Toast.makeText(this,
                                msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Log.e("API", "REGISTER FAILED: " + r.body);

                    if (r.httpCode == 404)
                        Utils.showAlert(RegisterActivity.this,
                                "Lỗi dịch vụ", "API không tìm thấy - " + ApiClient.URL_USER_REGISTER);
                    else if (r.httpCode == 502)
                        Utils.showAlert(RegisterActivity.this,
                                "Lỗi dịch vụ", "Dịch vụ API không hoạt động");
                    else
                        Toast.makeText(this,
                                "Lỗi ParseJSON " + r.body, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
