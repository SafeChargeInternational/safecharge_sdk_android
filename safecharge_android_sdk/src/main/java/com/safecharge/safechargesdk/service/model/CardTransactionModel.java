package com.safecharge.safechargesdk.service.model;


public class CardTransactionModel
{
    private String sessionToken;
    private String merchantSiteId;
    private String environment;

    private BillingAddress billingAddress = null;
    private CardData       cardData = null;

    public CardTransactionModel(String sessionToken,
                                String merchantSiteId) {
        this.sessionToken = sessionToken;
        this.merchantSiteId = merchantSiteId;
    }

    public CardTransactionModel(String sessionToken,
                                String merchantSiteId,
                                BillingAddress billingAddress,
                                CardData cardData) {

        this.sessionToken = sessionToken;
        this.merchantSiteId = merchantSiteId;
        this.billingAddress = billingAddress;
        this.cardData = cardData;
    }


    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
    public String getSessionToken() {
        return this.sessionToken;
    }


    public void setMerchantSiteId(String merchantSiteId) {
        this.merchantSiteId = merchantSiteId;
    }
    public String getMerchantSiteId() {
        return this.merchantSiteId;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    public String getEnvironment() {
        return environment;
    }

    public void setBillingAddress(BillingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }
    public BillingAddress getBillingAddress() {
        return this.billingAddress;
    }

    public void setCardData(CardData cardData) {
        this.cardData = cardData;
    }
    public CardData getCardData() {
        return this.cardData;
    }


}