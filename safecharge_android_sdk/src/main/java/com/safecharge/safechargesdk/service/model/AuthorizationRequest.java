package com.safecharge.safechargesdk.service.model;


import androidx.annotation.NonNull;
import androidx.annotation.Size;

import java.security.MessageDigest;

import android.os.Parcel;
import android.os.Parcelable;

import com.safecharge.safechargesdk.service.ServiceConstants;
import com.safecharge.safechargesdk.service.exceptions.InvalidAuthorizationException;


public class AuthorizationRequest implements Parcelable {

    public AuthorizationRequest(String merchantId,
                                String merchantSiteId,
                                String clientRequestId,
                                String secretKey) {
        this.merchantId = merchantId;
        this.merchantSiteId = merchantSiteId;
        this.clientRequestId = clientRequestId;
        this.secretKey = secretKey;
    }


    @NonNull
    public static String SHA256(String base) throws RuntimeException {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void checkIntegrity() throws InvalidAuthorizationException
    {
        if ( merchantId == null || merchantId.length() == 0 ) {
            throw new InvalidAuthorizationException("merchantId is either null or empty");
        }
        if ( merchantSiteId == null || merchantSiteId.length() == 0 ) {
            throw new InvalidAuthorizationException("merchantSiteId is either null or empty");
        }
        if ( clientRequestId == null || clientRequestId.length() == 0 ) {
            throw new InvalidAuthorizationException("clientRequestId is either null or empty");
        }
        if ( secretKey == null || secretKey.length() == 0 ) {
            throw new InvalidAuthorizationException("secretKey is either null or empty");
        }
    }

    public void postBuild()  {
        this.timeStamp = android.text.format.DateFormat.format("yyyyMMddhhmmss", new java.util.Date()).toString();
        try {
            this.checksum = SHA256(new String()
                    .concat(this.merchantId)
                    .concat(this.merchantSiteId)
                    .concat(this.clientRequestId)
                    .concat(this.timeStamp)
                    .concat(this.secretKey));
        } catch (RuntimeException checkSumException) {
            System.out.print(checkSumException.toString());
        }
    }

    @Size(max = ServiceConstants.MERCHANT_ID_MAX_LENGTH)        private String merchantId;
    @Size(max = ServiceConstants.MERCHANT_SITE_ID_MAX_LENGTH)   private String merchantSiteId;
    @Size(max = ServiceConstants.CLIENT_REQUEST_ID_MAX_LENGTH)  private String clientRequestId;
    @Size(max = ServiceConstants.USER_ID_MAX_LENGTH)            private String userToken;
    @Size(max = ServiceConstants.SECRECT_KEY_MAX_LENGTH)        private String secretKey;

    private String checksum;
    private String timeStamp;


    public String getSecretKey()
    {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }

    public String getUserToken()
    {
        return this.userToken;
    }

    public void setUserToken(String userToken)
    {
        this.userToken = userToken;
    }


    public String getTimeStamp ()
    {
        return timeStamp;
    }

    public void setTimeStamp (String timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public String getClientRequestId ()
    {
        return clientRequestId;
    }

    public void setClientRequestId (String clientRequestId)
    {
        this.clientRequestId = clientRequestId;
    }

    public String getMerchantId ()
    {
        return merchantId;
    }

    public void setMerchantId (String merchantId)
    {
        this.merchantId = merchantId;
    }

    public String getChecksum ()
    {
        return checksum;
    }

    public void setChecksum (String checksum)
    {
        this.checksum = checksum;
    }

    public String getMerchantSiteId ()
    {
        return merchantSiteId;
    }

    public void setMerchantSiteId (String merchantSiteId)
    {
        this.merchantSiteId = merchantSiteId;
    }

    @Override
    public String toString()
    {
        return "AuthorizationRequest [timeStamp = " + timeStamp +
                ", clientRequestId = " + clientRequestId +
                ", merchantId = " + merchantId +
                ", checksum = " + checksum +
                ", merchantSiteId = " + merchantSiteId + "]";
    }

    protected AuthorizationRequest(Parcel in) {
        merchantId = in.readString();
        merchantSiteId = in.readString();
        clientRequestId = in.readString();
        userToken = in.readString();
        secretKey = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(merchantId);
        dest.writeString(merchantSiteId);
        dest.writeString(clientRequestId);
        dest.writeString(userToken);
        dest.writeString(secretKey);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AuthorizationRequest> CREATOR = new Parcelable.Creator<AuthorizationRequest>() {
        @Override
        public AuthorizationRequest createFromParcel(Parcel in) {
            return new AuthorizationRequest(in);
        }

        @Override
        public AuthorizationRequest[] newArray(int size) {
            return new AuthorizationRequest[size];
        }
    };
}