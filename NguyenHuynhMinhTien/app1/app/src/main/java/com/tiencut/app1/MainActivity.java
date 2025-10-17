package com.tiencut.app1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // khai báo các biến
    EditText textNhapA, textNhapB, textKetQua;
    private Button btnTong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // ánh xạ id cho các biến
        textNhapA = findViewById(R.id.textNhapA);
        textNhapB = findViewById(R.id.textNhapB);
        textKetQua = findViewById(R.id.textKetQua);
        btnTong = findViewById(R.id.btnTong);

        // logic tương tác user
        btnTong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = Integer.parseInt(textNhapA.getText().toString());
                int b = Integer.parseInt(textNhapB.getText().toString());
                int tong = a+b;
                textKetQua.setText("" + tong);
            }
        });
    }
}