package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUser, edtName, edtPass1, edtPass2;
    TextView txtBack;
    Button edtCreate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        edtUser = (EditText) findViewById(R.id.edtUser);
        edtName = (EditText) findViewById(R.id.edtName);
        edtPass1 = (EditText) findViewById(R.id.edtPass1);
        edtPass2 = (EditText) findViewById(R.id.edtPass2);
        edtCreate = (Button) findViewById(R.id.btnCreateUser);
        txtBack = (TextView) findViewById(R.id.txtBack);

        edtCreate.setOnClickListener(new CButtonCreate());
        txtBack.setOnClickListener(new CButtonBack());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public class CButtonCreate implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String User = edtUser.getText().toString();
            String Name = edtName.getText().toString();
            String Pass1 = edtPass1.getText().toString();
            String Pass2 = edtPass2.getText().toString();

            if(User.length() < 3 || Pass1.length() < 3){
                Toast.makeText(getApplicationContext(),"Tài khoản hoặc mật khẩu không hợp lệ!",Toast.LENGTH_SHORT).show();
                return;
            }
            if(Name.length() < 5){
                Toast.makeText(getApplicationContext(),"Tên chưa hợp lệ!",Toast.LENGTH_SHORT).show();
                return;
            }
            if(!Pass1.equals(Pass2)){
                Toast.makeText(getApplicationContext(),"Mật khẩu chưa khớp!",Toast.LENGTH_SHORT).show();
                return;
            }
//            String message = "Đã đăng ký tài khoản [" + User + "/" + Name + "]";
//            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            registerUser(User,Name,Pass1);
        }

        private void registerUser(String username, String fullname, String password) {
            RequestBody formBody = new FormBody.Builder()
                    .add("username",username)
                    .add("fullname",fullname)
                    .add("password",password)
                    .build();
            Request request = new Request.Builder()
                    .url("https://dev.husc.edu.vn/tin4403/api/register")
                    .post(formBody)
                    .build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
//                        String responseBody = response.body().string();
                        // Xử lý phản hồi thành công từ server
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        String errorBody = response.body().string();
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + errorBody, Toast.LENGTH_LONG).show());
                    }
                }
            });
        }
    }

    public class CButtonBack implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }
}