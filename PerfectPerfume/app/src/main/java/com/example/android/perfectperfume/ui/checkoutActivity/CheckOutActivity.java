package com.example.android.perfectperfume.ui.checkoutActivity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.perfectperfume.R;
import com.example.android.perfectperfume.utilities.PaymentHelper;

public class CheckOutActivity extends FragmentActivity implements CheckOutFragment.CheckOutFragmentCallbacks {

    private CheckOutFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PaymentHelper.PAYMENT_REQUEST_CODE) {
            fragment.deliverActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void setCheckOutFragment(CheckOutFragment fragment) {
        this.fragment = fragment;
    }
}
