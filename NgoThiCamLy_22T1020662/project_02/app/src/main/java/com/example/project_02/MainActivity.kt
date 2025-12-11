package com.example.project_02

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var m_id_User: EditText
    private lateinit var m_id_Pass: EditText
    private lateinit var m_id_Login: Button
    private lateinit var m_id_Register: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ánh xạ các View
        m_id_User = findViewById(R.id.id_Username)
        m_id_Pass = findViewById(R.id.id_Password)
        m_id_Login = findViewById(R.id.id_Login)
        m_id_Register = findViewById(R.id.id_Register)

        // Sự kiện nút Đăng nhập
        m_id_Login.setOnClickListener {
            val user = m_id_User.text.toString().trim()
            val pass = m_id_Pass.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (user.length < 3 || pass.length < 6) {
                Toast.makeText(this, "Tên hoặc mật khẩu không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            apiLogin(user, pass)
        }

        // Sự kiện nút Đăng ký mới
        m_id_Register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Xử lý thanh điều hướng
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Hàm gọi API đăng nhập
    private fun apiLogin(user: String, pass: String) {
        val client = OkHttpClient()

        val json = """
            {
                "username": "$user",
                "password": "$pass"
            }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://dev.husc.edu.vn/tin4403/api/user/login")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Không thể kết nối máy chủ!", Toast.LENGTH_SHORT).show()
                    Log.e("LOGIN", "Lỗi kết nối: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("LOGIN", "Server trả về: $responseData")

                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                        // ✅ Chuyển sang UserActivity và truyền username
                        val intent = Intent(applicationContext, UserActivity::class.java)
                        intent.putExtra("username", user)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
