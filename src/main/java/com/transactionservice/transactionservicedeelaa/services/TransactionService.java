package com.transactionservice.transactionservicedeelaa.services;

import com.transactionservice.transactionservicedeelaa.dtos.APIResponse;
import com.transactionservice.transactionservicedeelaa.dtos.Statistics;
import com.transactionservice.transactionservicedeelaa.dtos.TransactionRequest;

public interface TransactionService {
    APIResponse<String> makePayment(TransactionRequest request, String email);

    APIResponse<Statistics> getStatistics(String email);

    APIResponse<?> deleteTransactions(String email);
}
