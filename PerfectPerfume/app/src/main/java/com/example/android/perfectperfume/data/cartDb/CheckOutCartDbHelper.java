package com.example.android.perfectperfume.data.cartDb;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.perfectperfume.data.CheckOutCart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

// only for checkout, adds functionality to delete cart contents
public class CheckOutCartDbHelper extends CartDbHelper {

    private CheckOutCartDbHelperCallbacks callbacks;
    private int orderCount = -1;

    private static final String ORDERS_DB_URL = "orders/";
    private static final String ORDERS_DB_LIST_URL = "orders";
    private static final String ORDERS_DB_COUNT_URL = "meta/count";

    public interface CheckOutCartDbHelperCallbacks {
        void processCartItems(List<Integer> ids, List<Integer> counts);
    }

    public CheckOutCartDbHelper(CheckOutCart cart) {
        super();
        try {
            callbacks = cart;
        } catch (ClassCastException e) {
            throw new ClassCastException("CheckOutCart should implement " +
                    "CheckOutCartDbHelperCallbacks");
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot ds) {
        super.onDataChange(ds);
        getOrders(ds);
        callbacks.processCartItems(ids, counts);
    }

    public void addOrder() {
        orderCount++;
        DatabaseReference order = dbRef.child(ORDERS_DB_URL + userId + "/" +
                ORDERS_DB_LIST_URL + "/" +
                (orderCount - 1)
        );
        for (int i = 0; i < ids.size(); i++) {
            String key = ids.get(i).toString();
            int value = counts.get(i);
            order.child(key).setValue(value);
            Log.d("ORDER_RECORD_ADDED_TO", order + key + ":" + Integer.toString(value));
        }
        dbRef.child(ORDERS_DB_URL + userId + "/" + ORDERS_DB_COUNT_URL).setValue(orderCount);
        deleteCartContents();
    }

    public void changeItemCount(int id, boolean isPlus) {
        int count = 0;
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == id) {
                count = counts.get(i);
                count = isPlus ? count + 1 : count -1;
                if (count < 1) {
                    ids.remove(i);
                    dbRef.child(CARTS_DB_URL + userId + "/" + Integer.toString(id)).removeValue();
                    return;
                }
                else counts.set(i, count);
                break;
            }
        }
        if (count == 0) {
            if (!isPlus) return;
            count = 1;
            ids.add(id);
            counts.add(count);
        }
        dbRef.child(CARTS_DB_URL + userId + "/" + Integer.toString(id)).setValue(count);
    }

    private void deleteCartContents() {
        dbRef.child(CARTS_DB_URL + userId).removeValue();
        ids = new ArrayList<>();
        counts = new ArrayList<>();
    }

    private void getOrders(DataSnapshot ds) {
        Long responseData =
                (Long) ds.child(ORDERS_DB_URL + userId + "/" + ORDERS_DB_COUNT_URL).getValue();
        if (responseData == null) orderCount = 0;
        else orderCount = responseData.intValue();
    }
}