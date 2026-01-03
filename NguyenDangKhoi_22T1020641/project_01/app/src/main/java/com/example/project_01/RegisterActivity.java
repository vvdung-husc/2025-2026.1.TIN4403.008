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

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    TextView m_txtBack;
    EditText m_edtUser, m_edtName, m_edtPass1, m_edtPass2;
    Button m_btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Bind views
        m_txtBack = (TextView) findViewById(R.id.txtBack);
        m_edtUser = (EditText) findViewById(R.id.edtUser);
        m_edtName = (EditText) findViewById(R.id.edtName);
        m_edtPass1 = (EditText) findViewById(R.id.edtPass1);
        m_edtPass2 = (EditText) findViewById(R.id.edtPass2);
        m_btnRegister = (Button) findViewById(R.id.btnRegister);

        // Back to login
        m_txtBack.setOnClickListener(v -> finish());

        // Register
        m_btnRegister.setOnClickListener(v -> doRegister());
    }

    // =========================
    // REGISTER LOGIC
    // =========================
    private void doRegister() {

        String username = m_edtUser.getText().toString().trim();
        String fullname = m_edtName.getText().toString().trim();
        String pass1 = m_edtPass1.getText().toString();
        String pass2 = m_edtPass2.getText().toString();

        if (username.length() < 3) {
            Toast.makeText(this, "Tên tài khoản tối thiểu 3 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fullname.length() < 3) {
            Toast.makeText(this, "Họ và tên không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass1.length() < 6) {
            Toast.makeText(this, "Mật khẩu tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass1.equals(pass2)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject obj = new JSONObject();
            obj.put("username", username);
            obj.put("password", pass1);
            obj.put("fullname", fullname);

            registerUser(obj.toString(), username, pass1);

        } catch (JSONException e) {
            Toast.makeText(this, "Lỗi tạo JSON", Toast.LENGTH_SHORT).show();
        }
    }

    // =========================
    // CALL API
    // =========================
    private void registerUser(String json, String username, String password) {

        m_btnRegister.setEnabled(false);

        new Thread(() -> {
            ApiClient.ApiResult r =
                    ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json, null);

            runOnUiThread(() -> {
                m_btnRegister.setEnabled(true);

                try {
                    JSONObject obj = new JSONObject(r.body);
                    int ret = obj.getInt("r");
                    String msg = obj.getString("m");

                    if (r.success && ret == 0) {
                        Utils.showAlert(this, "Thành công", msg);

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("prefill_user", username);
                        i.putExtra("prefill_pass", password);
                        startActivity(i);
                        finish();
                    } else {
                        Utils.showAlert(this, "Thất bại", msg);
                    }

                } catch (Exception e) {
                    Log.e("REGISTER", r.body);
                    Utils.showAlert(this, "Lỗi", "Không kết nối được API");
                }
            });
        }).start();
    }
}