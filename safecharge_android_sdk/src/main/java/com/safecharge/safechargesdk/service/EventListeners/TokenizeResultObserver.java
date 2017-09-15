package com.safecharge.safechargesdk.service.EventListeners;

import com.safecharge.safechargesdk.service.model.ServiceError;
import com.safecharge.safechargesdk.service.model.CardTransactionResultModel;



public interface TokenizeResultObserver {
    void onSuccessfulResult( CardTransactionResultModel transactionResult );
    void onErrorResult( ServiceError error );
}
