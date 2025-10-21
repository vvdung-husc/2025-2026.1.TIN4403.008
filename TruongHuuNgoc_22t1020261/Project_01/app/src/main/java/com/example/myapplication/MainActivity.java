package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class MainActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static String _userNameLogined;
    static String _token;
    EditText m_edtUser,m_edtPass; //Biến điều khiển EditText**
    Button m_btnLogin,m_btnRegister; //Biến điều khiển Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Khởi tạo các biến điều khiển tương ứng trong layout
        m_edtUser = (EditText)findViewById(R.id.editUser);
        m_edtPass = (EditText)findViewById(R.id.editPass);
        m_btnLogin = (Button) findViewById(R.id.btnLogin);
        m_btnRegister = (Button)findViewById(R.id.btnDky);

        //Cài đặt sự kiện Click cho Button Login
        m_btnLogin.setOnClickListener(new CButtonLogin());

        //Cài đặt sự kiện Click cho Button Register
        m_btnRegister.setOnClickListener(new CButtonRegister());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public class CButtonLogin  implements View.OnClickListener {
        @Override
        public void onClick(View v) {//Hàm sử lý sự kiện click button login
            String user = m_edtUser.getText().toString();// lấy thông tin nhâp tài khoản đã nhập
            String pass = m_edtPass.getText().toString();// lấy thông tin mật khẩu đã nhập
            Log.d("K46","CLICK BUTTON LOGIN ACCOUNT " + user + "/" + pass);
            if (user.length() < 3 || pass.length() < 6){
                Toast.makeText(getApplicationContext(),"Tài khoản hoặc mật khẩu không hợp lệ!",Toast.LENGTH_SHORT).show();
                return;
            }
            //Gọi hàm dịch vụ Login
            try {
                apiLogin(user,pass);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            String msg = "Đã nhập thông tin tài khoản [" + user + "/" + pass + "]";
//            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        }

        private void apiLogin(String user, String pass) throws IOException {
            String json = "{\"username\":\"" + user + "\",\"password\":\"" + pass +"\"}";
            Toast.makeText(getApplicationContext(),json,Toast.LENGTH_SHORT).show();
            Log.d("K46",json);

            RequestBody body = RequestBody.create(json,JSON);

            RequestBody formBody = new FormBody.Builder()
                    .add("username",user)
                    .add("password",pass)
                    .build();

            Request request = new Request.Builder()
                    .url("https://dev.husc.edu.vn/tin4403/api/login")
//                    .post(body)
                    .post(formBody)
                    .build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    String errStr = "Tài khoản hoặc mật khẩu không chính xác.\n" + e.getMessage();
                    Log.d("K46","onFailure\n" + errStr);
                    Toast.makeText(getApplicationContext(),errStr,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if (jsonResponse.optInt("r") == 1) {
                                _token = jsonResponse.optString("m");
                                if (!_token.isEmpty()) {

                                    runOnUiThread(() -> {
                                        Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                        _userNameLogined = user;
                                        Intent intent = new Intent(MainActivity.this, UserActivity.class);
                                        startActivity(intent);
                                        finish(); // Đóng MainActivity để người dùng không thể quay lại màn hình đăng nhập
                                    });
                                } else {
                                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Lỗi: Không nhận được token.", Toast.LENGTH_SHORT).show());
                                }
                            } else {
                                String message = jsonResponse.optString("m", "Đăng nhập thất bại.");
                                runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Lỗi phân tích dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Đăng nhập thất bại: " + response.message(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }
    }

    public class CButtonRegister implements View.OnClickListener {

        @Override
        public void onClick(View v) {//Hàm sử lý sự kiện click button register
            //Toast.makeText(getApplicationContext(),"::onClick...",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(i);
        }
    }
}