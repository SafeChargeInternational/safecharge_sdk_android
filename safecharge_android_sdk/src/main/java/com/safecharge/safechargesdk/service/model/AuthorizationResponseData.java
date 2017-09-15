package com.safecharge.safechargesdk.service.model;

public class AuthorizationResponseData
{
    private String sessionToken;

    private String reason;

    private String status;

    private String internalRequestId;

    private String errCode;

    private String clientRequestId;

    private String merchantId;

    private String merchantSiteId;

    private String version;

    public String getSessionToken ()
    {
        return sessionToken;
    }

    public void setSessionToken (String sessionToken)
    {
        this.sessionToken = sessionToken;
    }

    public String getReason ()
    {
        return reason;
    }

    public void setReason (String reason)
    {
        this.reason = reason;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getInternalRequestId ()
    {
        return internalRequestId;
    }

    public void setInternalRequestId (String internalRequestId)
    {
        this.internalRequestId = internalRequestId;
    }

    public String getErrCode ()
    {
        return errCode;
    }

    public void setErrCode (String errCode)
    {
        this.errCode = errCode;
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

    public String getMerchantSiteId ()
    {
        return merchantSiteId;
    }

    public void setMerchantSiteId (String merchantSiteId)
    {
        this.merchantSiteId = merchantSiteId;
    }

    public String getVersion ()
    {
        return version;
    }

    public void setVersion (String version)
    {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return "AuthorizationResponseData [sessionToken = "+sessionToken+", reason = "+reason+", status = "+status+", internalRequestId = "+internalRequestId+", errCode = "+errCode+", clientRequestId = "+clientRequestId+", merchantId = "+merchantId+", merchantSiteId = "+merchantSiteId+", version = "+version+"]";
    }



    public boolean isError() {
        return (this.getStatus().compareToIgnoreCase("SUCCESS") != 0);
    }

    public ServiceError checkAndReturnError() {

        if (this.isError() == false ){
            return null;
        }

        return new ServiceError(this.getReason(),this.getErrCode());

    }


}