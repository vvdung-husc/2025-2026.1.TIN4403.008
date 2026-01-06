package com.example.project_01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText m_edtUser, m_edtPass, m_edtFullname, m_edtEmail;
    Button m_btnRegister, m_btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // ánh xạ UI
        m_edtUser = findViewById(R.id.edtUser);
        m_edtPass = findViewById(R.id.edtPass1);
        m_edtFullname = findViewById(R.id.edtName);
        m_edtEmail = findViewById(R.id.edtEmail);

        m_btnRegister = findViewById(R.id.btnRegister);
        m_btnBackToLogin = findViewById(R.id.btnBackToLogin);

        m_btnRegister.setOnClickListener(v -> registerUser());

        m_btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void registerUser() {
        String user = m_edtUser.getText().toString().trim();
        String pass = m_edtPass.getText().toString().trim();
        String fullname = m_edtFullname.getText().toString().trim();
        String email = m_edtEmail.getText().toString().trim();

        if (user.length() < 3) {
            Toast.makeText(this, "Username phải >= 3 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "Password phải >= 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fullname.isEmpty()) {
            Toast.makeText(this, "Họ tên không được bỏ trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Email không được bỏ trống", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("username", user);
            obj.put("password", pass);
            obj.put("fullname", fullname);
            obj.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String json = obj.toString();
        Log.d("API", "REGISTER JSON: " + json);

        new Thread(() -> {
            ApiClient.ApiResult r =
                    ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json, null);

            runOnUiThread(() -> {
                try {
                    JSONObject ob = new JSONObject(r.body);
                    int ret = ob.getInt("r");
                    String msg = ob.getString("m");

                    if (r.success && ret == 1) {
                        Toast.makeText(this,
                                "Đăng ký thành công! Hãy đăng nhập.",
                                Toast.LENGTH_LONG).show();

                        m_edtUser.setText("");
                        m_edtPass.setText("");
                        m_edtFullname.setText("");
                        m_edtEmail.setText("");

                    } else {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
