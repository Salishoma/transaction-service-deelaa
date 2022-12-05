package com.transactionservice.transactionservicedeelaa.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidFormatException extends RuntimeException{

    public InvalidFormatException(String message) {
        super(message);
    }
}
