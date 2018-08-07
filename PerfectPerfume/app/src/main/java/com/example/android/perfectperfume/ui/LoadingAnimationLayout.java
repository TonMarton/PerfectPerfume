package com.example.android.perfectperfume.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.perfectperfume.R;

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
                        //createHeightAnimation().start();
                    }
                });
    }
}