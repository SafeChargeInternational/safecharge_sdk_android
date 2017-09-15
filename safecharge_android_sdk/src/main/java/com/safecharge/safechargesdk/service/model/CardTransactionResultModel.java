package com.safecharge.safechargesdk.service.model;

public class CardTransactionResultModel
{
    private String sessionToken;

    private String reason;

    private String status;

    private String ccTempToken;

    private String internalRequestId;

    private String errCode;

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

    public String getCcTempToken ()
    {
        return ccTempToken;
    }

    public void setCcTempToken (String ccTempToken)
    {
        this.ccTempToken = ccTempToken;
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
        return "CardTransactionResultModel [sessionToken = " + sessionToken +
                ", reason = " + reason +
                ", status = " + status +
                ", ccTempToken = " + ccTempToken +
                ", internalRequestId = " + internalRequestId +
                ", errCode = " + errCode +
                ", version = " + version +"]";
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