package com.example.android.perfectperfume.ui.checkoutActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.data.Perfume;
import com.example.android.perfectperfume.data.CheckOutCart;
import com.example.android.perfectperfume.ui.LoadingAnimationLayout;
import com.example.android.perfectperfume.ui.PurchaseResultMessage;
import com.example.android.perfectperfume.ui.WarningMessage;
import com.example.android.perfectperfume.utilities.PaymentHelper;

import java.text.DecimalFormat;
import java.util.List;

public class CheckOutFragment extends Fragment implements
        CheckOutCardLayout.CheckOutCardLayoutCallbacks, CheckOutCart.CheckOutCartCallbacks{

    private Activity activity;
    private CheckOutFragmentCallbacks callbacks;
    private CheckOutCart checkOutCart;
    private double total;

    private LinearLayout paymentImpossibleLayout;
    private RelativeLayout containerLayout;
    private ScrollView scrollView;
    private LinearLayout itemList;
    private LoadingAnimationLayout loadingLayout;
    private TextView totalTextView;
    private RelativeLayout googlePayLayout;

    public interface CheckOutFragmentCallbacks {
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void setCheckOutFragment(CheckOutFragment fragment);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callbacks = (CheckOutFragmentCallbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass() + " should implement " +
                    "CheckOutFragmentCallbacks");
        }
        callbacks.setCheckOutFragment(this);
        activity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkOutCart = new CheckOutCart(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        loadingLayout.startAnimation();
        googlePayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOutCart.createPaymentRequest();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkOutCart.onActivityResumed();
    }

    public void deliverActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PaymentHelper.PAYMENT_REQUEST_CODE) {
            checkOutCart.deliverPaymentResponse(requestCode, resultCode, data);
            PurchaseResultMessage.displayMessage(activity);
        }
    }

    public void updateTotal(double amount) {
        total += amount;
        if (total == 0) {
        }
        DecimalFormat df = new DecimalFormat("#.##");
        String totalString = "€" + df.format(total);
        totalTextView.setText(totalString);
    }

    @Override
    public void changeItemCount(int id, boolean isPlus) {
        checkOutCart.changeItemCount(id, isPlus);
    }

    @Override
    public void destroyItem(CheckOutCardLayout layout) {
        itemList.removeView(layout);
    }

    @Override
    public void generateItems(final List<Perfume> perfumes, final List<Integer> counts) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(loadingLayout, "alpha", 1, 0);
        fadeOut.setDuration(1300);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                inflateCheckOutCards(perfumes, counts);
                loadingLayout.setVisibility(View.GONE);
                loadingLayout = null;
            }
        });
        fadeOut.start();
    }

    @Override
    public void readyToPay(boolean ready) {
        if (ready) {
            googlePayLayout.setVisibility(View.VISIBLE);
        } else {
            WarningMessage.createConnectionWarning(getContext(), "Payment is not possible.");
        }
    }

    @Override
    public void removeCartItemsViews() {
        itemList.removeAllViews();
        totalTextView.setText("0");
        googlePayLayout.setVisibility(View.INVISIBLE);
    }

    private void bindViews(View view) {
        containerLayout = view.findViewById(R.id.checkout_container_rl);
        scrollView = view.findViewById(R.id.checkout_sv);
        itemList = view.findViewById(R.id.item_list_ll);
        loadingLayout = view.findViewById(R.id.loading_animation_layout_fl);
        totalTextView = view.findViewById(R.id.total_tv);
        googlePayLayout = view.findViewById(R.id.google_pay_rl);
        paymentImpossibleLayout = view.findViewById(R.id.payment_impossible_ll);
    }

    private void inflateCheckOutCards(List<Perfume> items, List<Integer> counts) {
        if (activity != null) {
            LayoutInflater inf = activity.getLayoutInflater();
            for (int i = 0; i < items.size(); i++) {
                CheckOutCardLayout item = (CheckOutCardLayout) inf.inflate(getResources().getLayout
                        (R.layout.checkout_item_layout), itemList, false);
                item.attachCheckOutCardToFragment(this);
                item.setData(items.get(i), counts.get(i));
                itemList.addView(item);
            }
        } else {
            Log.e("CheckOutFragment",
                    "Fragment not initialised or attached to Activity.");
        }
    }
}
