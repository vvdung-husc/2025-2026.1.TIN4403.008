package com.tiencut.app1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    Button btnLogout;
    TextView tvUsername, tvFullname, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvUsername = findViewById(R.id.tvUsername);
        tvFullname = findViewById(R.id.tvFullname);
        tvEmail = findViewById(R.id.tvEmail);

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa token khỏi SharedPreferences
                SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("AuthToken");
                editor.apply();

                // Chuyển hướng về MainActivity
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish(); // Đóng HomeActivity
            }
        });

        // Thêm nút lấy thông tin và cập nhật
        Button btnGetUser = findViewById(R.id.btnGetUser);
        Button btnUpdateUser = findViewById(R.id.btnUpdateUser);

        btnGetUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiClient.getUserInfo(HomeActivity.this, new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> android.widget.Toast.makeText(HomeActivity.this, "Lỗi kết nối: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show());
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                        final int code = response.code();
                        final String body = response.body() != null ? response.body().string() : "(empty)";

                        android.util.Log.d("HomeActivity", "getUserInfo response code=" + code + " body=" + body + " headers=" + response.headers().toString());

                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> android.widget.Toast.makeText(HomeActivity.this, "Lỗi server: " + code + " - " + response.message(), android.widget.Toast.LENGTH_LONG).show());
                            return;
                        }

                        try {
                            JSONObject json = new JSONObject(body);
                            if (json.optInt("r", 0) == 1) {
                                JSONObject data = json.optJSONObject("data");
                                final String username = data != null ? data.optString("username", "") : json.optString("username", "");
                                final String fullname = data != null ? data.optString("fullname", data.optString("HoTen", "")) : json.optString("fullname", json.optString("HoTen", ""));
                                final String email = data != null ? data.optString("email", "") : json.optString("email", "");

                                runOnUiThread(() -> {
                                    tvUsername.setText("Username: " + username);
                                    tvFullname.setText("Họ tên: " + fullname);
                                    tvEmail.setText("Email: " + email);
                                    android.widget.Toast.makeText(HomeActivity.this, "Lấy thông tin thành công", android.widget.Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                final String msg = json.optString("m", "Lấy thông tin thất bại");
                                runOnUiThread(() -> android.widget.Toast.makeText(HomeActivity.this, msg + " - resp: " + body, android.widget.Toast.LENGTH_LONG).show());
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            runOnUiThread(() -> android.widget.Toast.makeText(HomeActivity.this, "Lỗi phân tích dữ liệu: " + ex.getMessage() + " - resp: " + body, android.widget.Toast.LENGTH_LONG).show());
                        }
                    }
                });
            }
        });

        btnUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show dialog to enter new values
                android.view.LayoutInflater inflater = getLayoutInflater();
                android.view.View dialogView = inflater.inflate(R.layout.dialog_update_user, null);
                android.widget.EditText edtFullname = dialogView.findViewById(R.id.edtUpdateFullname);
                android.widget.EditText edtPassword = dialogView.findViewById(R.id.edtUpdatePassword);
                android.widget.EditText edtEmail = dialogView.findViewById(R.id.edtUpdateEmail);

                new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Cập nhật thông tin")
                        .setView(dialogView)
                        .setPositiveButton("Gửi", (dialog, which) -> {
                            String fullname = edtFullname.getText().toString();
                            String password = edtPassword.getText().toString();
                            String email = edtEmail.getText().toString();

                            ApiClient.updateUser(HomeActivity.this, password, fullname, email, new okhttp3.Callback() {
                                @Override
                                public void onFailure(okhttp3.Call call, IOException e) {
                                    e.printStackTrace();
                                    runOnUiThread(() -> android.widget.Toast.makeText(HomeActivity.this, "Lỗi kết nối: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show());
                                }

                                @Override
                                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                    final String body = response.body() != null ? response.body().string() : "(empty)";
                                    runOnUiThread(() -> android.widget.Toast.makeText(HomeActivity.this, "Update result: " + body, android.widget.Toast.LENGTH_LONG).show());
                                }
                            });

                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        // Danh sách sinh viên từ backend local
        Button btnGetStudents = findViewById(R.id.btnGetStudents);
        btnGetStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiClient.getStudentsLocal(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> android.widget.Toast.makeText(HomeActivity.this, "Lỗi kết nối (local backend): " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show());
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                        final int code = response.code();
                        final String body = response.body() != null ? response.body().string() : "(empty)";
                        android.util.Log.d("HomeActivity", "getStudentsLocal code=" + code + " body=" + body);

                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> android.widget.Toast.makeText(HomeActivity.this, "Lỗi server local: " + code, android.widget.Toast.LENGTH_LONG).show());
                            return;
                        }

                        try {
                            org.json.JSONArray arr = new org.json.JSONArray(body);
                            final StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < arr.length(); i++) {
                                org.json.JSONObject obj = arr.getJSONObject(i);
                                sb.append(obj.optString("MSV", "-"))
                                        .append(" | ")
                                        .append(obj.optString("HoTen", "-"))
                                        .append("\n");
                            }

                            runOnUiThread(() -> new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this)
                                    .setTitle("Danh sách SV")
                                    .setMessage(sb.length() > 0 ? sb.toString() : "Không có dữ liệu")
                                    .setPositiveButton("Đóng", null)
                                    .show());

                        } catch (org.json.JSONException ex) {
                            ex.printStackTrace();
                            runOnUiThread(() -> android.widget.Toast.makeText(HomeActivity.this, "Lỗi phân tích dữ liệu local: " + ex.getMessage(), android.widget.Toast.LENGTH_LONG).show());
                        }
                    }
                });
            }
        });
    }
}