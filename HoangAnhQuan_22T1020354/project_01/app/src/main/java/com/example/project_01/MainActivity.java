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

public class MainActivity extends AppCompatActivity {
    EditText m_edtUser, m_edtPass; //Biến điều khiển EditText**
    Button m_btnLogin, m_btnRegister; //Biến điều khiển Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Khởi tạo các biến điều khiển tương ứng trong layout
        m_edtUser = (EditText) findViewById(R.id.editTextText);
        m_edtPass = (EditText) findViewById(R.id.editTextTextPassword3);
        m_btnLogin = (Button) findViewById(R.id.button);
        m_btnRegister = (Button) findViewById(R.id.button3);

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
        public void onClick(View v) {
            String user = m_edtUser.getText().toString();
            String pass = m_edtPass.getText().toString();
            Log.d("K46", "CLICK BUTTON LOGIN ACCOUNT " + user + "/" + pass);

            if (user.length() < 3 || pass.length() < 6) {
                Toast.makeText(getApplicationContext(), "Tài khoản hoặc mật khẩu không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Giả sử điều kiện login thành công
            String msg = "Đăng nhập thành công [" + user + "]";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

            // 👉 Mở UserActivity
            Intent i = new Intent(MainActivity.this, UserActivity.class);
            i.putExtra("username", user); // gửi kèm tên đăng nhập
            startActivity(i);

            // Nếu không muốn quay lại màn hình login thì đóng luôn:
            // finish();
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