package com.example.android.perfectperfume.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.CardRequirements;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import java.util.Arrays;

/* This class is built based on the following tutorial by Google:
 *  https://developers.google.com/pay/api/android/guides/tutorial
 *  By no means I wanted to copy-paste code, but there is no sense in tweaking it.
 *  As in the rest of this project I organised this code into into its separate class.*/

public class PaymentHelper {

    public static final int PAYMENT_REQUEST_CODE = 1;

    private Activity activity;
    private PaymentsClient paymentsClient;
    private PaymentCallbacks paymentCallbacks;

    public interface PaymentCallbacks {
        void isReadyToPay(boolean ready);
    }

    public PaymentHelper(Object object, Activity activity){
        try {
            paymentCallbacks = (PaymentCallbacks) object;
        } catch (ClassCastException e) {
            throw new ClassCastException(object.getClass() + "should implement PaymentCallbacks.");
        }
        paymentsClient = getPaymentsClientInstance(activity);
        this.activity = activity;
    }

    private PaymentsClient getPaymentsClientInstance(Context context) {
        return  Wallet.getPaymentsClient(context, new Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST).build());
    }

    private void isReadyToPay() {
        IsReadyToPayRequest request =
                IsReadyToPayRequest.newBuilder()
                        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                        .build();
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    public void onComplete(Task<Boolean> task) {
                        try {
                            boolean result = task.getResult(ApiException.class);
                            paymentCallbacks.isReadyToPay(result);
                        } catch (ApiException exception) {
                        }
                    }
                });
    }

    public void createPaymentRequest() {
        PaymentDataRequest request = createPaymentDataRequest();
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request),
                    activity,
                    PAYMENT_REQUEST_CODE);
        }
    }

    // As per the API documentation "example" is valid gateway name for testing
    private PaymentDataRequest createPaymentDataRequest() {
        PaymentDataRequest.Builder request =
                PaymentDataRequest.newBuilder()
                        .setTransactionInfo(
                                TransactionInfo.newBuilder()
                                        .setTotalPriceStatus(WalletConstants.
                                                TOTAL_PRICE_STATUS_FINAL)
                                        .setTotalPrice("10.00")
                                        .setCurrencyCode("USD")
                                        .build())
                        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                        .setCardRequirements(
                                CardRequirements.newBuilder()
                                        .addAllowedCardNetworks(
                                                Arrays.asList(
                                                        WalletConstants.CARD_NETWORK_AMEX,
                                                        WalletConstants.CARD_NETWORK_DISCOVER,
                                                        WalletConstants.CARD_NETWORK_VISA,
                                                        WalletConstants.CARD_NETWORK_MASTERCARD))
                                        .build());

        PaymentMethodTokenizationParameters params =
                PaymentMethodTokenizationParameters.newBuilder()
                        .setPaymentMethodTokenizationType(
                                WalletConstants.PAYMENT_METHOD_CARD)
                        .addParameter("gateway", "example")
                        .addParameter("gatewayMerchantId", "example_id")
                        .build();

        request.setPaymentMethodTokenizationParameters(params);
        return request.build();
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        final String RESULT_LOG_TAG = "PAYMENT_RESULT";
        if (requestCode == PAYMENT_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.d(RESULT_LOG_TAG, "OK");
                    PaymentData paymentData = PaymentData.getFromIntent(data);
                    String token = paymentData.getPaymentMethodToken().getToken();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d(RESULT_LOG_TAG, "CANCELED");
                    break;
                case AutoResolveHelper.RESULT_ERROR:
                    Log.d(RESULT_LOG_TAG, "ERROR");
                    Status status = AutoResolveHelper.getStatusFromIntent(data);
                    // Log the status for debugging.
                    // Generally, there is no need to show an error to
                    // the user as the Google Pay API will do that.
                    break;
                default:
                    Log.d(RESULT_LOG_TAG, "DEFAULT???");
                    // Do nothing.
            }
        }
    }
}
