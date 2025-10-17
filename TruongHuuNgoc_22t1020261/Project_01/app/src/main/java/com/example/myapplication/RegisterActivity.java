package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
            String message = "Đã đăng ký tài khoản [" + User + "/" + Name + "]";
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
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