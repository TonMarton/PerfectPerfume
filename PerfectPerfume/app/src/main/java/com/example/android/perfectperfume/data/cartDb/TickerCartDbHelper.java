package com.example.android.perfectperfume.data.cartDb;

import android.support.annotation.NonNull;

import com.example.android.perfectperfume.data.Cart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/* TickerCartDbHelper is intended to be instantiated only by Cart objects.
 * Extends CartDbHelper with the ability to warn the housing Cart object about cart element count.
 * */

public class TickerCartDbHelper extends CartDbHelper {

    private TickerCallbacks callbacks;

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        callbacks.sendDatabaseError();
    }

    public interface TickerCallbacks {
        void updateCartTicker(boolean isEmpty);
        void sendDatabaseError();
    }

    public TickerCartDbHelper(Cart cart) {
        super();
        try {
            callbacks = cart;
        } catch (ClassCastException e) {
            throw new ClassCastException("Cart should implement TickerCallbacks");
        }
    }

    @Override
    protected boolean getCartData(DataSnapshot ds) {
        boolean isEmpty = super.getCartData(ds);
        callbacks.updateCartTicker(isEmpty);
        return true;
    }
}
