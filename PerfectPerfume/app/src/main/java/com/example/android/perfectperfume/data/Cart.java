package com.example.android.perfectperfume.data;

import android.content.Context;
import android.util.Log;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.data.cartDb.TickerCartDbHelper;
import com.example.android.perfectperfume.utilities.AnimationTimer;

public class Cart implements TickerCartDbHelper.TickerCallbacks,
        AnimationTimer.AnimationTimerCallbacks {

    //TODO: cart on click not working while animated
    private TickerCartDbHelper dbHelper;
    private Context context;
    private CartCallbacks callbacks;
    private AnimationTimer animationTimer;
    private boolean isCartEmpty = true;

    public interface CartCallbacks{
        void animateCartIcon();
    }

    public Cart(Context context) {
        try {
            callbacks = (CartCallbacks) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement CartCallbacks");
        }
        dbHelper = new TickerCartDbHelper(this);
        this.context = context;
    }

    public void activityPaused() {
        if (animationTimer != null) animationTimer.cancelTimer();
    }

    public void activityResumed() {
        isCartEmpty = true;
        if (animationTimer != null) animationTimer.restartTimer();
        dbHelper.refreshCartData();
    }

    @Override
    public void updateCartTicker(boolean isEmpty) {
        if (isCartEmpty && !isEmpty) {
            animateCart();
        }
        isCartEmpty = isEmpty;
        if (!isEmpty) {
            if (animationTimer == null) {
                animationTimer = new AnimationTimer(this,
                        context.getResources().getInteger(R.integer.cart_animation_offset));
            } else {
                animationTimer.restartTimer();
            }
        }
        Log.d("CART is empty", Boolean.toString(isEmpty));
    }

    @Override
    public void animateCart() {
        callbacks.animateCartIcon();
        Log.d("ANIMATED", "CartIcon");
    }

    @Override
    public boolean isCartEmpty() {
        return isCartEmpty;
    }

    public void addItemToCart(int id) {
        dbHelper.increaseItemCount(id);
    }
}