package com.transactionservice.transactionservicedeelaa.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private String accountId;

    private String email;

    private LocalDateTime createdDate;

    private BigDecimal amount;
}
