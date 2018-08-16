package com.example.android.perfectperfume.ui;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

public class WarningMessage {

    public static void createConnectionWarning(Context context, String text) {
        View rootView;
        try {
            rootView = ((Activity) context)
                    .getWindow().getDecorView().findViewById(android.R.id.content);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass() + " should be from an Activity.");
        }
        showSnackbar(rootView, text);
    }

    public static void createConnectionWarning(Activity activity, String text) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        showSnackbar(rootView, text);
    }

    private static void showSnackbar(View rootView, String text) {
        if (rootView == null)
            Log.e("WarningMessage Error", "The supplied rootview is null");
        else {
            Snackbar.make(rootView, text, Snackbar.LENGTH_LONG).show();
        }
        Log.d("WarningMessage", "showSnackbar");
    }
}
