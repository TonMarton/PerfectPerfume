package com.example.android.perfectperfume.ui.checkoutActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.data.Perfume;

import java.text.DecimalFormat;

public class CheckOutCardLayout extends LinearLayout {

    protected FrameLayout counterLayout;
    protected LinearLayout nameLayout;
    protected LinearLayout counterButtonsLayout;
    private TextView nameTextView;
    private TextView perPiecePriceTextView;
    private TextView priceTextView;
    protected ImageView plusImageView;
    protected ImageView minusImageView;
    protected TextView counterTextView;
    protected LinearLayout priceContainerLayout;

    private CheckOutItemAnimator animator;
    private CheckOutCardLayoutCallbacks callbacks;
    private CheckOutFragment fragment;
    private boolean accessible = false;
    private Perfume perfume;
    private int count;

    public interface CheckOutCardLayoutCallbacks {
        void changeItemCount(int id, boolean isPlus);
        void updateTotal(double amount);
        void destroyItem(CheckOutCardLayout layout);
    }

    private boolean isOpen = false;

    public CheckOutCardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckOutCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void attachCheckOutCardToFragment(CheckOutFragment fragment) {
        try {
            callbacks = fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException("CheckOutFragment should implement CheckOutCardLayoutCallbacks");
        }
        this.fragment = fragment;
    }

    public void makeDetailsVisible() {
        counterButtonsLayout.setVisibility(VISIBLE);
        priceContainerLayout.setVisibility(VISIBLE);
        plusImageView.setOnClickListener(createClickListener(true));
        minusImageView.setOnClickListener(createClickListener(false));
    }

    public void makeDetailsInvisible() {
        counterButtonsLayout.setVisibility(GONE);
        priceContainerLayout.setVisibility(GONE);
        plusImageView.setOnClickListener(null);
        minusImageView.setOnClickListener(null);
    }

    public void notifyAboutClosing() {
        isOpen = false;
    }

    private void setPerfume(Perfume perfume) {
        this.perfume = perfume;
    }

    private void setCount(int count) {
        this.count = count;
    }

    public void setData(Perfume perfume, int count) {
        setPerfume(perfume);
        setCount(count);
        String string = Integer.toString(count);
        this.counterTextView.setText(string);
        setPriceText();
        this.nameTextView.setText(perfume.getName());
    }

    private void init() {
        inflateContents();
        bindViews();

        animator = CheckOutItemAnimator.getInstance();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = animator.animateItem(CheckOutCardLayout.this);
                if (success) isOpen = !isOpen;
            }
        });
    }

    private void inflateContents() {
        LayoutInflater inf = LayoutInflater.from(getContext());
        inf.inflate(getContext().getResources().getLayout(
                R.layout.checkout_item_contents_layout),
                this,
                true);
    }

    private void bindViews() {
        counterLayout = findViewById(R.id.counter_fl);
        nameLayout = findViewById(R.id.name_ll);
        counterButtonsLayout = counterLayout.findViewById(R.id.counter_btns_ll);
        nameTextView = nameLayout.findViewById(R.id.checkout_name_tv);
        priceContainerLayout = nameLayout.findViewById(R.id.checkout_price_container_ll);
        priceTextView = priceContainerLayout.findViewById(R.id.checkout_price_tv);
        perPiecePriceTextView = priceContainerLayout.findViewById(R.id.checkout_perpiece_price_tv);
        plusImageView = counterButtonsLayout.findViewById(R.id.plus_btn_iv);
        minusImageView = counterButtonsLayout.findViewById(R.id.minus_btn_iv);
        counterTextView = counterLayout.findViewById(R.id.counter_tv);
    }

    private View.OnClickListener createClickListener(final boolean isPlus) {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                count = isPlus ? count + 1 : count - 1;
                if (isInterfaceAccessible()) {
                    modifyDataAndUI(count, isPlus);
                }
            }
        };
    }

    private void modifyDataAndUI(int count, boolean isPlus) {
        callbacks.changeItemCount(perfume.getId(), isPlus);
        double price = Double.parseDouble(perfume.getPrice());
        callbacks.updateTotal(isPlus ? price : price * -1);
        if (count > 0) {
            changeCounterText(isPlus);
            changePriceText(count);
        } else {
            callbacks.destroyItem(this);
        }
    }

    private void changeCounterText(boolean isPlus) {
        int newCount = Integer.parseInt(counterTextView.getText().toString());
        newCount = isPlus ? newCount + 1 : newCount - 1;
        String newString = Integer.toString(newCount);
        counterTextView.setText(newString);
        changePriceText(newCount);
    }

    private double changePriceText(int count) {
        double price = Double.parseDouble(perfume.getPrice()) * (double) count;
        DecimalFormat df = new DecimalFormat("#.##");
        String priceString = df.format(price);
        priceString = "€" + priceString;
        priceTextView.setText(priceString);
        return price;
    }

    private void setPriceText() {
        double priceAmount = changePriceText(count);
        callbacks.updateTotal(priceAmount);
        String price = perfume.getPrice();
        price = " (€" + price + "/pcs.)";
        perPiecePriceTextView.setText(price);
    }

    private boolean isInterfaceAccessible() {
        if (accessible) return true;
        if (fragment != null && callbacks != null) {
            accessible = true;
            return true;
        } else {
            throw new Error("CheckOutCardLayout is trying to communicate with CheckOutFragment, " +
                    "but it is not bound to any.");
        }
    }
}