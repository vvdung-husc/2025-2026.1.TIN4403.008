package com.example.myapplication;

import android.adservices.ondevicepersonalization.RequestToken;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {
    TextView m_txtWelcome;
    Button m_btnLogout, m_btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        m_txtWelcome = (TextView) findViewById(R.id.txtWelcome);
        m_btnLogout = (Button) findViewById(R.id.btnLogout);
        m_btnUpdate = (Button) findViewById(R.id.btnUpdate);

        String s = "Chào mừng tài khoản : " + MainActivity._userNameLogined;
        m_txtWelcome.setText(s);

        HienThi();

        m_btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        m_btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
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

    public void HienThi(){
        String token = MainActivity._token;
        RequestBody formBody = new FormBody.Builder()
                .add("token",token)
                .build();

        Request request = new Request.Builder()
                .url("https://dev.husc.edu.vn/tin4403/api/userinfo")
                .post(formBody)
                .header("token",token)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(),"Token -> JSON không hợp lệ.\n" + e.getMessage(),Toast.LENGTH_SHORT).show();
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if(!response.isSuccessful()){
//                    UserActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Toast.makeText(getApplicationContext(),"Token -> JSON không hợp lệ.\n" + response.body().string(),Toast.LENGTH_SHORT).show();
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//                    });
//                    return;
//                }
//                try {
//                    JSONObject jsonObject = new JSONObject(response.body().string());
//                    if(jsonObject.optInt("r") == 1){
//                        Toast.makeText(getApplicationContext(), jsonObject.optJSONArray("m").toString(), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getApplicationContext(),UserActivity.class);
//                        startActivity(intent);
//                    }
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        if (jsonResponse.optInt("r") == 1) {
                            String Info = jsonResponse.optString("m");

                            runOnUiThread(() -> {
                                m_txtWelcome.setText(Info);
                                Toast.makeText(UserActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            String message = jsonResponse.optString("m", "Đăng nhập thất bại.");
                            runOnUiThread(() -> Toast.makeText(UserActivity.this, message, Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(UserActivity.this, "Lỗi phân tích dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(UserActivity.this, "Đăng nhập thất bại: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }

        });
        return;
    }
}