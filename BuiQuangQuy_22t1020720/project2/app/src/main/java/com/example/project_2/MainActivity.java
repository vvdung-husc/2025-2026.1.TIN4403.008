package com.example.project_2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText edtUser, edtName, edtPass1, edtPass2;
    Button btnCreateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUser = findViewById(R.id.edtUser);
        edtName = findViewById(R.id.edtName);
        edtPass1 = findViewById(R.id.edtPass1);
        edtPass2 = findViewById(R.id.edtPass2);
        btnCreateUser = findViewById(R.id.btnCreateUser);

        btnCreateUser.setOnClickListener(v -> register());
    }

    private void register() {
        String user = edtUser.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String pass1 = edtPass1.getText().toString().trim();
        String pass2 = edtPass2.getText().toString().trim();

        if (user.isEmpty() || name.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass1.equals(pass2)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("username", user);
            json.put("fullname", name);
            json.put("password", pass1);

            new Thread(() -> {
                ApiClient.ApiResult result =
                        ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json.toString(), null);

                runOnUiThread(() -> {
                    if (result.success) {
                        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_LONG).show();
                        Log.d("API", result.body);
                    } else {
                        Toast.makeText(this, "Lỗi: " + result.body, Toast.LENGTH_LONG).show();
                        Log.e("API", result.body);
                    }
                });
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
