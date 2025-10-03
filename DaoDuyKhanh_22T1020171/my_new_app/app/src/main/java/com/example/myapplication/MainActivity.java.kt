package com.example.project_01

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class `MainActivity.java` : AppCompatActivity() {
    var m_edtUser: EditText? = null
    var m_edtPass: EditText? = null //Biến điều khiển EditText**
    var m_btnLogin: Button? = null
    var m_btnRegister: Button? = null //Biến điều khiển Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //Khởi tạo các biến điều khiển tương ứng trong layout
        m_edtUser = findViewById<View>(R.id.edtUsername) as EditText
        m_edtPass = findViewById<View>(R.id.edtPassword) as EditText
        m_btnLogin = findViewById<View>(R.id.btnLogin) as Button
        m_btnRegister = findViewById<View>(R.id.btnRegister) as Button

        //Cài đặt sự kiện Click cho Button Login
        m_btnLogin!!.setOnClickListener(CButtonLogin())

        //Cài đặt sự kiện Click cho Button Register
        m_btnRegister!!.setOnClickListener(CButtonRegister())

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View>(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    inner class CButtonLogin : View.OnClickListener {
        override fun onClick(v: View) { //Hàm sử lý sự kiện click button login
            val user = m_edtUser!!.text.toString() // lấy thông tin nhâp tài khoản đã nhập
            val pass = m_edtPass!!.text.toString() // lấy thông tin mật khẩu đã nhập
            Log.d("K46", "CLICK BUTTON LOGIN ACCOUNT $user/$pass")
            if (user.length < 3 || pass.length < 6) {
                Toast.makeText(
                    applicationContext,
                    "Tài khoản hoặc mật khẩu không hợp lệ!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            //Gọi hàm dịch vụ Login
            //apiLogin(user,pass);
            val msg = "Đã nhập thông tin tài khoản [$user/$pass]"
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        }
    }

    inner class CButtonRegister : View.OnClickListener {
        override fun onClick(v: View) { //Hàm sử lý sự kiện click button register
            //Toast.makeText(getApplicationContext(),"::onClick...",Toast.LENGTH_SHORT).show();
            val i = Intent(
                applicationContext,
                RegisterActivity::class.java
            )
            startActivity(i)
        }
    }
}