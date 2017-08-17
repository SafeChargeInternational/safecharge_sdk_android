# SafeCharge Android SDK for Java.

SafeCharge's Android SDK provides payment activity and fragment in order for any given app to be able to process consumer payments through SafeCharge’s payment gateway.
 
## Requirements

Android API >= 16

### Gradle

Add safecharge_android_sdk package in the module dependency:

dependencies {  
    compile 'com.safecharge:safecharge_android_sdk:1.+'
}

## Initialization

You will need to obtain MerchantId, merchantSiteId, clientRequestId and secretKey via your Safecharge merchant account.

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
	
    try {		
	  mSafeChargeAuthRequest = new AuthorizationRequest("#MerchantId#","#merchantSiteId#","#clientRequestId#","#secretKey#");
																												
      mSafeChargeFragment = mSafeChargeFragment.newInstance(this, mSafeChargeAuthRequest);
    }
	catch (InvalidAuthorizationException e) {
		//issue with the provided authorization request data
	} 
	catch (InvalidArgumentException e) {
      // There was an issue with your authorization string.
    }
}

## Register Listeners 

mSafeChargeFragment.addPaymentListener(new SafechargePaymentListener() {

	@Override 
	public void onTokenizeCard(CardTransactionResultModel cardTransactionResult) {
		//everything you need
	}

	public void onTokenizeCardError(ServiceError error) {
		//service error can be handled here
	}
});

mSafeChargeFragment.addFragmentListener(new SafechargeFragmentListener() {
	@Override 
	public void onFragmentClose() {
		
	}	
});

## ProGuard
A ProGuard configuration is provided as part of Safecharge Android SDK. There is no need to add any specific rules to your ProGuard configuration.


	
	
	

