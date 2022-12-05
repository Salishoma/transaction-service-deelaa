package com.transactionservice.transactionservicedeelaa.dtos;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {
    private BigDecimal sum;
    private double avg;
    private BigDecimal max;
    private BigDecimal min;
    private long count;
}
