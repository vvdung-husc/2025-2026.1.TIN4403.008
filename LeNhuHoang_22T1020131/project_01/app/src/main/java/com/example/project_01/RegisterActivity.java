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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    EditText edtUser, edtName, edtPass1, edtPass2;
    Button btnCreateUser;
    TextView txtBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Liên kết các View
        edtUser = findViewById(R.id.edtUser);
        edtName = findViewById(R.id.edtName);
        edtPass1 = findViewById(R.id.edtPass1);
        edtPass2 = findViewById(R.id.edtPass2);
        btnCreateUser = findViewById(R.id.btnCreateUser);
        txtBack = findViewById(R.id.txtBack);

        // Khi nhấn "Tạo mới tài khoản"
        btnCreateUser.setOnClickListener(v -> {
            String user = edtUser.getText().toString().trim();
            String name = edtName.getText().toString().trim();
            String pass1 = edtPass1.getText().toString();
            String pass2 = edtPass2.getText().toString();

            if (user.isEmpty() || name.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass1.equals(pass2)) {
                Toast.makeText(this, "Mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                apiRegister(user, name, pass1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Khi nhấn "Đã có tài khoản? Đăng nhập."
        txtBack.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Hàm gọi API đăng ký
    void apiRegister(String user, String name, String pass) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String json = "{\"username\":\"" + user + "\",\"fullname\":\"" + name + "\",\"password\":\"" + pass + "\"}";
        Log.d("REGISTER_JSON", json);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("https://dev.husc.edu.vn/tin4403/api/register")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Lỗi kết nối máy chủ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("REGISTER_RESPONSE", responseBody);

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        // Quay lại màn hình đăng nhập
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Đăng ký thất bại: " + responseBody, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
