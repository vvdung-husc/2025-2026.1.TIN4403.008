package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    EditText m_edtFullname, m_edtPass, m_edtEmail;
    Button m_btnSave, m_btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        m_edtFullname = (EditText) findViewById(R.id.edtFullname);
        m_edtPass = (EditText) findViewById(R.id.edtPass);
        m_edtEmail = (EditText) findViewById(R.id.edtEmail);
        m_btnSave = (Button) findViewById(R.id.btnSave);
        m_btnBack = (Button) findViewById(R.id.btnBack);

        ShowInfo();
        m_btnSave.setOnClickListener(new ButtonSave());

        m_btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    private void ShowInfo() {
//        apiInfo();
    }

    public class ButtonSave implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String fullname = m_edtFullname.getText().toString();
            String pass = m_edtPass.getText().toString();
            String email = m_edtEmail.getText().toString();

            if(fullname.length() < 5 || pass.length() < 5 || email.length() < 5){
                Toast.makeText(getApplicationContext(),"Định dạng không phù hợp",Toast.LENGTH_SHORT).show();
                return;
            }
            apiUpdate(fullname, pass, email);
        }
    }

    private void apiUpdate(String fullname, String pass, String email) {

    }

}