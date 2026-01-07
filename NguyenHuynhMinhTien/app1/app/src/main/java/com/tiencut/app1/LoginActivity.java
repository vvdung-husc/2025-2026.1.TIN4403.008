package com.tiencut.app1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword;
    Button btnLogin;
    TextView txtRegister;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra xem người dùng đã đăng nhập chưa
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String authToken = sharedPref.getString("AuthToken", "");

        if (!authToken.isEmpty()) {
            // Nếu có token, chuyển hướng đến HomeActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Đóng MainActivity
            return; // Kết thúc onCreate để không load layout đăng nhập
        }

        setContentView(R.layout.activity_login);
        EdgeToEdge.enable(this);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ tên người dùng và mật khẩu", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(username, password);
                }
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String username, String password) {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("https://dev.husc.edu.vn/tin4403/api/login")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("LoginActivity", "Network failure on login", e);
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();

                    String token = null;
                    String serverMessage = "";

                    // First try parsing JSON to get structured fields
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        // server may return r (status) and m (message)
                        // we capture server message if present
                        // note: optString default should be non-null to satisfy annotations
                        serverMessage = jsonResponse.optString("m", "");

                        // Common token fields
                        token = jsonResponse.optString("token", "");
                        if (token.isEmpty()) token = jsonResponse.optString("accessToken", "");
                        if (token.isEmpty() && jsonResponse.has("data")) {
                            JSONObject data = jsonResponse.optJSONObject("data");
                            if (data != null) token = data.optString("token", "");
                        }

                        android.util.Log.d("LoginActivity", "Login response (JSON parsed): " + responseBody + " Authorization: " + response.header("Authorization"));
                    } catch (JSONException e) {
                        // Not JSON — will try header/fallback heuristics below
                        android.util.Log.w("LoginActivity", "Response is not JSON, will try fallback extraction. Raw response: " + responseBody);
                    }

                    // If still no token, check Authorization header
                    if (token == null || token.isEmpty()) {
                        String authHeader = response.header("Authorization");
                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            token = authHeader.substring(7);
                            android.util.Log.d("LoginActivity", "Token extracted from Authorization header.");
                        }
                    }

                    // Fallback heuristics on response body
                    if (token == null || token.isEmpty()) {
                        // Look for JWT-like start
                        int idx = responseBody.indexOf("eyJ");
                        if (idx >= 0) {
                            String sub = responseBody.substring(idx);
                            String[] parts = sub.split("\\s+|\\r?\\n");
                            token = parts.length > 0 ? parts[0].trim() : "";
                            android.util.Log.d("LoginActivity", "Token extracted by eyJ heuristic: " + token);
                        }
                    }

                    if (token == null || token.isEmpty()) {
                        String[] parts = responseBody.trim().split("\\s+");
                        if (parts.length > 0) {
                            String candidate = parts[parts.length - 1].trim();
                            if (candidate.length() >= 20) {
                                token = candidate;
                                android.util.Log.d("LoginActivity", "Token extracted by last-segment heuristic: " + token);
                            }
                        }
                    }

                    if (token == null || token.isEmpty()) {
                        Pattern p = Pattern.compile("[A-Za-z0-9_\\-.=]{20,}");
                        Matcher m = p.matcher(responseBody);
                        if (m.find()) {
                            token = m.group(0);
                            android.util.Log.d("LoginActivity", "Token extracted by regex fallback: " + token);
                        }
                    }

                    // Final decision: if we have a token, treat as success; otherwise show an error.
                    if (token != null && !token.isEmpty()) {
                        final String finalToken = token;
                        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("AuthToken", finalToken);
                        editor.apply();

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công! Token: " + finalToken, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        final String dbg = !serverMessage.isEmpty() ? serverMessage : responseBody;
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Lỗi: Không nhận được token. Response: " + dbg, Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}