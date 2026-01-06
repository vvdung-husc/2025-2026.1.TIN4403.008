package com.example.project_01;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
public class Utils {
    /**
     * Hiển thị một hộp thoại thông báo (AlertDialog) đơn giản.
     * Hàm này cần được chạy trên UI Thread.
     *
     * @param context Context của Activity hoặc Fragment đang hiển thị.
     * @param title   Tiêu đề của hộp thoại.
     * @param message Nội dung thông báo.
     */
    public static void showAlert(Context context, String title, String message) {
        // Kiểm tra xem context có hợp lệ và Activity có đang hoạt động không
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
            return; // Không hiển thị dialog nếu activity đã bị hủy
        }

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    dialog.dismiss(); // Đóng hộp thoại khi nhấn nút
                })
                .setIcon(android.R.drawable.ic_dialog_alert) // Icon cảnh báo
                .show();
    }
}
