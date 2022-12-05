package com.transactionservice.transactionservicedeelaa.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class JSONFormatException extends RuntimeException {

    public JSONFormatException(String message) {
        super(message);
    }
}
