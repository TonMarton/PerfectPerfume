package com.example.android.perfectperfume.data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PerfumeDbHelper implements ValueEventListener{

    private DatabaseReference dbRef;
    private PerfumeDataCallbacks callbacks;

    private List<Perfume> items = new ArrayList<>();
    private int counter;

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
        for (DataSnapshot cds : ds.child("perfumes").getChildren()) {
            items.add(cds.getValue(Perfume.class));
        }
        setImageIds(items);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    private void setImageIds(List<Perfume> items) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        counter = items.size();
        for (Perfume item : items) {
            getImageUri(item, storageRef);
        }
    }

    private void getImageUri(final Perfume item, StorageReference storageRef) {
        String pathString = Integer.toString(item.getId()) + ".jpg";
        StorageReference ref = storageRef.child(pathString);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String uriString = uri.toString();
                item.setImageurl(uriString);
                notifyCounter();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                notifyCounter();
                //TODO: handle the errors
            }
        });
    }

    //deliver the items only, once all the imageUrl-s are fetched and set
    private void notifyCounter() {
        counter--;
        if (counter <= 0) {
            callbacks.deliverPerfumes(items);
        }
    }
}