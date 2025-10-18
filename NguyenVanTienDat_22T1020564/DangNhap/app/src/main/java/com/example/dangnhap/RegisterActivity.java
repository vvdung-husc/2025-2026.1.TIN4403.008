package com.example.dangnhap;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import okhttp3.*;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    EditText edtNewUser, edtNewPass, edtConfirmPass;
    Button btnCreateAccount;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtNewUser = findViewById(R.id.edtNewUser);
        edtNewPass = findViewById(R.id.edtNewPass);
        edtConfirmPass = findViewById(R.id.edtConfirmPass);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(v -> {
            String user = edtNewUser.getText().toString().trim();
            String pass = edtNewPass.getText().toString().trim();
            String confirm = edtConfirmPass.getText().toString().trim();

            if(user.isEmpty() || pass.isEmpty() || confirm.isEmpty()){
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!pass.equals(confirm)){
                Toast.makeText(this, "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = AppConfig.getBaseUrl(this) + "/api/register";
            RequestBody body = new FormBody.Builder()
                    .add("username", user)
                    .add("password", pass)
                    .add("fullname", user)
                    .add("email", user + "@example.com")
                    .build();

            Request req = new Request.Builder().url(url).post(body).build();

            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        });
    }
}
