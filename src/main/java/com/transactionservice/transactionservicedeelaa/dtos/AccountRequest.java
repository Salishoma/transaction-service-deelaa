package com.transactionservice.transactionservicedeelaa.dtos;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    private String description;
    private BigDecimal amount;
}
