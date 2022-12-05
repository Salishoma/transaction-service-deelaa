package com.transactionservice.transactionservicedeelaa.validators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.transactionservice.transactionservicedeelaa.dtos.TransactionRequest;

public class JSONValidator {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static boolean isValidJSON(TransactionRequest request) {
        try {
            Gson gson = new Gson();
            String requestString = gson.toJson(request);
            mapper.readTree(requestString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
