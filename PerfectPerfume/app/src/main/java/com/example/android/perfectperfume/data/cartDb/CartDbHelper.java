package com.example.android.perfectperfume.data.cartDb;

import android.support.annotation.NonNull;

import com.example.android.perfectperfume.utilities.SignInHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public abstract class CartDbHelper implements ValueEventListener {

    DatabaseReference dbRef;
    String userId;
    List<Integer> ids = new ArrayList<>();
    List<Integer> counts = new ArrayList<>();

    static final String CARTS_DB_URL = "carts/";

    CartDbHelper() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addListenerForSingleValueEvent(this);
        userId = SignInHandler.getCurrentUser().getUid();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot ds) {
        getCartData(ds);
    }

    public void refreshCartData() {
        dbRef.addListenerForSingleValueEvent(this);
    }

    public void increaseItemCount(int id) {
        int count = 0;
        boolean exists = false;
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == id) {
                count = counts.get(i) + 1;
                counts.set(i, count);
                exists = true;
            }
        }
        if (!exists) {
            ids.add(id);
            counts.add(1);
            count = 1;
        }
        dbRef.child(CARTS_DB_URL + userId + "/" + Integer.toString(id)).setValue(count);
    }

    protected boolean getCartData(DataSnapshot ds) {
        boolean isEmpty = true;
        ids = new ArrayList<>();
        counts = new ArrayList<>();
        for (DataSnapshot cds : ds.child(CARTS_DB_URL + userId).getChildren()) {
            ids.add(Integer.parseInt(cds.getKey()));
            Long value = (Long) cds.getValue();
            if (value != null) {
                counts.add(Integer.parseInt(cds.getValue().toString()));
            } else {
                throw new Error("Database Error, illegal value has been returned.");
            }
            isEmpty = false;
        }
        return isEmpty;
    }
}