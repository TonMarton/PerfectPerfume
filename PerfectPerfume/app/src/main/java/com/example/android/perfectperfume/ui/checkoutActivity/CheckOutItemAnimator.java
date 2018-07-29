package com.example.android.perfectperfume.ui.checkoutActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.perfectperfume.R;

/* Holds functionality to animate CheckOutCardLayout objects.
*  It acts as buffer, preventing many unnecessary animations to go simultaneously. */

public class CheckOutItemAnimator {

    private static CheckOutItemAnimator instance;
    private boolean isAnimating = false;
    private CheckOutCardLayout openedItem;
    private int closedHeight;

    private CheckOutItemAnimator() {}

    public static CheckOutItemAnimator getInstance() {
        if (instance == null) {
            instance = new CheckOutItemAnimator();
        }
        return instance;
    }

    public boolean animateItem(CheckOutCardLayout item) {
        if (isAnimating) {
            return false;
        } else {
            isAnimating = true;
            if (openedItem == null) {
                AnimatorSet openingSet = createAnimationSets(item, false);
                openingSet.start();
                return true;
            } else if (item != openedItem) {
                AnimatorSet closingSet = createAnimationSets(openedItem, true);
                AnimatorSet openingSet = createAnimationSets(item, false);
                AnimatorSet switchSet = new AnimatorSet();
                switchSet.play(closingSet).with(openingSet);
                switchSet.start();
                openedItem.notifyAboutClosing();
                return true;
            } else {
                AnimatorSet closingSet = createAnimationSets(item, true);
                closingSet.start();
                return true;
            }
        }
    }

    private AnimatorSet createAnimationSets(CheckOutCardLayout item, boolean isOpen) {
        Resources res = item.getContext().getResources();
        AnimatorSet firstSet =
                !isOpen ? createCardAnimSet(item, res, isOpen) :
                        createCardDetailAnimSet(item, res, isOpen);
        AnimatorSet secondSet =
                !isOpen ? createCardDetailAnimSet(item, res, isOpen) :
                        createCardAnimSet(item, res, isOpen);
        addAnimatorListeners(firstSet, secondSet, item, isOpen, res);
        return firstSet;
    }

    private AnimatorSet createCardAnimSet(CheckOutCardLayout item, Resources res, boolean isOpen) {
        ValueAnimator increaseHeight = animateHeight(item, res, isOpen);
        ObjectAnimator changeBgColor = createChangeBgColor(item, isOpen);
        ObjectAnimator elevateItem =
                createElevateAnim(item, res, isOpen, false);
        AnimatorSet set = new AnimatorSet();
        set.play(increaseHeight).with(changeBgColor).with(elevateItem);
        return set;
    }

    private AnimatorSet createCardDetailAnimSet(
            CheckOutCardLayout item, Resources res, boolean isOpen) {
        ObjectAnimator buttonsFade =
                createFadeAnim(item.counterButtonsLayout, isOpen);
        ObjectAnimator priceFade = createFadeAnim(item.priceContainerLayout, isOpen);
        ObjectAnimator elevatePlusButton =
                createElevateAnim(item.plusImageView, res, isOpen, true);
        ObjectAnimator elevateMinusButton =
                createElevateAnim(item.minusImageView, res, isOpen, true);
        AnimatorSet set = new AnimatorSet();
        if (!isOpen) {
            set.play(buttonsFade).with(priceFade);
            set.play(elevatePlusButton).with(elevateMinusButton).after(buttonsFade);
        } else {
            set.play(elevatePlusButton).with(elevateMinusButton);
            set.play(buttonsFade).with(priceFade).after(elevatePlusButton);
        }

        return set;
    }

    private void addAnimatorListeners(AnimatorSet firstSet, final AnimatorSet secondSet,
                                      final CheckOutCardLayout item, final boolean isOpen,
                                      final Resources res) {
        firstSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isOpen) {
                    item.makeDetailsVisible();
                    secondSet.start();
                } else {
                    item.makeDetailsInvisible();
                    secondSet.start();
                }
            }
        });
        secondSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isOpen) {
                    openedItem = item;
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        item.setBackground(ContextCompat.getDrawable(item.getContext(), R.drawable.checkout_item_bg));
                    } else {
                        item.setBackgroundDrawable(res.getDrawable(R.drawable.checkout_item_bg));
                    }
                    if (item == openedItem) openedItem = null;
                }
                isAnimating = false;
            }
        });
    }

    private ValueAnimator animateHeight(final CheckOutCardLayout itemLayout, Resources res, boolean isOpen) {
        int initialHeight = itemLayout.getMeasuredHeight();
        int finalHeight = !isOpen ? (int) res.getDimension(R.dimen.counter_layout_height) :
                closedHeight;

        if (!isOpen) closedHeight = initialHeight;
        final ValueAnimator increaseHeight =
                ValueAnimator.ofInt(initialHeight, finalHeight);
        increaseHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = itemLayout.getLayoutParams();
                layoutParams.height = val;
                itemLayout.setLayoutParams(layoutParams);
            }
        });
        increaseHeight.setDuration(200);
        return increaseHeight;
    }

    private ObjectAnimator createChangeBgColor(View view, boolean isOpen) {
        int colorAccentLight = ContextCompat.getColor(view.getContext(), R.color.colorAccentLight);
        int colorAccent = ContextCompat.getColor(view.getContext(), R.color.colorAccent);

        int initialColor = !isOpen ? colorAccentLight : colorAccent;
        int finalColor = !isOpen ? colorAccent : colorAccentLight;

        final ObjectAnimator changeBgColor = ObjectAnimator.ofObject(view,
                "backgroundColor",
                new ArgbEvaluator(), initialColor, finalColor);
        changeBgColor.setDuration(200);
        return changeBgColor;
    }

    private ObjectAnimator createFadeAnim(View view, boolean isOpen) {
        int initialAlpha = !isOpen ? 0 : 1;
        int finalAlpha = !isOpen ? 1 : 0;

        final ObjectAnimator fadeAnim =
                ObjectAnimator.ofFloat(view, View.ALPHA, initialAlpha, finalAlpha);
        fadeAnim.setDuration(200);
        return fadeAnim;
    }

    private ObjectAnimator createElevateAnim(View view, Resources res, boolean isOpen,
                                             boolean isButton) {
        int lowElevation = 0;
        int highElevation = isButton ? (int) res.getDimension(R.dimen.counter_btn_active_elevation)
                :(int) res.getDimension(R.dimen.checkout_item_active_elevation);
        int initialElevation = !isOpen ? lowElevation : highElevation;
        int finalElevation = !isOpen ? highElevation : lowElevation;

        final ObjectAnimator elevateAnim = ObjectAnimator.ofFloat(view, "translationZ", initialElevation, finalElevation);
        int duration = 200;
        if (isButton) duration = 200;
        elevateAnim.setDuration(duration);
        return elevateAnim;
    }
}