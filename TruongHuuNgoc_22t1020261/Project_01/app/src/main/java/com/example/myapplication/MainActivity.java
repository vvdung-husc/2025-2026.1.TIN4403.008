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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static String _userNameLogined;
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
            Request request = new Request.Builder()
                    .url("https://dev.husc.edu.vn/tin4403/api/login")
                    .post(body)
                    .build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    String errStr = "Tài khoản hoặc mật khẩu không chính xác.\n" + e.getMessage();
                    Log.d("K46","onFailure\n" + errStr);
                    Toast.makeText(getApplicationContext(),errStr,Toast.LENGTH_SHORT).show();
                    call.cancel();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String errStr = "Tài khoản hoặc mật khẩu không chính xác.\n" + response.body().string();
                    Log.d("K46",errStr);
                    if (!response.isSuccessful()){
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),errStr,Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    _userNameLogined = user;
                    Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                    startActivity(intent);
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