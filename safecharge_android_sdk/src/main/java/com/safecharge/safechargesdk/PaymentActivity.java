package com.safecharge.safechargesdk;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;


import com.safecharge.safechargesdk.credit_card_utils.CreditCardValidator;
import com.safecharge.safechargesdk.credit_card_utils.ExpDateHelper;
import com.safecharge.safechargesdk.service.EventListeners.AuthenticateResultObserver;
import com.safecharge.safechargesdk.service.EventListeners.TokenizeResultObserver;
import com.safecharge.safechargesdk.service.model.AuthorizationResponseData;
import com.safecharge.safechargesdk.service.model.CardTransactionModel;
import com.safecharge.safechargesdk.service.model.CardTransactionResultModel;
import com.safecharge.safechargesdk.service.model.ServiceError;

import com.safecharge.safechargesdk.service.model.CardData;
import com.safecharge.safechargesdk.service.SafechargeService;
import com.safecharge.safechargesdk.service.ServiceConstants;
import com.safecharge.safechargesdk.service.model.AuthorizationRequest;


public class PaymentActivity extends AppCompatActivity {

    private static int SCAN_REQUEST_CODE = 0x0001;

    private EditText m_creditCardText = null;
    private boolean  m_creditCardUpdateFlag = false;
    private ImageView m_cardImageView = null;

    private EditText m_expDateText = null;
    private boolean  m_expDateUpdateFlag = false;
    private ExpDateHelper m_expDateHelper = null;
    private CreditCardValidator.CardType m_enteredCardType = CreditCardValidator.CardType.None;

    private EditText m_cvvField = null;
    private boolean m_cvvUpdateFlag = false;

    private Button m_payButton = null;


    private CardData m_cardInfo = null;

    private SafechargeService m_safechargeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        m_cardInfo = new CardData();

        this.initializeCreditCardField();
        this.initializeExpDateField();
        this.initializeCVVField();

        m_payButton = (Button) this.findViewById(R.id.button);


        this.m_safechargeService = new SafechargeService(ServiceConstants.IntegrationBaseURL);
        m_creditCardText.requestFocus();
    }



    private void initializeCVVField()
    {
        m_cvvField = (EditText) this.findViewById(R.id.editText3);
        m_cvvField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int end, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int end, int count) {
                if (!m_cvvUpdateFlag) {
                    final int maxLength = m_enteredCardType.getCardCVVLength();
                    if (charSequence.length() >= maxLength) {
                        String clamped = charSequence.toString().substring(0, maxLength);
                        m_cvvUpdateFlag = true;
                        m_cvvField.setText(clamped);
                        m_cvvField.setSelection(clamped.length());
                        m_cvvUpdateFlag = false;

                        m_cvvField.clearFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void initializeCreditCardField()
    {
        m_creditCardText = (EditText) this.findViewById(R.id.editText);
        m_creditCardText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int end, int count) {
                if ( !m_creditCardUpdateFlag ) {

                    String creditCardString = charSequence.toString();
                    String formatted = CreditCardValidator.formatBy4(creditCardString);

                    m_creditCardUpdateFlag = true;

                    m_creditCardText.setText(formatted);
                    m_creditCardText.setSelection(formatted.length());
                    m_creditCardUpdateFlag = false;

                    CreditCardValidator.CheckCardReturnType classResult = CreditCardValidator.checkCardType(formatted);
                    boolean valid = CreditCardValidator.luhnCheck(formatted);

                    this.updateImageView(classResult.cardType,classResult.exactMatch,valid);
                    m_enteredCardType = classResult.cardType;

                    if ( valid && classResult.cardType != CreditCardValidator.CardType.None ) {
                        m_cardInfo.setCardNumber(m_creditCardText.getText().toString());
                        m_expDateText.requestFocus();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }

            private void updateImageView(CreditCardValidator.CardType cardType,
                                         boolean exactMatch,
                                         boolean luhnValid) {
                switch (cardType) {
                    case Amex:
                        m_cardImageView.setImageResource(R.drawable.americanexpress);
                        break;
                    case DinersClubInternational:
                        m_cardImageView.setImageResource(R.drawable.dinersclubinternational);
                        break;
                    case DiscoverCard:
                        m_cardImageView.setImageResource(R.drawable.discover);
                        break;
                    case EntropayMasterCard:
                        m_cardImageView.setImageResource(R.drawable.entropymaster);
                        break;
                    case EntropayVisa:
                        m_cardImageView.setImageResource(R.drawable.entropyvisa);
                        break;
                    case Jcb:
                        m_cardImageView.setImageResource(R.drawable.jcb);
                        break;
                    case Maestro:
                        m_cardImageView.setImageResource(R.drawable.maestro);
                        break;
                    case MasterCard:
                        m_cardImageView.setImageResource(R.drawable.mastercard);
                        break;
                    case Visa:
                        m_cardImageView.setImageResource(R.drawable.visa);
                        break;
                    default:
                        m_cardImageView.setImageResource(R.drawable.unknownnumber);
                        break;
                };
            }
        });

        m_cardImageView = (ImageView) findViewById(R.id.imageView);
    }

    private void initializeExpDateField()
    {
        m_expDateHelper = new ExpDateHelper();
        m_expDateText = (EditText) this.findViewById(R.id.editText4);
        m_expDateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int end, int count) {
                if (!m_expDateUpdateFlag) {
                    boolean complete = m_expDateHelper.updateBuffer(charSequence.toString(), start, end, count);

                    m_expDateUpdateFlag = true;
                    m_expDateText.setText(m_expDateHelper.getBufferString());
                    m_expDateText.setSelection(m_expDateHelper.getBufferString().length());
                    m_expDateUpdateFlag = false;

                    if ( complete ) {
                        m_cardInfo.setCardExpDatesFromString(m_expDateText.getText().toString());
                        m_cvvField.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPayPress(View v) {
        AuthorizationRequest authModel = new AuthorizationRequest("8912193623117089371",
                "125823","111899",
                "mY4QUnfnH2JO8cnSN6fm0aqRMsXOUDKu1Cx0im5eFzHOiGQ9WPbQaFopSgO1Vmp8");

        m_safechargeService.authenticate(authModel, new AuthenticateResultObserver() {
            @Override
            public void onSuccessfulResult(AuthorizationResponseData sessionAuthModel) {
                tokenizeCard(sessionAuthModel);
            }

            @Override
            public void onErrorResult(ServiceError error) {
                showErrorDialog(error);
            }
        });
    }

    public void tokenizeCard(AuthorizationResponseData sessionAuthModel) {
        CardTransactionModel model = new CardTransactionModel(sessionAuthModel.getSessionToken(),sessionAuthModel.getMerchantSiteId());

        m_safechargeService.tokenizeCard(model, new TokenizeResultObserver() {
            @Override
            public void onSuccessfulResult(CardTransactionResultModel transactionResult) {
                showSuccessDialog(transactionResult);
            }

            @Override
            public void onErrorResult(ServiceError error) {
                showErrorDialog(error);
            }
        });
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

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY,true);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY,true);
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO,true);
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON,false);

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
        }
    }
}
