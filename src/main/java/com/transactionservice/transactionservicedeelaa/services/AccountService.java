package com.transactionservice.transactionservicedeelaa.services;

import com.transactionservice.transactionservicedeelaa.dtos.*;

import java.util.List;

public interface AccountService {
    APIResponse<AccountResponse> createAccount(String email, AccountRequest accountRequest);
    APIResponse<AccountResponse> findAccountByEmail(String email);
    APIResponse<List<String>> getPaymentTrackerKeys(String email);
    APIResponse<PayStackResponse> initTransaction(String email, PayStackPaymentRequest request) throws Exception;
    APIResponse<VerifyTransactionResponse> verifyPayment(String email, String reference);
}
