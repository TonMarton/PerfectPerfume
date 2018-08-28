package com.example.android.perfectperfume.data;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.example.android.perfectperfume.data.cartDb.CheckOutCartDbHelper;
import com.example.android.perfectperfume.ui.checkoutActivity.CheckOutFragment;
import com.example.android.perfectperfume.utilities.PaymentHelper;

import java.util.ArrayList;
import java.util.List;

public class CheckOutCart implements CheckOutCartDbHelper.CheckOutCartDbHelperCallbacks,
        PerfumeDbHelper.PerfumeDataCallbacks, PaymentHelper.PaymentCallbacks {

    private FragmentActivity activity;
    private CheckOutCartCallbacks callbacks;
    private CheckOutCartDbHelper cartDbHelper;
    private PerfumeDbHelper perfumeDbHelper;
    private PaymentHelper paymentHelper;
    private boolean cartItemIdsDelivered = false;
    private boolean perfumeItemsDelivered = false;
    private List<Integer> ids;
    private List<Integer> counts;
    private List<Perfume> items = new ArrayList<>();

    public interface CheckOutCartCallbacks {
        void generateItems(List<Perfume> perfumes, List<Integer> counts);
        void readyToPay(boolean ready);
        void removeCartItemsViews();
    }

    public CheckOutCart(CheckOutFragment fragment) {
        try {
            callbacks = fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException("CheckOutFragment should implement CheckOutCartCallbacks");
        }
        activity = fragment.getActivity();
        cartDbHelper = new CheckOutCartDbHelper(this);
        perfumeDbHelper = new PerfumeDbHelper(this);
        paymentHelper = new PaymentHelper(this, activity);
    }

    public void changeItemCount(int id, boolean isPlus) {
        cartDbHelper.changeItemCount(id, isPlus);
    }

    public void onActivityResumed() {
        cartDbHelper.refreshCartData();
    }

    public void createPaymentRequest() {
        paymentHelper.createPaymentRequest();
    }

    public void deliverPaymentResponse(int requestCode, int resultCode, Intent data) {
        //TODO: deliver only if returned the result code is ok
        paymentHelper.handleActivityResult(requestCode, resultCode, data);
        cartDbHelper.addOrder();
        callbacks.removeCartItemsViews();
    }

    @Override
    public void processCartItems(List<Integer> ids, List<Integer> counts) {
        if (perfumeItemsDelivered) {
            sortOutItems(ids, items);
            callbacks.generateItems(items, counts );
            return;
        }
        this.ids = ids;
        this.counts = counts;
        cartItemIdsDelivered = true;
    }

    @Override
    public void deliverPerfumes(List<Perfume> items) {
        if (cartItemIdsDelivered) {
            sortOutItems(ids, items);
            callbacks.generateItems(this.items, counts);
            return;
        }
        this.items = items;
        perfumeItemsDelivered = true;
    }

    @Override
    public void sendDatabaseError() {
        //TODO: send dab error
    }

    @Override
    public void isReadyToPay(boolean ready) {
        callbacks.readyToPay(ready);
    }

    private void sortOutItems(List<Integer> ids, List<Perfume> items) {
        for (Integer id : ids) {
            for (Perfume item : items) {
                if (item.getId() == id) {
                    this.items.add(item);
                    items.remove(item);
                    break;
                }
            }
        }
    }
}
