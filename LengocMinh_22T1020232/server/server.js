const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');

// Import các tệp từ thư mục _global của bạn
const db = require('./_global/ltdd_db'); 
const utils = require('./_global/utils');

const app = express();
const SECRET_KEY = "K46_MOBILE_PROJECT"; // Khóa bí mật để tạo Token

app.use(cors());
app.use(express.json());

// Khởi tạo kết nối MongoDB thông qua ltdd_db.js
db.Init().then(success => {
    if (!success) {
        console.error("❌ Không thể khởi động Database. Dừng server.");
        process.exit(1);
    }
});

// Middleware xác thực Token (Dùng cho userinfo và userupdate)
const authenticateToken = (req, res, next) => {
    const token = req.headers['token']; // Khớp với UserActivity.java
    if (!token) return utils.apiResult(0, "Thiếu Token xác thực", res);

    jwt.verify(token, SECRET_KEY, (err, user) => {
        if (err) return utils.apiResult(0, "Token hết hạn hoặc không hợp lệ", res);
        req.user = user;
        next();
    });
};

// 1. API Đăng ký (Register)
app.post('/register', async (req, res) => {
    const { username, password, fullname, email } = req.body;
    try {
        const result = await db.modifyUser(username, { 
            password, 
            fullname: fullname || "", 
            email: email || "" 
        });
        utils.apiResult(1, "Đăng ký thành công", res);
    } catch (e) {
        utils.apiResult(0, "Lỗi đăng ký: " + e.message, res);
    }
});

// 2. API Đăng nhập (Login)
app.post('/login', async (req, res) => {
    const { username, password } = req.body;
    // Sử dụng hàm Authentication có sẵn trong ltdd_db.js
    const user = await db.Authentication(username, password);
    
    if (user) {
        // Tạo token chứa username
        const token = jwt.sign({ username: user.username }, SECRET_KEY, { expiresIn: '24h' });
        // Trả về token trong biến 'm' để MainActivity.java đọc được
        utils.apiResult(1, token, res);
    } else {
        utils.apiResult(0, "Sai tài khoản hoặc mật khẩu", res);
    }
});

// 3. API Lấy thông tin người dùng (User Info)
app.post('/userinfo', authenticateToken, async (req, res) => {
    const user = await db.getUser(req.user.username);
    if (user) {
        // Trả về đối tượng user trong biến 'm' để UserActivity.java hiển thị
        utils.apiResult(1, user, res);
    } else {
        utils.apiResult(0, "Không tìm thấy người dùng", res);
    }
});

// 4. API Cập nhật thông tin (User Update)
app.post('/userupdate', authenticateToken, async (req, res) => {
    const updateData = req.body;
    try {
        await db.modifyUser(req.user.username, updateData);
        utils.apiResult(1, "Cập nhật thành công", res);
    } catch (e) {
        utils.apiResult(0, "Lỗi cập nhật", res);
    }
});

app.listen(4380, () => {
    console.log("🚀 Server đang lắng nghe tại http://localhost:4380");
});