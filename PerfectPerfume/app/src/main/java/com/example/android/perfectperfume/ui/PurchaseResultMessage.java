package com.example.android.perfectperfume.ui;

import android.app.Activity;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.android.perfectperfume.R;

public class PurchaseResultMessage {
    public static void displayMessage(Activity activity) {
        final FrameLayout root = activity.findViewById(android.R.id.content);
        final ViewGroup view = (ViewGroup) activity.getLayoutInflater()
                .inflate(R.layout.purchase_message_layout, root, false);
        root.addView(view);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                root.removeView(view);
            }
        }, 6000);
    }
}
