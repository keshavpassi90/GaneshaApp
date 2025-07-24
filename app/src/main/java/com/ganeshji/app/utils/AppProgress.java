package com.ganeshji.app.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

public class AppProgress {

    private final Dialog dialog;

    public AppProgress(@NonNull Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Use large progress style for a bigger spinner
        ProgressBar spinner = new ProgressBar(
                context,
                null,
                android.R.attr.progressBarStyleSmall
        );

        // Increased size and padding
        int sizePx = dpToPx(context, 70);     // Container size
        int padPx  = dpToPx(context, 12);     // Padding around spinner

        FrameLayout box = new FrameLayout(context);
        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(sizePx, sizePx, Gravity.CENTER);
        box.setLayoutParams(lp);
        box.setPadding(padPx, padPx, padPx, padPx);
        box.addView(spinner);

        // Rounded white background
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFFFFFFFF);
        bg.setCornerRadius(dpToPx(context, 12));
        box.setBackground(bg);

        dialog.setContentView(box);

        Window w = dialog.getWindow();
        if (w != null) {
            w.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            w.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams params = w.getAttributes();
            params.dimAmount = 0.45f;
            w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            w.setAttributes(params);
        }

        dialog.setCancelable(false);
    }

    private static int dpToPx(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public void show() {
        if (!dialog.isShowing()) dialog.show();
    }

    public void dismiss() {
        if (dialog.isShowing()) dialog.dismiss();
    }
}
