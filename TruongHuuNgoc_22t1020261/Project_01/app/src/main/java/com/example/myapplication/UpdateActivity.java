package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    EditText m_edtFullname, m_edtPass, m_edtEmail;
    Button m_btnSave, m_btnBack;

    private static final String TAG = "UpdateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        m_edtFullname = (EditText) findViewById(R.id.edtFullname);
        m_edtPass = (EditText) findViewById(R.id.edtPass);
        m_edtEmail = (EditText) findViewById(R.id.edtEmail);
        m_btnSave = (Button) findViewById(R.id.btnSave);
        m_btnBack = (Button) findViewById(R.id.btnBack);

        ShowInfo();
        m_btnSave.setOnClickListener(new ButtonSave());

        m_btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    private void ShowInfo() {
//        apiInfo();
    }

    public class ButtonSave implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String fullname = m_edtFullname.getText().toString();
            String pass = m_edtPass.getText().toString();
            String email = m_edtEmail.getText().toString();

            if(fullname.isEmpty() && pass.isEmpty() && email.isEmpty()){
                Toast.makeText(getApplicationContext(),"Chưa nhập thông tin sửa",Toast.LENGTH_SHORT).show();
                return;
            }
            apiUpdate(fullname, pass, email);
        }
    }

    private void apiUpdate(String fullname, String pass, String email) {
        String token = MainActivity._token;
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Thiếu token. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestBody formBody =  new FormBody.Builder()
                    .add("token", token)
                    .add("fullname", fullname) // nếu server dùng key khác, thay vào đây
                    .add("email", email)
                    .add("password", pass)
                    .build();

        Request request = new Request.Builder()
                .url("https://dev.husc.edu.vn/tin4403/api/userupdate")
                .post(formBody)
                .header("token", token)
                .build();

        OkHttpClient client = new OkHttpClient();

        // Disable nút để tránh gửi nhiều lần (UI thread)
        runOnUiThread(() -> m_btnSave.setEnabled(false));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "apiUpdate onFailure: " + e.getMessage());
                runOnUiThread(() -> {
                    m_btnSave.setEnabled(true);
                    Toast.makeText(UpdateActivity.this, "Lỗi mạng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> m_btnSave.setEnabled(true));
                if (response.isSuccessful() && response.body() != null) {
                    String respBody = response.body().string();
                    Log.d(TAG, "apiUpdate response: " + respBody);
                    try {
                        JSONObject jsonResponse = new JSONObject(respBody);
                        if (jsonResponse.optInt("r") == 1) {
                            final String message = jsonResponse.optString("m", "Cập nhật thành công.");
                            runOnUiThread(() -> {
                                Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show();
                                // Trở về UserActivity và refresh
                                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            final String message = jsonResponse.optString("m", "Cập nhật thất bại.");
                            runOnUiThread(() -> Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "apiUpdate JSON parse error: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(UpdateActivity.this, "Lỗi phân tích dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    String msg = "Lỗi server: " + response.message();
                    Log.e(TAG, msg);
                    runOnUiThread(() -> Toast.makeText(UpdateActivity.this, msg, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

}