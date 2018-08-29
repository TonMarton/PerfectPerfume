package com.example.android.perfectperfume.ui;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.android.perfectperfume.R;

public class PurchaseResultMessage extends AsyncTask<Object, Object, Object> {

    private Activity activity;

    public void displayMessage(final Activity activity) {
        this.activity = activity;
        final FrameLayout root = activity.findViewById(android.R.id.content);
        final ViewGroup view = (ViewGroup) activity.getLayoutInflater()
                .inflate(R.layout.purchase_message_layout, root, false);
        root.addView(view);
        this.execute(new Object());
    }

    @Override
    protected Object doInBackground(Object... objects) {
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        activity.sendBroadcast(updateIntent);
        return true;
    }

    @Override
    protected void onPostExecute(Object o) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PurchaseResultMessage.this.activity.finish();
            }
        }, 4000);
    }
}
