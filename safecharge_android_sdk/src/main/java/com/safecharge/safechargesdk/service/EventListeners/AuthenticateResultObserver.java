package com.safecharge.safechargesdk.service.EventListeners;

import com.safecharge.safechargesdk.service.model.ServiceError;
import com.safecharge.safechargesdk.service.model.AuthorizationResponseData;

public interface AuthenticateResultObserver {

    void onSuccessfulResult(AuthorizationResponseData sessionAuthResult);
    void onErrorResult(ServiceError error);
}
