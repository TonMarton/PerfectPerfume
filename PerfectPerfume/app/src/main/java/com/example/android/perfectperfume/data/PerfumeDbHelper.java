package com.example.android.perfectperfume.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PerfumeDbHelper implements ValueEventListener{

    private DatabaseReference dbRef;
    private PerfumeDataCallbacks callbacks;

    public interface PerfumeDataCallbacks {
        void deliverPerfumes(List<Perfume> items);
    }

    public PerfumeDbHelper(Object object) {
        try {
            callbacks = (PerfumeDataCallbacks) object;
        } catch (ClassCastException e) {
            throw new ClassCastException(object.toString() + "should implement " +
                    "PerfumeDataCallbacks");
        }
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addListenerForSingleValueEvent(this);
    }

    // ValueEventListener methods
    @Override
    public void onDataChange(@NonNull DataSnapshot ds) {
        List<Perfume> items = new ArrayList<>();
        for (DataSnapshot cds : ds.child("perfumes").getChildren()) {
            items.add(cds.getValue(Perfume.class));
        }
        callbacks.deliverPerfumes(items);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}