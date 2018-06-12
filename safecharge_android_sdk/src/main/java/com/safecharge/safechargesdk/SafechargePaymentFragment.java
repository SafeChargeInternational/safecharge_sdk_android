package com.safecharge.safechargesdk;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.safecharge.safechargesdk.credit_card_utils.CreditCardValidator;
import com.safecharge.safechargesdk.credit_card_utils.ExpDateHelper;
import com.safecharge.safechargesdk.service.EventListeners.AuthenticateResultObserver;
import com.safecharge.safechargesdk.service.EventListeners.TokenizeResultObserver;
import com.safecharge.safechargesdk.service.SafechargeService;
import com.safecharge.safechargesdk.service.model.AuthorizationRequest;
import com.safecharge.safechargesdk.service.exceptions.InvalidArgumentException;
import com.safecharge.safechargesdk.service.exceptions.InvalidAuthorizationException;

import com.safecharge.safechargesdk.service.Listeners.SafechargeBaseListener;
import com.safecharge.safechargesdk.service.Listeners.SafechargePaymentListener;
import com.safecharge.safechargesdk.service.Listeners.SafechargeFragmentListener;
import com.safecharge.safechargesdk.service.model.AuthorizationResponseData;
import com.safecharge.safechargesdk.service.model.BillingAddress;
import com.safecharge.safechargesdk.service.model.CardData;
import com.safecharge.safechargesdk.service.model.CardTransactionModel;
import com.safecharge.safechargesdk.service.model.CardTransactionResultModel;
import com.safecharge.safechargesdk.service.model.ServiceError;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;


public class SafechargePaymentFragment extends Fragment {

    public static final String FRAGMENT_TAG = "com.safecharge.safechargesdk.SafechargePaymentFragment";


    private static final String EXTRA_AUTHORIZATION_DATA = "com.safecharge.safechargesdk.EXTRA_AUTHORIZATION_DATA";
    private static final String EXTRA_BILLING_DATA = "com.safecharge.safechargesdk.EXTRA_BILLING_DATA";
    private static final String EXTRA_CARDHOLDER_NAME = "com.safecharge.safechargesdk.EXTRA_CARDHOLDER_NAME";
    private static final String EXTRA_ENVIRONMENT = "com.safecharge.safechargesdk.EXTRA_ENVIRONMENT";

    protected Context                       m_context;
    protected AuthorizationRequest          m_authRequestData;
    protected BillingAddress                m_billingData;
    protected String                        m_cardholderName;
    protected String                        m_environment;
    protected SafechargePaymentListener     m_safechargePaymentListener;
    protected SafechargeFragmentListener    m_safechargeFragmentListener;

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

    private static int SCAN_REQUEST_CODE = 0x0001;

    public SafechargePaymentFragment() {
    }

    /**
     * Creates new instance of SafechargePaymentFragment
     * @param activity
     * @param auth
     * @param billingAddress
     * @param cardholderName
     * @param environment
     * @return SafechargePaymentFragment
     * @throws InvalidArgumentException
     * @throws InvalidAuthorizationException
     */

    public static SafechargePaymentFragment newInstance(@NonNull Activity activity,
                                                        @NonNull AuthorizationRequest auth,
                                                        @Nullable BillingAddress billingAddress,
                                                        @NonNull String cardholderName,
                                                        @NonNull String environment)
            throws InvalidArgumentException, InvalidAuthorizationException {
        if (activity == null) {
            throw new InvalidArgumentException("Activity is null");
        }
        if (auth == null) {
            throw new InvalidArgumentException("AuthorizationRequest is null");
        }
        if (cardholderName == null) {
            throw new InvalidArgumentException("CardholderName is null");
        }
        if (cardholderName.length() == 0) {
            throw new InvalidArgumentException("CardholderName length 0");
        }
        if (environment == null || cardholderName.length() == 0) {
            throw new InvalidArgumentException("Environment not specified");
        }

        try {
            auth.checkIntegrity();
        } catch (InvalidAuthorizationException exception) {
            throw exception;
        }

        FragmentManager fragManager = activity.getFragmentManager();

        SafechargePaymentFragment safechargePaymentFragment = (SafechargePaymentFragment) fragManager.findFragmentByTag(FRAGMENT_TAG);
        if (safechargePaymentFragment == null) {
            safechargePaymentFragment = new SafechargePaymentFragment();
            Bundle bundle = new Bundle();
            try {

                if( billingAddress != null ) { //opt
                    bundle.putParcelable(EXTRA_BILLING_DATA,billingAddress);
                }

                if( cardholderName != null ) { //opt
                    bundle.putString(EXTRA_CARDHOLDER_NAME,cardholderName);
                }

                bundle.putParcelable(EXTRA_AUTHORIZATION_DATA, auth); //req
                bundle.putString(EXTRA_ENVIRONMENT,environment);

            } catch (RuntimeException exception) {
                System.out.println("runtime exception : " + exception.toString());
                return null; //or rethrow
            }
            safechargePaymentFragment.setArguments(bundle);
        }
        safechargePaymentFragment.m_context = activity.getApplicationContext();
        return safechargePaymentFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View fragmentView = inflater.inflate(R.layout.content_payment,container, false);

        this.initializeCreditCardField(fragmentView);
        this.initializeExpDateField(fragmentView);
        this.initializeCVVField(fragmentView);
        this.initializeButtons(fragmentView);

        m_payButton = (Button) fragmentView.findViewById(R.id.button);
        m_creditCardText.requestFocus();

        return fragmentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (m_context == null) {
            m_context = getActivity().getApplicationContext();
        }

        bindInputData();
    }



    private void bindInputData() {
        m_authRequestData = getArguments().getParcelable(EXTRA_AUTHORIZATION_DATA);
        m_billingData = getArguments().getParcelable(EXTRA_BILLING_DATA);
        m_cardholderName = getArguments().getString(EXTRA_CARDHOLDER_NAME);
        m_environment = getArguments().getString(EXTRA_ENVIRONMENT);

        m_cardInfo = new CardData();
        m_cardInfo.setCardHolderName(m_cardholderName);
        this.m_safechargeService = new SafechargeService(m_environment);

    }

    protected boolean isBacspacePressed(int start,int end, int count) {
        return (count > 0) ? false : true;
    }

    private void initializeCVVField(View fragmentView)
    {
        m_cvvField = (EditText) fragmentView.findViewById(R.id.editText3);
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
                        m_cardInfo.setCVV(m_cvvField.getText().toString());

                    } else if (isBacspacePressed(start,end,count)) {
                        m_cardInfo.setCVV("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void initializeCreditCardField(View fragmentView)
    {
        m_creditCardText = (EditText) fragmentView.findViewById(R.id.editText);
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

                    if ( isBacspacePressed(start,end,count) ) {
                        m_cardInfo.setCardNumber("");
                    } else if ( valid && classResult.cardType != CreditCardValidator.CardType.None ) {
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

        m_cardImageView = (ImageView) fragmentView.findViewById(R.id.imageView);
    }

    private void initializeExpDateField(View fragmentView)
    {
        m_expDateHelper = new ExpDateHelper();
        m_expDateText = (EditText) fragmentView.findViewById(R.id.editText4);
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
                    } else if (isBacspacePressed(start,end,count)) {
                        m_cardInfo.setCardExpDatesFromString("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initializeButtons(View fragmentView) {
        Button payButton = (Button) fragmentView.findViewById(R.id.button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPayPress(view);
            }
        });

        Button scanButton = (Button) fragmentView.findViewById(R.id.button2);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScanPress(view);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttach(getActivity());
    }

    /**
     * Adds payment listener
     * @param listener
     * @param <T>
     */

    public <T extends SafechargeBaseListener> void addListener(T listener) {

        if(listener instanceof  SafechargePaymentListener) {
            m_safechargePaymentListener = (SafechargePaymentListener) listener;
        } else if(listener instanceof  SafechargeFragmentListener) {
            m_safechargeFragmentListener = (SafechargeFragmentListener) listener;
        } else {
            //not supported interface
            assert(false);
        }
    }

    protected void onPayPress(View v) {
        m_authRequestData.postBuild();
        m_safechargeService.authenticate(m_authRequestData, new AuthenticateResultObserver() {
            @Override
            public void onSuccessfulResult(AuthorizationResponseData sessionAuthModel) {
                tokenizeCard(sessionAuthModel);
            }

            @Override
            public void onErrorResult(ServiceError error)
            {
                if( m_safechargePaymentListener != null ) {
                    m_safechargePaymentListener.onTokenizeCardError(error);
                }
            }
        });
    }

    protected void tokenizeCard(AuthorizationResponseData sessionAuthModel) {
        CardTransactionModel model = new CardTransactionModel(sessionAuthModel.getSessionToken(),
                sessionAuthModel.getMerchantSiteId());
        model.setCardData(m_cardInfo);
        model.setBillingAddress(m_billingData);

        m_safechargeService.tokenizeCard(model, new TokenizeResultObserver() {
            @Override
            public void onSuccessfulResult(CardTransactionResultModel transactionResult) {
                if( m_safechargePaymentListener != null ) {
                    m_safechargePaymentListener.onTokenizeCard(transactionResult);
                }

                if( m_safechargeFragmentListener != null ) {
                    m_safechargeFragmentListener.onFragmentClose();
                }
            }

            @Override
            public void onErrorResult(ServiceError error) {
                if( m_safechargePaymentListener != null ) {
                    m_safechargePaymentListener.onTokenizeCardError(error);
                }
            }
        });
    }

    protected void onScanPress(View v) {
        Intent scanIntent = new Intent(this.getActivity(), CardIOActivity.class);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
