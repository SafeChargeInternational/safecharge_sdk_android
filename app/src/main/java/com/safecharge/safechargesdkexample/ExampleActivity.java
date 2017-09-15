package com.safecharge.safechargesdkexample;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.FragmentTransaction;
import android.app.FragmentManager;

import com.safecharge.safechargesdk.SafechargePaymentFragment;
import com.safecharge.safechargesdk.service.ServiceConstants;
import com.safecharge.safechargesdk.service.exceptions.*;
import com.safecharge.safechargesdk.service.model.AuthorizationRequest;
import com.safecharge.safechargesdk.service.Listeners.SafechargePaymentListener;
import com.safecharge.safechargesdk.service.Listeners.SafechargeFragmentListener;
import com.safecharge.safechargesdk.service.model.BillingAddress;
import com.safecharge.safechargesdk.service.model.CardTransactionResultModel;
import com.safecharge.safechargesdk.service.model.ServiceError;

public class ExampleActivity extends AppCompatActivity {

    protected AuthorizationRequest      m_safeChargeAuthRequest;
    protected SafechargePaymentFragment m_safeChargeFragment;
    protected BillingAddress            m_billingAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        try {
            m_safeChargeAuthRequest = new AuthorizationRequest("8912193623117089371",
                    "125823",
                    "111899",
                    "mY4QUnfnH2JO8cnSN6fm0aqRMsXOUDKu1Cx0im5eFzHOiGQ9WPbQaFopSgO1Vmp8");

            m_billingAddress = new BillingAddress("London","UK","1263","user@example.com","Harry","Potter","UK");
            m_safeChargeFragment = SafechargePaymentFragment.newInstance(this, m_safeChargeAuthRequest,m_billingAddress,"HarryPotter", ServiceConstants.IntegrationBaseURL);
        }
        catch (InvalidAuthorizationException e) {
            //issue with the provided authorization request data
        }
        catch (InvalidArgumentException e) {
            // There was an issue with your authorization string.
        }

        m_safeChargeFragment.addListener(new SafechargePaymentListener() {
            @Override
            public void onTokenizeCard(CardTransactionResultModel cardTransactionResult) {
                showSuccessDialog(cardTransactionResult);
            }

            @Override
            public void onTokenizeCardError(ServiceError error) {
                showErrorDialog(error);
            }
        });

        m_safeChargeFragment.addListener(new SafechargeFragmentListener() {
            @Override
            public void onFragmentClose() {
                // onFragmentClose is called after successful transaction on the same event loop
                System.out.println("onFragmentClose called");
            }
        });

        try {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, m_safeChargeFragment);
            fragmentTransaction.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void showErrorDialog(ServiceError error) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(error.getErrorDescription() + " error code: " + error.getErrorCode())
                .setCancelable(false)
                .setPositiveButton("ok", null).show();

    }

    private void showSuccessDialog(CardTransactionResultModel transactionResult) {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("credit card token id: " + transactionResult.getCcTempToken())
                .setCancelable(false)
                .setPositiveButton("ok", null).show();
    }

}
