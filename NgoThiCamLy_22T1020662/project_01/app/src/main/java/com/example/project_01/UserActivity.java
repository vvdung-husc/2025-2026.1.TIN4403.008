package com.example.project_01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    TextView m_txtWelcome;
    Button m_btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

//Khởi tạo các biến tương ứng trong layout
        m_txtWelcome = (TextView)findViewById(R.id.txtWelcome);
        m_btnLogout = (Button) findViewById(R.id.btnLogout);

        //String s = "Chào mừng tài khoản : " + MainActivity._userNameLogined;
        //m_txtWelcome.setText(s);
        m_btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // chạy trên thread khác với UIThread để tránh bị treo ứng dụng
        new Thread(() -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("token", MainActivity._token);

            ApiClient.ApiResult r = ApiClient.httpPost("https://dev.husc.edu.vn/tin4403/api/userinfo",null, headers);

            runOnUiThread(() -> {
                if (r.success) {
                    Log.d("API", "OK: " + r.body);
                    try {
                        Log.d("API", "OK: " + r.body);

                        JSONObject obj = new JSONObject(r.body);

                        int ret = obj.getInt("r");
                        JSONObject m = obj.getJSONObject("m");

                        //String username = m.getString("username");
                        //String password = m.getString("password");
                        String fullname = m.getString("fullname");

                        String s = "Chào mừng tài khoản : " + fullname;
                        m_txtWelcome.setText(s);

                    } catch (JSONException e) {
                        Toast.makeText(this, "Lỗi ParseJSON ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "ERR: " + r.body, Toast.LENGTH_SHORT).show();
                    //Log.d("API", "ERR: " + r.body);
                }
            });

        }).start();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}