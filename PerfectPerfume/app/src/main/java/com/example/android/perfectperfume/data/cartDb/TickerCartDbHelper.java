package com.example.android.perfectperfume.data.cartDb;

import com.example.android.perfectperfume.data.Cart;
import com.google.firebase.database.DataSnapshot;

/* TickerCartDbHelper is intended to be instantiated only by Cart objects.
 * Extends CartDbHelper with the ability to warn the housing Cart object about cart element count.
 * */

public class TickerCartDbHelper extends CartDbHelper {

    private TickerCallbacks callbacks;

    public interface TickerCallbacks {
        void updateCartTicker(boolean isEmpty);
    }

    public TickerCartDbHelper(Cart cart) {
        super();
        try {
            callbacks = (TickerCallbacks) cart;
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
