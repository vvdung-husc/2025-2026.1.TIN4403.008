# Dự án: Tiencut

Hướng dẫn ngắn để chạy dự án trong **VS Code** (Backend Node, import dữ liệu MongoDB, ứng dụng Android).

---

## Yêu cầu trước khi chạy ✅
- Node.js (v16+), npm
- MongoDB (local `mongod` hoặc MongoDB Atlas)
- Java JDK 11+
- Android SDK (cần **platform-tools** để có `adb`) và `emulator` nếu dùng AVD
- Git & VS Code

> Gợi ý: Dùng Android Studio để quản lý SDK/AVD dễ dàng; nếu không, cài `cmdline-tools` + `platform-tools` rồi đặt `ANDROID_SDK_ROOT`.

---

## Backend (Node) — chạy trong terminal của VS Code 🔧
1. Mở workspace bằng VS Code và mở Integrated Terminal.
2. Cài thư viện và khởi động server:

```bash
cd backend
npm install
npm start   # chạy server.js trên port 3000
```

3. Kiểm tra nhanh:
```bash
curl http://localhost:3000/
curl http://localhost:3000/api/students
```

---

## Import CSV → MongoDB 📥
1. Đảm bảo MongoDB đang chạy hoặc đặt biến môi trường `MONGODB_URI`/`DB_NAME`.
2. Trong terminal VS Code:
```bash
cd mongodb_setup
npm install
npm run import   # import DSSV_TIN4403.008.csv vào MongoDB
```

> Script sẽ dùng `MONGODB_URI` và `DB_NAME` nếu bạn thiết lập; xem thêm `.env.example` trong repo.

---

## Ứng dụng Android (`app1`) — build & cài từ terminal VS Code 📱
1. Đảm bảo `adb` có thể truy cập (trên `PATH`) hoặc gọi bằng đường dẫn đầy đủ (ví dụ `D:\...\platform-tools\adb.exe`).
2. Build và cài:

```bash
cd app1
# tải dependency và build
./gradlew --refresh-dependencies assembleDebug
# cài lên thiết bị/emulator đang kết nối
./gradlew installDebug
```

Trên Windows dùng `gradlew.bat` thay cho `./gradlew`.

3. Kiểm tra device/emulator:
```bash
adb devices
adb logcat
```

---

## Mẹo dùng VS Code ✨
- Tạo file `.env` trong `backend/` hoặc export biến môi trường cho `MONGODB_URI`/`DB_NAME`.
- Nếu bạn muốn, tôi có thể tạo sẵn `.vscode/launch.json` để debug `backend/server.js` và `tasks.json` để chạy các task Gradle từ Command Palette.

---

## Gỡ lỗi nhanh ⚠️
- Nếu không tìm thấy `adb`: thêm `<SDK>/platform-tools` vào `PATH` và đặt `ANDROID_SDK_ROOT` (User env var), sau đó khởi động lại VS Code.
- Nếu gặp lỗi Gradle/AGP: chạy `./gradlew --refresh-dependencies` rồi mở `app1` bằng Android Studio để sync và xem lỗi chi tiết.

---

Muốn tôi làm tiếp gì? Chọn một trong các tùy chọn sau: **A** — thêm `.vscode/launch.json` để debug Node, **B** — thêm `tasks.json` cho Gradle (build/install), **C** — thêm hỗ trợ `dotenv` để tự load `.env`. Bạn muốn tôi thực hiện mục nào?