package com.transactionservice.transactionservicedeelaa.validators;

import java.time.Instant;

public class DateValidator {

    public static Instant getInstantTime(String dateString) {
        try {
            return Instant.parse(dateString);
        } catch (Exception ex) {
            return null;
        }
    }
}
