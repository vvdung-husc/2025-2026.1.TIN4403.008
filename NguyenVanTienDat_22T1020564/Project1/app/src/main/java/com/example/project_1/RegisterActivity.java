package com.example.project_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUser, edtName, edtPass1, edtPass2;
    Button btnRegister;
    TextView txtBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        edtUser = findViewById(R.id.edtUser);
        edtName = findViewById(R.id.edtName);
        edtPass1 = findViewById(R.id.edtPass1);
        edtPass2 = findViewById(R.id.edtPass2);
        btnRegister = findViewById(R.id.btnRegister);
        txtBack = findViewById(R.id.txtBack);

        txtBack.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> doRegister());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void doRegister() {
        String user = edtUser.getText().toString().trim();
        String fullname = edtName.getText().toString().trim();
        String pass1 = edtPass1.getText().toString();
        String pass2 = edtPass2.getText().toString();

        if (user.length() < 3) {
            Toast.makeText(this, "Tài khoản không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fullname.length() < 3) {
            Toast.makeText(this, "Họ tên không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass1.length() < 6 || !pass1.equals(pass2)) {
            Toast.makeText(this, "Mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("username", user);
            obj.put("password", pass1);
            obj.put("fullname", fullname);
        } catch (JSONException e) { return; }

        String json = obj.toString();
        Log.d("K46", "REGISTER " + json);

        new Thread(() -> {
            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_REGISTER, json, null);

            runOnUiThread(() -> {
                try {
                    JSONObject o = new JSONObject(r.body);
                    int ret = o.getInt("r");
                    String msg = o.getString("m");

                    if (ret > 0) {
                        Utils.showAlert(RegisterActivity.this, "Thành công", msg);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "Lỗi ParseJSON", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
