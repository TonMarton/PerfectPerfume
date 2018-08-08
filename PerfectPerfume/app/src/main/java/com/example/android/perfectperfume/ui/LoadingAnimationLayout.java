package com.example.android.perfectperfume.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.ui.checkoutActivity.CheckOutCardLayout;
import com.google.android.gms.common.images.internal.LoadingImageView;

public class LoadingAnimationLayout extends FrameLayout {

    private ImageView imageView;

    public LoadingAnimationLayout(@NonNull Context context) {
        super(context);
        init();
        this.setVisibility(VISIBLE);
    }

    public LoadingAnimationLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        bindViews();
    }

    private void bindViews() {
        LayoutInflater inf = LayoutInflater.from(getContext());
        imageView = (ImageView) inf.inflate(R.layout.loading_animation_layout, this, false);
        this.addView(imageView);
    }

    public void startAnimation() {
        this.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            LoadingAnimationLayout.this.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        } else {
                            LoadingAnimationLayout.this.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                        //TODO: could remove this line:
                        LoadingAnimationLayout.this.invalidate();
                        assambleAndLaunchAnimations();
                    }
                });
    }

    private void assambleAndLaunchAnimations() {
        final int BG_TRANSITION_LENGTH = 1000;
        TransitionDrawable transitionDrawable = (TransitionDrawable) this.getBackground();
        ValueAnimator sizeAnim = createContainerSizeAnim();
        final ObjectAnimator rotationAnim = createRotatingAnim();
        sizeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rotationAnim.setRepeatCount(ValueAnimator.INFINITE);
                rotationAnim.start();
            }
        });
        transitionDrawable.startTransition(BG_TRANSITION_LENGTH);
        sizeAnim.setStartDelay(BG_TRANSITION_LENGTH);
        sizeAnim.start();
    }

    private ValueAnimator createContainerSizeAnim() {
        int initialHeight = this.getMeasuredHeight();
        int finalHeight = imageView.getMeasuredHeight();

        final ValueAnimator shrinkContainer =
                ValueAnimator.ofInt(initialHeight, finalHeight);
        shrinkContainer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = LoadingAnimationLayout.this.getLayoutParams();
                layoutParams.width = val;
                layoutParams.height = val;
                LoadingAnimationLayout.this.setLayoutParams(layoutParams);
            }
        });
        shrinkContainer.setDuration(500);
        return shrinkContainer;
    }

    private ObjectAnimator createRotatingAnim() {
        ObjectAnimator rotationAnim = ObjectAnimator
                .ofFloat(imageView, "rotation", 0f, 360f);
        rotationAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        rotationAnim.setDuration(700);
        return rotationAnim;
    }
}