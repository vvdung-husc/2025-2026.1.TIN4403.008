package com.tiencut.app1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChuyenDoiNhietDoActivity extends AppCompatActivity {
    private EditText editTextFarenheit, editTextCelsius;
    private Button buttonFC, buttonCF;
    private Button buttonClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.linear_layout_chuyen_doi_nhiet_do);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ánh xạ id cho các biến
        editTextFarenheit = findViewById(R.id.editTextFarenheit);
        editTextCelsius = findViewById(R.id.editTextCelsius);
        buttonFC = findViewById(R.id.buttonFC);
        buttonCF = findViewById(R.id.buttonCF);
        buttonClear = findViewById(R.id.buttonClear);

        // logic tương tác user
        buttonFC.setOnClickListener(view -> {
            String fStr = editTextFarenheit.getText().toString();
            if (!fStr.isEmpty()) {
                double f = Double.parseDouble(fStr);
                double c = (f - 32) * 5 / 9;
                editTextCelsius.setText(String.format("%.2f", c));
            }
        });

        buttonCF.setOnClickListener(view -> {
            String cStr = editTextCelsius.getText().toString();
            if (!cStr.isEmpty()) {
                double c = Double.parseDouble(cStr);
                double f = c * 9 / 5 + 32;
                editTextFarenheit.setText(String.format("%.2f", f));
            }
        });

        buttonClear.setOnClickListener(view -> {
            editTextFarenheit.setText("");
            editTextCelsius.setText("");
        });
    }
}