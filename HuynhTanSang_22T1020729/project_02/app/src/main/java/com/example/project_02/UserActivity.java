package com.example.project_02;

import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class UserActivity extends AppCompatActivity {

    private TextView labelConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        labelConfirmPassword = findViewById(R.id.labelConfirmPassword);

        // Nhận dữ liệu từ Intent
        String responseStr = getIntent().getStringExtra("userResponse");

        if (responseStr == null || responseStr.isEmpty()) {
            labelConfirmPassword.setText("❌ Không có dữ liệu phản hồi từ server!");
            return;
        }

        try {
            // Bước 1: parse JSON gốc
            JSONObject root = new JSONObject(responseStr);
            int status = root.optInt("r", 0);
            String base64Str = root.optString("m", "");

            if (status == 1 && !base64Str.isEmpty()) {
                // Bước 2: Giải mã Base64 → JSON thực
                byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
                String decodedJson = new String(decodedBytes);

                // Bước 3: Parse JSON bên trong
                JSONObject data = new JSONObject(decodedJson);
                String username = data.optString("u", "unknown");
                long token = data.optLong("t", 0);

                // Bước 4: Hiển thị
                String displayText =
                        "✅ Đăng nhập thành công!\n\n" +
                                "👤 Tên người dùng: " + username + "\n" +
                                "🔑 Mã token: " + token;

                labelConfirmPassword.setText(displayText);
            } else {
                labelConfirmPassword.setText("⚠️ Đăng nhập thất bại!\n" + responseStr);
            }

        } catch (JSONException e) {
            labelConfirmPassword.setText("Lỗi khi đọc dữ liệu: " + e.getMessage());
        }

        // EdgeToEdge hỗ trợ hiển thị full màn
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
