package com.example.android.perfectperfume.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.utilities.SignInHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WidgetProvider extends AppWidgetProvider implements SignInHandler.SignInHelper, ValueEventListener {

    private final String DEBUG_TAG = "Perfume Widget";
    private RemoteViews remoteViews;
    private Context context;
    private AppWidgetManager manager;
    private int[] ids;
    private String uuid;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        this.context = context;
        this.ids = appWidgetIds;
        this.manager = appWidgetManager;

        //start fetching update data
        SignInHandler handler = new SignInHandler(this, context);

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            Intent intent = new Intent(context, LoginActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            remoteViews.setOnClickPendingIntent(R.id.widget_root, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        Log.d(DEBUG_TAG, "onUpdate");
    }


    @Override
    public void startActivityForAuthentication(Intent signInIntent) {
        //unnecessary
    }

    @Override
    public void signInReady() {

        uuid = SignInHandler.getCurrentUser().getUid();
        //start fetching the data from the cart db
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addListenerForSingleValueEvent(this);
        Log.d(DEBUG_TAG, "signInReady");
    }

    @Override
    public void showLoginInterface() {
        //there is no signed in user
        if (remoteViews != null) {
            String text = context.getResources().getString(R.string.widget_nouser_text);
            modifyWidgetText(text);
        }
        Log.d(DEBUG_TAG, "showLoginInterface");
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot ds) {
        long count = (long) ds.child("orders/" + uuid + "/meta/count").getValue();
        String text = " to be arriving.";
        int intCount =  Integer.parseInt(Long.toString(count));
        switch (intCount) {
            case 0:
                text = "You have no orders" + text;
                break;
            case 1:
                text = "You have one order" + text;
                break;
            default:
                text = "You have " + count + " orders" + text;
                break;
        }
        if (remoteViews != null) {
            Log.d(DEBUG_TAG, "onDataChange - text: " + text);
            modifyWidgetText(text);
        } else {
            Log.d(DEBUG_TAG, "onDataChange - remoteViews was null");
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.d(DEBUG_TAG, "onCancelled");
    }

    private void modifyWidgetText(String text) {
        final int N = ids.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = ids[i];
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            remoteViews.setTextViewText(R.id.widget_tv, text);
            manager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
