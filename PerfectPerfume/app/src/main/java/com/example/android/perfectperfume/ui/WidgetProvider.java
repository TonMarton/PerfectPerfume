package com.example.android.perfectperfume.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.utilities.SignInHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WidgetProvider extends AppWidgetProvider implements SignInHandler.SignInHelper, ValueEventListener {

    private RemoteViews remoteViews;
    private Context context;
    private String uuid;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        this.context = context;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            Intent intent = new Intent(context, LoginActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            remoteViews.setOnClickPendingIntent(R.id.widget_root, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
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
    }

    @Override
    public void showLoginInterface() {
        //there is no signed in user
        if (remoteViews != null) {
            String text = context.getResources().getString(R.string.widget_nouser_text);
            remoteViews.setTextViewText(R.id.widget_tv, text);
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot ds) {
        int count = (int) ds.child("orders/" + uuid + "/meta/count").getValue();
        String text = " to be arriving.";
        switch (count) {
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
        Log.e("This is the text",text);
        remoteViews.setTextViewText(R.id.widget_tv, text);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
