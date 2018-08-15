package com.example.android.perfectperfume.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class LayoutSwapAnimator {

    public static void swap(FrameLayout visibleLayout, FrameLayout invisibleLayout, int width) {
        repositionRightLayout(invisibleLayout, width);
        visibleLayout.getLayoutParams().width = width;
        createCombinedAnimationSet(visibleLayout, invisibleLayout, width).start();
    }

    private static void repositionRightLayout(FrameLayout rightL, int width) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rightL.getLayoutParams();
        params.leftMargin = width;
        params.width = width;
        rightL.setLayoutParams(params);
        rightL.setVisibility(View.VISIBLE);
    }

    private static AnimatorSet createCombinedAnimationSet(final FrameLayout leftL, FrameLayout rightL, int width) {
        AnimatorSet combinedSet = new AnimatorSet();
        ValueAnimator outAnim = createSlideLeftAnimation(0, -width, leftL);
        ValueAnimator inAnim = createSlideLeftAnimation( width, 0, rightL);
        combinedSet.play(outAnim).with(inAnim);
        combinedSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                leftL.setVisibility(View.GONE);
                ViewGroup root = (ViewGroup) leftL.getParent();
                root.removeView(leftL);
            }
        });
        combinedSet.setInterpolator(new DecelerateInterpolator());
        return combinedSet;
    }

    private static ValueAnimator createSlideLeftAnimation(int start, int end, final FrameLayout layout) {
        final ValueAnimator slideLeft = ValueAnimator.ofInt(start, end);
        slideLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
                layoutParams.leftMargin = val;
                layout.setLayoutParams(layoutParams);
            }
        });
        slideLeft.setDuration(1000);
        return slideLeft;
    }
}
