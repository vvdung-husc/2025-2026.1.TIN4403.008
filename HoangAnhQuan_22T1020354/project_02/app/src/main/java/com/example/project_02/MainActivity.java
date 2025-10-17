package com.example.project_02;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Callback;

public class MainActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static String   _userNameLogined;
    EditText m_edtUser, m_edtPass; //Biến điều khiển EditText**
    Button m_btnLogin, m_btnRegister; //Biến điều khiển Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Khởi tạo các biến điều khiển tương ứng trong layout
        m_edtUser = (EditText) findViewById(R.id.edtUsername);
        m_edtPass = (EditText) findViewById(R.id.edtPassword);
        m_btnLogin = (Button) findViewById(R.id.btnLogin);
        m_btnRegister = (Button) findViewById(R.id.btnRegister);

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

    public class CButtonLogin implements View.OnClickListener {
        @Override
        public void onClick(View v) {//Hàm sử lý sự kiện click button login
            String user = m_edtUser.getText().toString();// lấy thông tin nhâp tài khoản đã nhập
            String pass = m_edtPass.getText().toString();// lấy thông tin mật khẩu đã nhập
            Log.d("K46", "CLICK BUTTON LOGIN ACCOUNT " + user + "/" + pass);
            if (user.length() < 3 || pass.length() < 6) {
                Toast.makeText(getApplicationContext(), "Tài khoản hoặc mật khẩu không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
            //Gọi hàm dịch vụ Login
            try {
                apiLogin(user,pass);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //String msg = "Đã nhập thông tin tài khoản [" + user + "/" + pass + "]";
            //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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

    //Hàm mẫu sử dụng phương thức GET
    void doGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //txtString.setText(myResponse);
                        Log.d("K46",myResponse);
                    }
                });
            }
        });
    }

    //Hàm mẫu sử dụng phương thức POST
    void doPost(String url,String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json,JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("K43",response.body().string());
            }
        });
    }
    //Hàm dịch vụ Login
    void apiLogin(String user, String pass) throws IOException {
        //boolean bOk = (user.equals("vvdung") && pass.equals("123456"));
        String json = "{\"username\":\"" + user + "\",\"password\":\"" + pass +"\"}";
        Toast.makeText(getApplicationContext(),json,Toast.LENGTH_SHORT).show();
        Log.d("K46",json);

        RequestBody body = RequestBody.create(json,JSON);
        Request request = new Request.Builder()
                .url("https://dev.husc.edu.vn/tin4403/api/login") //.url("http://192.168.56.1:4380/login")
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String errStr = "Tài khoản hoặc mật khẩu không chính xác.\n" + e.getMessage();
                Log.d("K46","onFailure\n" + errStr);
                Toast.makeText(getApplicationContext(),errStr,Toast.LENGTH_SHORT).show();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resStr = response.body().string();
                Log.d("LOGIN_RESPONSE", resStr);

                if (!response.isSuccessful()) {
                    MainActivity.this.runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(),
                                    "Đăng nhập thất bại: " + resStr,
                                    Toast.LENGTH_LONG).show());
                    return;
                }

                try {
                    org.json.JSONObject json = new org.json.JSONObject(resStr);
                    int r = json.optInt("r", 0);
                    String token = json.optString("m", "");

                    if (r != 1 || token.isEmpty()) {
                        MainActivity.this.runOnUiThread(() ->
                                Toast.makeText(getApplicationContext(),
                                        "Không tìm thấy token đăng nhập!",
                                        Toast.LENGTH_SHORT).show());
                        return;
                    }

                    // ✅ Lưu token và chuyển sang UserActivity
                    Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                    intent.putExtra("token", token);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.this.runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(),
                                    "Lỗi xử lý phản hồi đăng nhập!",
                                    Toast.LENGTH_SHORT).show());
                }
            }


        });
    }

}