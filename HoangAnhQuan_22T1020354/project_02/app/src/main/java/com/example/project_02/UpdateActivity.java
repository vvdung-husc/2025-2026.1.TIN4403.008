package com.example.project_02;

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

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateActivity extends AppCompatActivity {

    private static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");

    // 🔹 API máy cá nhân
    private static final String BASE_URL = "http://192.168.1.11:8080";

    EditText edtFullname, edtEmail, edtPassword;
    Button btnUpdate, btnCancel;

    String token; // Token đăng nhập

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        // Nhận token
        token = getIntent().getStringExtra("token");
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy token!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnUpdate.setOnClickListener(v -> {
            try {
                updateUserInfo();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Lỗi: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /** 🔹 Gọi API cập nhật thông tin người dùng **/
    private void updateUserInfo() throws Exception {
        String fullname = edtFullname.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        JSONObject json = new JSONObject();

        // Chỉ gửi các trường có dữ liệu
        if (!fullname.isEmpty()) json.put("fullname", fullname);
        if (!email.isEmpty()) json.put("email", email);
        if (!password.isEmpty()) json.put("password", password);

        if (json.length() == 0) {
            Toast.makeText(this,
                    "Vui lòng nhập ít nhất một thông tin để cập nhật!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json.toString(), JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/userupdate")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Log.d("API_UPDATE", "URL: " + request.url());
        Log.d("API_UPDATE", "DATA: " + json);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(),
                                "Không kết nối được server!",
                                Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bodyStr = response.body() != null
                        ? response.body().string()
                        : "";

                Log.d("API_UPDATE", "HTTP " + response.code());
                Log.d("API_UPDATE", "RESPONSE: " + bodyStr);

                runOnUiThread(() -> {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                "Cập nhật thất bại!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        JSONObject res = new JSONObject(bodyStr);
                        int r = res.optInt("r", 1);
                        String msg = res.optString("m", "OK");

                        if (r == 1) {
                            Toast.makeText(getApplicationContext(),
                                    "✅ Cập nhật thành công!",
                                    Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(getApplicationContext(), UserActivity.class);
                            i.putExtra("token", token);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Cập nhật thất bại: " + msg,
                                    Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Lỗi đọc phản hồi!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
