package com.example.project_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    TextView m_txtFullname, m_txtEmail;
    EditText m_edtNewEmail, m_edtPassword1, m_edtPassword2; //Biến điều khiển EditText**
    Button m_btnLogout, m_btnUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

//Khởi tạo các biến điều khiển tương ứng trong layout
        m_txtFullname = (TextView)findViewById(R.id.txtFullname);
        m_txtEmail= (TextView)findViewById(R.id.txtEmail);
        m_btnLogout = (Button) findViewById(R.id.btnLogout);

        m_edtNewEmail = (EditText) findViewById(R.id.edtNewEmail);
        m_edtPassword1 = (EditText) findViewById(R.id.edtPassword1);
        m_edtPassword2 = (EditText) findViewById(R.id.edtPassword2);
        m_btnUpdate = (Button) findViewById(R.id.btnUpdate);

        //cập nhật thông tin tài khoản đã đăng nhập
        getUserInfo();

        m_btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        m_btnUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //kiểm tra thông tin email mới;
                String email = m_edtNewEmail.getText().toString();// lấy thông tin EMAIL đã nhập
                String pass = m_edtPassword1.getText().toString();// lấy thông tin mật khẩu đã nhập
                String pass2 = m_edtPassword2.getText().toString();// lấy thông tin mật khẩu đã nhập

                if (email.isEmpty() && pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email hoặc Password phải có để cập nhật", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject obj = new JSONObject();

                if (!email.isEmpty()){
                    if (email.indexOf('@') == -1) {
                        Toast.makeText(getApplicationContext(), "Địa chỉ email không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        obj.put("email",email);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }

                if (!pass.isEmpty()){
                    if ( pass.length() < 6 ||   pass2.isEmpty() || !pass.equals(pass2)) {
                        Toast.makeText(getApplicationContext(), "Mật khẩu thay đổi không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        obj.put("password", pass);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }

                String json = obj.toString();

                Log.d("K46", "CLICK BUTTON UPDATE " + json);

                updateUserInfo(json);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void getUserInfo(){
        // chạy trên thread khác với UIThread để tránh bị treo ứng dụng
        new Thread(() -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("token", MainActivity._token);

            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_INFO,null, headers);

            runOnUiThread(() -> {
                if (r.success) {
                    Log.w("API", "OK: " + r.httpCode + " " + r.body);

                    try {

                        JSONObject obj = new JSONObject(r.body);

                        int ret = obj.getInt("r");
                        JSONObject m = obj.getJSONObject("m");

                        //String username = m.getString("username");
                        //String password = m.getString("password");
                        String fullname = m.has("fullname") ? m.getString("fullname") : "<Chưa có fullname>";
                        String szEmail = m.has("email") ? m.getString("email") : "<Chưa có email>";

                        m_txtFullname.setText("Chào mừng tài khoản : " + fullname);
                        m_txtEmail.setText( "Địa chỉ thư diện tử : " + szEmail);

                    } catch (JSONException e) {
                        Toast.makeText(this, "Lỗi ParseJSON ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API", "ERR: " + r.httpCode + " " + r.body);
                    Toast.makeText(this, "ERR: " + r.body, Toast.LENGTH_SHORT).show();
                }
            });

        }).start();
    }

    void updateUserInfo(String json) {
        // chạy trên thread khác với UIThread để tránh bị treo ứng dụng
        new Thread(() -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("token", MainActivity._token);

            ApiClient.ApiResult r = ApiClient.httpPost(ApiClient.URL_USER_UPDATE, json, headers);

            runOnUiThread(() -> {
                try {
                    JSONObject obj = new JSONObject(r.body);

                    int ret = obj.getInt("r");          // r là mã lỗi trả về từ API
                    String msg = obj.getString("m");    // m là thông báo trả về từ API

                    if (r.success) {
                        Log.w("API", "OK: " + r.httpCode + " " + r.body);

                        //Hiển thị AlertDialog
                        Utils.showAlert(UserActivity.this,"Thành công",msg);

                        // Cập nhật lại giao diện người dùng với thông tin mới
                        getUserInfo(); // Gọi lại hàm này để làm mới tên và email trên màn hình

                        //Xóa nội dung trong các ô EditText sau khi cập nhật thành công
                        m_edtNewEmail.setText("");
                        m_edtPassword1.setText("");
                        m_edtPassword2.setText("");
                    } else {
                        Log.e("API", "ERR: " + r.httpCode + " " + r.body);
                        Toast.makeText(this, "ERR: " + r.body, Toast.LENGTH_SHORT).show();

                        //có lỗi API => quay lại form chính
                        if (ret == -3) { // Trường hợp token hết hạn
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                } catch (JSONException e) {
                    Log.e("API", "FAILED: " + r.httpCode + " " + r.body);
                    if (r.httpCode == 404)
                        Utils.showAlert(UserActivity.this,"Lỗi dịch vụ","API không tìm thấy - " + ApiClient.URL_USER_UPDATE);
                    else if (r.httpCode == 502)  // Bad Gateway - Dịch vụ không chạy
                        Utils.showAlert(UserActivity.this,"Lỗi dịch vụ","Dịch vụ API đang không hoạt động");
                    else
                        Toast.makeText(this, "Lỗi ParseJSON ", Toast.LENGTH_SHORT).show();
                }
            });

        }).start();
    }
}