package com.safecharge.safechargesdk.service.model;

public class ServiceError {

    private String errorDescription;
    private String errorCode;


    public ServiceError(String errorDescription,
                        String errorCode) {
        this.errorDescription = errorDescription;
        this.errorCode = errorCode;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
    public String getErrorDescription() {
        return this.errorDescription;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String toString() {
        return "errorDesctiption : " + this.errorDescription +
                ",errorCode : " + this.errorCode;
    }


}
