package com.example.project__01;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project__01.ApiClient;
import com.example.project__01.R;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    EditText m_edtUser, m_edtPass, m_edtFullname, m_edtEmail;
    Button m_btnRegister;
    TextView m_txtBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        m_edtUser = findViewById(R.id.edtUsername);
        m_edtPass = findViewById(R.id.edtPassword);
        m_edtFullname = findViewById(R.id.edtFullname);
        m_edtEmail = findViewById(R.id.edtEmail);
        m_btnRegister = findViewById(R.id.btnRegister);
        m_txtBack = findViewById(R.id.txtBack);

        m_txtBack.setOnClickListener(v -> finish());

        m_btnRegister.setOnClickListener(v -> {
            try {
                JSONObject obj = new JSONObject();
                obj.put("username", m_edtUser.getText().toString());
                obj.put("password", m_edtPass.getText().toString());
                obj.put("fullname", m_edtFullname.getText().toString());
                obj.put("email", m_edtEmail.getText().toString());

                new Thread(() -> {
                    ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_REGISTER, obj.toString(), null);
                    runOnUiThread(() -> {
                        if (r.success) {
                            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}