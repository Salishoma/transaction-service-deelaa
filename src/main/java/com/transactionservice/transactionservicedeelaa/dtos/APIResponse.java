package com.transactionservice.transactionservicedeelaa.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class APIResponse<T> {
    private Boolean status;
    private String message;
    private int statusCode;
    private T data;
}
