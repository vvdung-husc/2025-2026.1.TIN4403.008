package com.example.project_01;

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

import com.example.project_01.ApiClient;
import com.example.project_01.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    static String _token; //token nhận được khi login thành công
    EditText m_edtUser, m_edtPass; //Biến điều khiển EditText**
    Button m_btnLogin, m_btnRegister; //Biến điều khiển Button

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        applyPrefillFromIntent(intent);
    }

    private void applyPrefillFromIntent(Intent intent) {
        if (intent == null) return;

        if (intent.hasExtra("prefill_user")) {
            String u = intent.getStringExtra("prefill_user");
            if (u != null) m_edtUser.setText(u);
        }

        if (intent.hasExtra("prefill_pass")) {
            String p = intent.getStringExtra("prefill_pass");
            if (p != null) m_edtPass.setText(p);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Khởi tạo các biến điều khiển tương ứng trong layout
        m_edtUser = (EditText) findViewById(R.id.edtUsername);
        m_edtPass = (EditText) findViewById(R.id.edtPassword);
        m_btnLogin = (Button) findViewById(R.id.btnLogin);
        m_btnRegister = (Button) findViewById(R.id.btnRegister);
        applyPrefillFromIntent(getIntent());

        //m_edtUser.setText("yntn_k46");
        //m_edtPass.setText("020534");

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
                //apiLogin(user,pass);
                okhttpLogin(user,pass);
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

    //Hàm dịch vụ Login
    void okhttpLogin(String user, String pass) throws IOException {
        //boolean bOk = (user.equals("vvdung") && pass.equals("123456"));
        String json = "{\"username\":\"" + user + "\",\"password\":\"" + pass +"\"}";
        Toast.makeText(getApplicationContext(),json,Toast.LENGTH_SHORT).show();
        Log.d("K46",json);

        // chạy trên thread khác với UIThread để tránh bị treo ứng dụng
        new Thread(() -> {
            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_LOGIN, json,null);

            runOnUiThread(() -> {
                try {
                    JSONObject obj = new JSONObject(r.body);

                    int ret = obj.getInt("r");          // r là mã lỗi trả về từ API
                    String msg = obj.getString("m");    // m là thông báo trả về từ API

                    if (r.success) {
                        Log.w("API", "OK: " + r.httpCode + " " + r.body);
                        Toast.makeText(this, "OK: " + r.httpCode + " " + r.body, Toast.LENGTH_SHORT).show();


                        _token = msg; // là một chuỗi Base64
                        Intent i = new Intent(getApplicationContext(), UserActivity.class);
                        startActivity(i);
                    }
                    else {
                        Log.e("API", "ERR: " + r.httpCode + " " + r.body);
                        Toast.makeText(this, "ERR: " + r.body, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("API", "FAILED: " + r.httpCode + " " + r.body);

                    //Một số thông báo lỗi khi không kết nối được với dịch vụ API
                    if (r.httpCode == 404)
                        Utils.showAlert(MainActivity.this,"Lỗi dịch vụ","API không tìm thấy - " + ApiClient.URL_LOGIN);
                    else if (r.httpCode == 502)  // Bad Gateway - Dịch vụ không chạy
                        Utils.showAlert(MainActivity.this,"Lỗi dịch vụ","Dịch vụ API đang không hoạt động");
                    else if (r.body.contains("Failed to connect"))
                        Utils.showAlert(MainActivity.this,"Lỗi dịch vụ",r.body);
                    else Toast.makeText(this, "Lỗi ParseJSON " + r.body, Toast.LENGTH_SHORT).show();
                }

            });
        }).start();

    }

    /*void apiLogin(String user, String pass) throws IOException {
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

                Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                startActivity(intent);

            }
        });//client.newCall(request).enqueue(new Callback() {
    }*/

}