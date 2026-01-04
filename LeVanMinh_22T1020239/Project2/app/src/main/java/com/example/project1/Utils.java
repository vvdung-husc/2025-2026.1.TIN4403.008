package com.example.project1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;

public class Utils {
    private static final String PREF_NAME = "MyLoginPref";
    private static final String KEY_TOKEN = "auth_token";

    /**
     * Hiển thị một hộp thoại thông báo (AlertDialog) đơn giản.
     * Cần chạy trên UI Thread.
     */
    public static void showAlert(Context context, String title, String message) {
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Lưu Token vào SharedPreferences.
     */
    public static void saveToken(Context context, String token) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    /**
     * Lấy Token đã lưu.
     * @return Token hoặc chuỗi rỗng nếu chưa đăng nhập.
     */
    public static String getToken(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(KEY_TOKEN, "");
    }

    /**
     * Xóa Token (Đăng xuất).
     */
    public static void clearToken(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(KEY_TOKEN);
        editor.apply();
    }
}