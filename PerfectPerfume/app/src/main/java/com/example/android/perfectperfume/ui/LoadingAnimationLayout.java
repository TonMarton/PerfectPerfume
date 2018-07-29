package com.example.android.perfectperfume.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.perfectperfume.R;

public class LoadingAnimationLayout extends FrameLayout {

    private View fillerView;
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

    public void startAnimation() {
        createHeightAnimation().start();
    }

    private void init() {
        bindViews();
    }

    private void bindViews() {
        LayoutInflater inf = LayoutInflater.from(getContext());
        fillerView = inf.inflate(R.layout.filler_view, this, false);
        imageView = (ImageView) inf.inflate(R.layout.fillable_frame_imageview, this, false);
        this.addView(fillerView);
        this.addView(imageView);
    }

    private ValueAnimator createHeightAnimation() {
        int finalHeight = (int) ((double) this.getMeasuredHeight() * 0.65);
        final ValueAnimator increaseHeight =
                ValueAnimator.ofInt(0, finalHeight);
        increaseHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = fillerView.getLayoutParams();
                layoutParams.height = val;
                fillerView.setLayoutParams(layoutParams);
            }
        });
        increaseHeight.setDuration(3000);
        increaseHeight.setRepeatCount(ValueAnimator.INFINITE);
        return increaseHeight;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        final double WIDTH_PER_HEIGHT = 0.56743421052;
        w = (int) (h * WIDTH_PER_HEIGHT);
        setMeasuredDimension(w, h);
        if (oldw == 0) this.getLayoutParams().width = w;
    }
}