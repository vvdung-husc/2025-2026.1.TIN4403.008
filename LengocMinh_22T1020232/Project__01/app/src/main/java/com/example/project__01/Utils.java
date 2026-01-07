package com.example.project__01;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

public class Utils {
    public static void showAlert(Context context, String title, String message) {
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đồng ý", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}