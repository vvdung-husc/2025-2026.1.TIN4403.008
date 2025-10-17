package com.example.project_01;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Liên kết tới TextView có id là txtWelcome
        TextView txtWelcome = findViewById(R.id.txtWelcome);
        txtWelcome.setText("Xin chào!");
    }
}
