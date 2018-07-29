package com.example.android.perfectperfume.utilities;

import android.os.Handler;

import com.example.android.perfectperfume.data.Cart;
/*TODO: maybe make this into a singleton, since it would be practical
 to not restart the times over and over, when browsing perfumes*/
public class AnimationTimer {

    private static int ANIMATION_OFFSET;

    private static Handler handler;
    private AnimationTimerCallbacks callbacks;

    public interface AnimationTimerCallbacks {
        void animateCart();
        boolean isCartEmpty();
    }

    public AnimationTimer(Cart cart, int animationOffset) {
        try {
            callbacks = cart;
            ANIMATION_OFFSET = animationOffset;
        } catch (ClassCastException e) {
            throw new ClassCastException("Cart should implement AnimationTimerCallbacks");
        }
        handler = createNewHandler();
    }

    private Handler createNewHandler() {
        Handler handler = new Handler();
        handler.postDelayed(new AnimationTimerRunnable(), ANIMATION_OFFSET);
        return handler;
    }

    public void cancelTimer() {
        handler.removeCallbacksAndMessages(null);
    }

    public void restartTimer() {
        handler = createNewHandler();
    }

    private class AnimationTimerRunnable implements Runnable {

        @Override
        public void run() {
            if (callbacks.isCartEmpty()) {
                return;
            } else {
                callbacks.animateCart();
                handler = createNewHandler();
            }
        }
    }
}