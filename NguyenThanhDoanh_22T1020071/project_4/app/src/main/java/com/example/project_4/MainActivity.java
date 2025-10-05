// Đảm bảo bạn thay thế "com.your.package" bằng tên package thực tế của dự án
package com.example.project_4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 1. Khai báo tất cả các thành phần giao diện (View) mà bạn cần tương tác
    EditText edtUserName, edtPassword;
    Button btnLogin, btnSingIn;
    TextView tvForgotPassword;
    CheckBox rememberPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Tải layout từ file activity_main.xml

        // 2. Ánh xạ các View đã khai báo với ID tương ứng trong file XML
        // Đây là bước kết nối mã Java với các thành phần trong giao diện
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSingIn = findViewById(R.id.btnSingIn);
        rememberPass = findViewById(R.id.rememberPass);

        // Lưu ý: ID của TextView "Quên Mật Khẩu" trong XML của bạn là "edtFgPw"
        // Tôi đã sửa nó trong file XML ở các bước trước thành "tvForgotPassword" để dễ hiểu hơn.
        // Nếu bạn chưa sửa, hãy dùng ID gốc của bạn: findViewById(R.id.edtFgPw)
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // 3. Thiết lập bộ lắng nghe sự kiện nhấn (ClickListener) cho nút Đăng Nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu người dùng nhập từ EditText
                String username = edtUserName.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                // Kiểm tra xem người dùng đã nhập đủ thông tin chưa
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!", Toast.LENGTH_SHORT).show();
                    return; // Dừng hàm tại đây nếu thông tin trống
                }

                // Logic đăng nhập giả lập: kiểm tra tài khoản "admin" và mật khẩu "123"
                // Trong một ứng dụng thực tế, bạn sẽ kiểm tra thông tin này với cơ sở dữ liệu hoặc API.
                if (username.equals("admin") && password.equals("123")) {
                    // Nếu đúng, thông báo đăng nhập thành công
                    Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Tạo một Intent để chuyển từ MainActivity sang UserActivity
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    startActivity(intent);

                    // Gọi finish() để đóng MainActivity. Khi người dùng ở UserActivity
                    // và nhấn nút "Back", ứng dụng sẽ thoát thay vì quay lại màn hình đăng nhập.
                    finish();
                } else {
                    // Nếu sai, thông báo lỗi
                    Toast.makeText(MainActivity.this, "Tài khoản hoặc mật khẩu không chính xác.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 4. Thiết lập ClickListener cho nút Đăng Ký
        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Chuyển đến trang đăng ký...", Toast.LENGTH_SHORT).show();

                // Tạo Intent để chuyển từ MainActivity sang RegisterActivity
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 5. Thiết lập ClickListener cho TextView "Quên Mật Khẩu"
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiện tại chỉ hiển thị một thông báo.
                // Trong tương lai, bạn có thể tạo một Activity mới cho chức năng này.
                Toast.makeText(MainActivity.this, "Chức năng khôi phục mật khẩu đang được phát triển.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
