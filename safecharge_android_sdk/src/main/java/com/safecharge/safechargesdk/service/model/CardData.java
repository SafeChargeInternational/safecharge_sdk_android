package com.safecharge.safechargesdk.service.model;

public class CardData {

    private String cardNumber;
    private String cardHolderName;
    private String expirationMonth;
    private String expirationYear;
    private String CVV;

    public CardData()
    {

    }

    public CardData(String cardNumber,
                    String cardHolderName,
                    String expirationMonth,
                    String expirationYear,
                    String CVV)
    {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.CVV = CVV;
    }


    public void setCardExpDatesFromString(String expdate) {
        int separatorIndex = expdate.indexOf("/");
        if( separatorIndex > -1 ) {
            this.expirationMonth = expdate.substring(0, separatorIndex);
            this.expirationYear = expdate.substring(separatorIndex + 1, expdate.length());
        } else {
            this.expirationMonth = "";
            this.expirationYear = "";
        }
    }


    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber.replaceAll("[^0-9]","");
    }

    public String getCardNumber()
    {
        return this.cardNumber;
    }

    public void setCardHolderName(String cardHolderName)
    {
        this.cardHolderName = cardHolderName;
    }

    public String getCardHolderName()
    {
        return cardHolderName;
    }

    public void setExpirationMonth(String expirationMonth)
    {
        this.expirationMonth = expirationMonth;
    }

    public String getExpirationMonth()
    {
        return this.expirationMonth;
    }

    public void setExpirationYear(String expirationYear)
    {
        this.expirationYear = expirationYear;
    }

    public String getExpirationYear()
    {
        return this.expirationYear;
    }

    public void setCVV(String CVV)
    {
        this.CVV = CVV;
    }

    public String getCVV()
    {
        return this.CVV;
    }

    @Override
    public String toString()
    {
        return "CardData [cardNumber = " + cardNumber +
                ", cardHolderName = " + cardHolderName +
                ", expirationMonth = " + expirationMonth +
                ", expirationYear = " + expirationYear +
                ", CVV = " + CVV;
    }

}