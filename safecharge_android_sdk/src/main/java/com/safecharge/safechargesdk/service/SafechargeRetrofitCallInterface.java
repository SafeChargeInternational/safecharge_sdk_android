package com.safecharge.safechargesdk.service;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import com.safecharge.safechargesdk.service.model.AuthorizationRequest;
import com.safecharge.safechargesdk.service.model.AuthorizationResponseData;

import com.safecharge.safechargesdk.service.model.CardTransactionModel;
import com.safecharge.safechargesdk.service.model.CardTransactionResultModel;

public interface SafechargeRetrofitCallInterface {
    @POST("/ppp/api/v1/getSessionToken.do")
    Call<AuthorizationResponseData> authenticate(@Body AuthorizationRequest sessionAuth);

    @POST("/ppp/api/cardTokenization.do")
    Call<CardTransactionResultModel> tokenizeCard(@Body CardTransactionModel cardTransaction);
}
