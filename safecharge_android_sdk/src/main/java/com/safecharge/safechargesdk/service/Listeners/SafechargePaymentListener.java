package com.safecharge.safechargesdk.service.Listeners;

import com.safecharge.safechargesdk.service.model.CardTransactionResultModel;
import com.safecharge.safechargesdk.service.model.ServiceError;

public interface SafechargePaymentListener extends SafechargeBaseListener {
    void onTokenizeCard(CardTransactionResultModel cardTransactionResult);
    void onTokenizeCardError(ServiceError error);
}
